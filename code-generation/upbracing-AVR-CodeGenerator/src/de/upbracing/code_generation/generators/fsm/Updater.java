package de.upbracing.code_generation.generators.fsm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import statemachine.FinalState;
import statemachine.InitialState;
import statemachine.NormalState;
import statemachine.Region;
import statemachine.State;
import statemachine.StateParent;
import statemachine.StateWithActions;
import statemachine.SuperState;
import statemachine.Transition;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.ContextItem;
import de.upbracing.code_generation.common.Times;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.StatemachinesConfigProvider;
import de.upbracing.code_generation.fsm.model.Action;
import de.upbracing.code_generation.fsm.model.ActionType;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.fsm.model.StateVariable;
import de.upbracing.code_generation.fsm.model.StateVariablePurposes;
import de.upbracing.code_generation.fsm.model.TransitionInfo;

public class Updater {
	private Messages messages;

	public Updater(CodeGeneratorConfigurations config) {
		this.messages = config.getMessages();
	}

	public Object updateConfig(CodeGeneratorConfigurations config) {
		ContextItem context = messages.pushContext("updateConfig");
		
		updateParents(config);
		assignNames(config);
		collapseFinalStates(config);
		addStateVariables(config);
		convertWaitToActionsAndConditions(config);
		
		context.pop();
		
		return null;
	}

	private void updateParents(CodeGeneratorConfigurations config) {
		// Unfortunately, the parents of most elements are not set
		// automatically. We correct this here. The information
		// remains valid because the structure of the statemachine
		// won't change anymore.

		for (StateMachineForGeneration smg : StatemachinesConfigProvider.get(config)) {
			updateParents(smg.getStates(), smg.getStateMachine());
		}
	}

	private void updateParents(Iterable<State> states, StateParent parent) {
		for (State state : states) {
			state.setParent(parent);

			if (state instanceof SuperState) {
				SuperState superstate = (SuperState) state;
				for (Region region : superstate.getRegions()) {
					region.setParent(superstate);

					updateParents(region.getStates(), region);
				}
			}
		}
	}

	// remove final states

	// Make sure that there is at most one final state in each region
	// and top-level of a statemachine. If there is more than one final
	// state, remove it and update the transitions accordingly. There
	// can be more than one final state in a statemachine (e.g. one is
	// in a region and the other one on the top-level), so be careful
	// to set each transition to the right one.
	
	
	private void collapseFinalStates(CodeGeneratorConfigurations config) {
		for (StateMachineForGeneration smg : StatemachinesConfigProvider.get(config)) {
			collapseFinalStates(smg.getStateMachine());
		}
	}

	private void collapseFinalStates(StateParent parent) {
		// collect final states
		List<FinalState> final_list = new ArrayList<FinalState>();
		for (State state : parent.getStates()) {
			if (state instanceof FinalState) {
				final_list.add((FinalState) state);
			}
		}
		
		// collapse them into one final state, if we have more than one
		if (final_list.size() > 1)
			collapseFinalStates(final_list);
		
		// do the same for children
		for (State state : parent.getStates()) {
			if (state instanceof SuperState) {
				SuperState superstate = (SuperState) state;
				for (Region region : superstate.getRegions()) {
					collapseFinalStates(region);
				}
			}
		}
	}

	private void collapseFinalStates(List<FinalState> states) {
		// copy the list to avoid ConcurrentModificationException
		states = new ArrayList<FinalState>(states);
		
		// choose one state that will not be removed
		FinalState surviving_state = states.get(0);
		
		// remove all other states
		for (FinalState state : states) {
			// do not remove the surviving state
			if (state == surviving_state)
				continue;
			
			// rewrite all transitions to point to the surviving state instead of the removed states
			// Again, we have to copy the list to avoid exceptions.
			List<Transition> incomingTransitions = new ArrayList<Transition>(
					state.getIncomingTransitions());
			for (Transition transition : incomingTransitions)
				transition.setDestination(surviving_state);
	
			// remove the state
			state.getParent().getStates().remove(state);
		}
	}

	// assign names to unnamed states and regions
	
	private int assignNamesCounter;
	private String getNextName(String prefix) {
		return prefix + Integer.toHexString(++assignNamesCounter).toUpperCase();
	}
	
	private void assignNames(CodeGeneratorConfigurations config) {
		for (StateMachineForGeneration smg : StatemachinesConfigProvider.get(config)) {
			assignNamesCounter = 0;
			
			assignNames(smg.getStateMachine());
		}
	}

	private void assignNames(StateParent state_parent) {
		if (state_parent instanceof SuperState) {
			for (Region region : ((SuperState) state_parent).getRegions()) {
				if (emptyOrNull(region.getName()))
					region.setName(getNextName("region"));
				
				assignNames(region);
			}
		} else {
			for (State state : state_parent.getStates())
				assignNames(state);
		}
	}

	private void assignNames(State state) {
		String name = state.getName();
		
		if (emptyOrNull(name)) {
			if (state instanceof InitialState)
				((InitialState) state).setName(getNextName("initial"));
	
			else if (state instanceof FinalState)
				((FinalState) state).setName(getNextName("final"));
	
			else if (state instanceof NormalState)
				((NormalState) state).setName(getNextName("state"));
	
			else if (state instanceof SuperState)
				((SuperState) state).setName(getNextName("superstate"));
			
			else
				throw new IllegalArgumentException("unexpected type");
		}
		
		if (state instanceof StateParent)
			assignNames((StateParent)state);
	}

	public static boolean emptyOrNull(String obj) {
		return obj == null || obj.isEmpty();
	}

	private void addStateVariables(CodeGeneratorConfigurations config) {
		for (StateMachineForGeneration smg : StatemachinesConfigProvider.get(config)) {
			addStateVariables(smg, smg.getStateMachine());
		}
	}

	private void addStateVariables(StateMachineForGeneration smg,
			StateParent parent) {
		addStateVariable(smg, parent);

		for (State state : parent.getStates())
			if (state instanceof SuperState)
				for (Region region : ((SuperState) state).getRegions())
					addStateVariables(smg, region);
	}

	private void addStateVariable(StateMachineForGeneration smg,
			StateParent parent) {
		StringBuilder declaration = new StringBuilder();
		declaration.append("enum {\n");
		for (State state : smg.sortStatesForEnum(parent.getStates())) {
			if (state instanceof InitialState)
				continue;
			declaration.append("\t");
			declaration.append(smg.stateName(state));
			declaration.append(",\n");
		}
		declaration.append("}");

		StateVariable var = new StateVariable("state",
				StateVariable.TYPE_AUTONAME, declaration.toString(), parent);
		smg.getStateVariables().add(var, StateVariablePurposes.STATE, parent);
	}

	/**
	 * wait events can be implemented with actions and conditions. This method
	 * does the conversion so the code generator doesn't have to deal with this.
	 * 
	 * @param config
	 *            the configuration to convert
	 */
	private void convertWaitToActionsAndConditions(CodeGeneratorConfigurations config) {
		for (StateMachineForGeneration smg : StatemachinesConfigProvider.get(config)) {
			Set<StateWithActions> statesWithWait = new HashSet<StateWithActions>();

			for (Transition t : smg.getTransitions()) {
				TransitionInfo tinfo = smg.getTransitionInfo(t);
				if (!tinfo.isWaitTransition())
					continue;

				State source_ = t.getSource();
				if (source_ != null && source_ instanceof StateWithActions) {
					StateWithActions source = (StateWithActions) source_;

					statesWithWait.add(source);

					StateVariable timerVar = smg.getStateVariables()
							.getVariable(source, StateVariablePurposes.WAIT);
					if (timerVar == null) {
						// TODO use appropiate type
						timerVar = new StateVariable("wait_time", "uint8_t",
								null, source);
						smg.getStateVariables().add(timerVar,
								StateVariablePurposes.WAIT, source);
					}

					addCondition(
							tinfo,
							waitConditionFor(timerVar.getRealName(), tinfo,
									smg.getBasePeriod()));
				} else {
					messages.error(
							"Transition with wait condition starts at state '%s', which doesn't support actions\n",
							source_);
				}
			}

			for (StateWithActions state : statesWithWait) {
				List<Action> actions = smg.getActions(state);

				String timerVar = smg.getStateVariables()
						.getVariable(state, StateVariablePurposes.WAIT)
						.getRealName();

				// reset timer, when the state is entered
				actions.add(new Action(ActionType.ENTER,
						"// reset time for wait(...)\n" + timerVar + " = 0"));

				// increment timer, when remaining in state (but not when the
				// state is entered)
				actions.add(new Action(ActionType.DURING,
						"// increment time for wait(...)\n" + "++" + timerVar));
			}
		}
	}

	private void addCondition(TransitionInfo tinfo, String and_condition) {
		String condition = tinfo.getCondition();

		if (condition == null || condition.trim().equals(""))
			condition = and_condition;
		else
			condition = "(" + condition + ") && (" + and_condition + ")";

		tinfo.setCondition(condition);
	}

	private String waitConditionFor(String timerVar, TransitionInfo tinfo,
			double basePeriod) {
		String waitType = tinfo.getWaitType();
		double waitTime = tinfo.getWaitTime();
		if (waitType == null || Double.isNaN(waitTime))
			return null;

		double ticksD = waitTime / basePeriod;
		long ticks = Math.round(ticksD);
		double error = Math.abs((ticks - ticksD) / ticksD);
		if (Math.abs((ticks - ticksD) / ticksD) > 0.1)
			messages.warn("Actual wait time is off by %1.2f%% (%s instead of %s)\n",
							error * 100,
							Times.formatTime(ticks * basePeriod),
							Times.formatTime(waitTime));

		String operator;
		if (waitType.equals("wait") || waitType.equals("after"))
			operator = ">=";
		else if (waitType.equals("at"))
			operator = "==";
		else if (waitType.equals("before"))
			operator = "<=";
		else {
			messages.error("Unsupported wait type '%s' is treated like 'wait'\n",
							waitType);

			operator = ">=";
		}

		return timerVar + " " + operator + " " + ticks;
	}
}
