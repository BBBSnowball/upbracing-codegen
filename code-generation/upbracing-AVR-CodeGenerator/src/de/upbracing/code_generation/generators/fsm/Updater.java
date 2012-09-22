package de.upbracing.code_generation.generators.fsm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateParent;
import Statecharts.StateWithActions;
import Statecharts.SuperState;
import Statecharts.Transition;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.ContextItem;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.Action;
import de.upbracing.code_generation.fsm.model.ActionType;
import de.upbracing.code_generation.fsm.model.FSMParsers;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.fsm.model.StateVariable;
import de.upbracing.code_generation.fsm.model.StateVariablePurposes;
import de.upbracing.code_generation.fsm.model.TransitionInfo;

public class Updater {
	private Messages messages;

	public Updater(MCUConfiguration config) {
		this.messages = config.getStatemachines().getMessages();
	}

	public Object updateConfig(MCUConfiguration config) {
		ContextItem context = messages.pushContext("updateConfig");
		
		updateParents(config);
		assignNames(config);
		removeFinalStates(config);
		addStateVariables(config);
		convertWaitToActionsAndConditions(config);
		
		context.pop();

		return null;
	}

	private void updateParents(MCUConfiguration config) {
		// Unfortunately, the parents of most elements are not set
		// automatically. We correct this here. The information
		// remains valid because the structure of the statemachine
		// won't change anymore.

		for (StateMachineForGeneration smg : config.getStatemachines()) {
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
	private void removeFinalStates(MCUConfiguration config) {
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			removeFinalStates(smg.getStates(), smg.getStateMachine());
		}
	}

	private void removeFinalStates(Iterable<State> states, StateParent parent) {
		List<FinalState> final_list = new ArrayList<FinalState>();
		for (State state : states) {
			if (state instanceof FinalState) {
				final_list.add((FinalState) state);
				if (final_list.size() > 1)
					removeFinalStates(final_list);
			}
		}
		for (State state : states) {
			if (state instanceof SuperState) {
				SuperState superstate = (SuperState) state;
				for (Region region : superstate.getRegions()) {
					removeFinalStates(region.getStates(), region);
				}
			}
		}
	}

	private void removeFinalStates(List<FinalState> state) {
		for (Transition transition : state.get(0).getIncomingTransitions())
			transition.setDestination(state.get(1));

		state.get(0).getParent().getStates().remove(state.get(0));
	}

	// assign names to unnamed states
	private void assignNames(MCUConfiguration config) {
		int runningCounter = 0;
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			// TODO for Rishab: Assign a name to each state and region that
			// doesn't have a
			// name, yet. You can generate unique names like this:
			// ("state" + (++runningCounter))

			// TODO for Rishab: Make sure that there is at most one final state
			// in each region
			// and top-level of a statemachine. If there is more than one final
			// state, remove it and update the transitions accordingly. There
			// can be more than one final state in a statemachine (e.g. one is
			// in a region and the other one on the top-level), so be careful
			// to set each transition to the right one.

			List<State> unnamed = new ArrayList<State>();

			unnamed.addAll(assignNames(smg.getStates(), smg.getStateMachine(),
					unnamed));

			for (State state : unnamed) {

				if (state instanceof InitialState)
					((InitialState) state).setName("Initial_"
							+ runningCounter++);

				else if (state instanceof FinalState)
					((FinalState) state).setName("Final_" + runningCounter++);

				else if (state instanceof NormalState)
					((NormalState) state).setName("Normal_" + runningCounter++);

				else if (state instanceof SuperState)
					((SuperState) state).setName("Super_" + runningCounter++);
			}
		}
	}

	// get list of unnamed states
	private List<State> assignNames(Iterable<State> states, StateParent parent,
			List<State> unnamed) {
		for (State state : states) {
			if (state instanceof SuperState) {
				if (emptyOrNull(state.getName()))
					unnamed.add(state);
				for (Region reg : ((SuperState) state).getRegions())
					assignNames(reg.getStates(), reg, unnamed);
			} else if (!(state instanceof SuperState))
				if (emptyOrNull(state.getName()))
					unnamed.add(state);
		}
		return unnamed;
	}

	public static boolean emptyOrNull(String obj) {
		return obj == null || obj.isEmpty();
	}

	private void addStateVariables(MCUConfiguration config) {
		for (StateMachineForGeneration smg : config.getStatemachines()) {
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
	private void convertWaitToActionsAndConditions(MCUConfiguration config) {
		for (StateMachineForGeneration smg : config.getStatemachines()) {
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
							smg.getName(source_));
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
							FSMParsers.formatTime(ticks * basePeriod),
							FSMParsers.formatTime(waitTime));

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
