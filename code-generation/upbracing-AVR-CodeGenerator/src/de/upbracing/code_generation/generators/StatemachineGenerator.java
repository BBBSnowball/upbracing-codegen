package de.upbracing.code_generation.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.jruby.org.objectweb.asm.tree.IntInsnNode;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.StateParent;
import Statecharts.StateWithActions;
import Statecharts.SuperState;
import Statecharts.Transition;
import de.upbracing.code_generation.StatemachinesHeaderTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.Action;
import de.upbracing.code_generation.fsm.model.ActionType;
import de.upbracing.code_generation.fsm.model.FSMParsers;
import de.upbracing.code_generation.fsm.model.ParserException;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.fsm.model.StateVariable;
import de.upbracing.code_generation.fsm.model.StateVariablePurposes;
import de.upbracing.code_generation.fsm.model.TransitionInfo;

/**
 * Generator for statemachine code
 * 
 * @author benny
 * 
 */
public class StatemachineGenerator extends AbstractGenerator {
	public StatemachineGenerator() {
		super("statemachines.h", new StatemachinesHeaderTemplate(),
				"statemachines.c", new StatemachinesCFileTemplate());
	}

	@Override
	public boolean validate(MCUConfiguration config,
			boolean after_update_config, Object generator_data) {
		boolean valid = true;

		// make sure that we don't have any unexpected null values
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			if (!validateNotNull(smg.getGlobalCodeBoxes(), "global code boxes"))
				return false;

			if (!validateNotNull(smg.getStates(), "states"))
				return false;

			if (!validateNotNull(smg.getTransitions(), "transitions"))
				return false;

			if (!validateStatesNotNull(smg.getStates()))
				return false;

			for (Transition t : smg.getTransitions()) {
				if (t.getSource() == null || t.getDestination() == null) {
					System.err
							.println("FATAL: Each transition must have a valid source and destination. Invalid transition: "
									+ t);
					return false;
				}
			}
		}

		// make sure that we have the appropiate values on the root object
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			if (smg.getBasePeriodAsString() == null) {
				System.err.println("ERROR: Statemachine needs a base rate");

				// set some rate because the program will crash, if it find a
				// null in there
				smg.setBasePeriod("1s");
			}

			try {
				smg.getBasePeriod();
			} catch (ParserException e) {
				System.err.println("Cannot parse time ('"
						+ smg.getBasePeriodAsString() + "'): " + e.toString());

				smg.setBasePeriod("1s");
			}
		}

		if (!after_update_config) {
			for (StateMachineForGeneration smg : config.getStatemachines()) {
				// TODO for Rishab: validate state machines
				// - statemachine names, regions and event names are valid C
				// identifiers; however, all
				// of them except the statemachine name can be empty
				// done // - no edges going to initial states or starting at
				// final states
				// done // - initial states have exactly one transition
				// done // - edges from initial states cannot have events, wait
				// conditions or conditions
				// - if an edge has a condition or an event, the waitType cannot
				// be "wait" ("after" and all the others are ok)
				// - if an edge has waitType=="before", it must have a condition
				// or an event (or both)
				// Probably, you will find some more things to check.

				// If an error is detected, the validator should continue and
				// display all
				// other errors.

				// For each error, print a meaningful message and the location.
				// The location could
				// look like this: "SuperState abc -> Region x -> State foo"

				// statemachine validation
				if (!nameValidate(smg.getName(), smg))
					valid = false;

				// event validation
				else if (smg.getEvents().keySet().size() > 0) {
					for (String event : smg.getEvents().keySet())
						if (!evNameValidate(smg, event))
							valid = false;

				} else if (!typeCheckAndValidate(smg.getStates(), smg))
					valid = false;
			}
		}
		return valid;
	}

	// check all the state types and perform validation
	private boolean typeCheckAndValidate(List<State> s,
			StateMachineForGeneration smg) {
		boolean valid = true;

		for (State state : s) {
			if (state instanceof InitialState)
				if (!initalValidate(state, smg))
					valid = false;

			if (state instanceof FinalState)
				if (!finalValidate(state, smg))
					valid = false;

			if (state instanceof NormalState)
				if (!normSupValidate(state, smg))
					valid = false;

			if (state instanceof SuperState) {
				if (!normSupValidate(state, smg))
					valid = false;
				typeCheckAndValidate(superInnerStateValidate(state, smg), smg);
			}
		}

		// check number of initial states for each parent
		if (!initialCount(smg, s))
			valid = false;

		return valid;
	}

	// perform validation of initial state
	private boolean initalValidate(State state, StateMachineForGeneration smg) {
		boolean valid = true;

		if (!nameValidate(state, smg))
			valid = false;

		else if (initialHasInTransValidate(state, smg))
			valid = false;

		else if (!initialOutValidate(state, smg))
			valid = false;

		else if (!initialTransValidate(state, smg))
			valid = false;

		return valid;
	}

	// count number of initial states
	private boolean initialCount(StateMachineForGeneration smg,
			List<State> states) {
		int initialcount = 0;
		boolean valid = true;

		for (State state : smg.getStates()) {
			if (state instanceof InitialState) {
				initialcount++;
				if (initialcount > 1) {
					System.err.println(getStateInfo(smg, state)
							+ " has more than one start states");
					valid = false;
				}
			}
			for (State st : smg.getStates()) {
				if (state instanceof SuperState) {
					for (Region region : ((SuperState) state).getRegions())
						initialCount(smg, region.getStates());
				}
			}
		}
		return valid;
	}

	// validate all final states
	private boolean finalValidate(State state, StateMachineForGeneration smg) {
		boolean valid = true;

		if (!nameValidate(state, smg))
			valid = false;

		else if (!finalOutValidate(state, smg))
			valid = false;

		else if (!stateInTransValidate(state, smg))
			valid = false;

		else if (!normalTransValidate(state, smg))
			valid = false;

		return valid;
	}

	// validate normal and super states
	private boolean normSupValidate(State state, StateMachineForGeneration smg) {
		boolean valid = true;

		if (!nameValidate(state, smg))
			valid = false;

		else if (!stateInTransValidate(state, smg))
			valid = false;

		else if (!normalTransValidate(state, smg))
			valid = false;

		return valid;
	}

	// validate states inside superstate
	private List<State> superInnerStateValidate(State state,
			StateMachineForGeneration smg) {
		List<State> states = new ArrayList<State>();

		for (Region region : ((SuperState) state).getRegions())
			states.addAll(states);

		return states;
	}

	private boolean validateStatesNotNull(Iterable<State> states) {
		for (State state : states) {
			validateNotNull(state.getIncomingTransitions(),
					"incoming transitions of " + state);
			validateNotNull(state.getOutgoingTransitions(),
					"outgoing transitions of " + state);

			if (state instanceof SuperState) {
				for (Region region : ((SuperState) state).getRegions()) {
					if (!validateStatesNotNull(region.getStates()))
						return false;
				}
			}
		}
		return true;
	}

	private boolean validateNotNull(Iterable<?> xs, String list_name) {
		for (Object x : xs) {
			if (x == null) {
				System.err
						.println("FATAL: At least one element in the list of "
								+ list_name + " is null");
				return false;
			}
		}
		return true;
	}

	public static boolean emptyOrNull(String obj) {
		return obj == null || obj.isEmpty();
	}

	@Override
	public Object updateConfig(MCUConfiguration config) {
		updateParents(config);
		assignNames(config);
		removeFinalStates(config);
		addStateVariables(config);
		convertWaitToActionsAndConditions(config);

		return super.updateConfig(config);
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

	// validate state name
	public static boolean nameValidate(Object nametobevalidated,
			StateMachineForGeneration smg) {
		Pattern digit = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
		boolean validname = true;

		if (nametobevalidated instanceof State) {
			State state = (State) nametobevalidated;
			String name = state.getName();
			
			if (emptyOrNull(name) || (!digit.matcher(name).matches())) {
				System.err.println(getStateInfo(smg, state)
						+ " is not a valid C identifier!");
				validname = false;
				}
		} else if (!(digit.matcher((String) nametobevalidated).matches())) {
			System.err.println(getStateInfo(smg));
			validname = false;
		}

		return validname;
	}

	// event name validator
	private boolean evNameValidate(StateMachineForGeneration smg, String event) {
		Pattern digit = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
		boolean validevent = true;

		if (event.isEmpty() || (!digit.matcher(event).matches())) {
			System.err.println(getStateInfo(smg) + " | Event name -> " + event
					+ " is not a valid C identifier!");
			validevent = false;
		}
		return validevent;
	}

	// get state information
	private static String getStateInfo(StateMachineForGeneration smg,
			State... state) {
		StringBuilder str = new StringBuilder();
		str.append("Statemachine -> " + smg.getName());
		State[] stat = state;

		if (state.length != 0)
			str.append(getStateInfo(new StringBuilder(), stat[0]));

		return str.toString();
	}

	private static String getStateInfo(StringBuilder inf, State state) {
		StringBuilder info = new StringBuilder();
		StateParent parent = state.getParent();

		if (parent instanceof StateMachine) {
			info.insert(0, " | State -> " + state.getName());

		} else if (parent instanceof Region) {
			Region region = (Region) parent;
			SuperState super_state = (SuperState) region.getParent();
			info.insert(0, " | Super State -> " + super_state.getName()
					+ " | Region -> " + region.getName());
			getStateInfo(info, super_state);
		}
		return info.toString();
	}

	// get transition information
	private String getTransitionInfo(Transition trans) {
		return (" | Transition -> Source : " + trans.getSource()
				+ " Destination : " + trans.getDestination());
	}

	// validate initial state transitions
	private boolean initialTransValidate(State state,
			StateMachineForGeneration smg) {

		boolean valid = true;
		for (Transition trans : state.getOutgoingTransitions()) {
			TransitionInfo ti = smg.getTransitionInfo(trans);

			if (ti.isWaitTransition()) {
				System.err.println(getStateInfo(smg, state)
						+ getTransitionInfo(trans)
						+ " : Start state cannot have waiting transition!");
				valid = false;
			} else if (!emptyOrNull(ti.getEventName())) {
				System.err.println(getStateInfo(smg, state)
						+ getTransitionInfo(trans)
						+ " : Start state cannot have events!");
				valid = false;
			} else if (!emptyOrNull(ti.getCondition())) {
				System.err.println(getStateInfo(smg, state)
						+ getTransitionInfo(trans)
						+ " : Start state cannot have conditions !");
				valid = false;
			}
		}
		return valid;
	}

	// validate normal/super state transition
	private boolean normalTransValidate(State state,
			StateMachineForGeneration smg) {

		boolean valid = true;
		for (Transition trans : state.getIncomingTransitions()) {
			TransitionInfo ti = smg.getTransitionInfo(trans);

			if (!(trans.getSource() instanceof InitialState)) {
				if (!emptyOrNull(ti.getCondition())
						|| !emptyOrNull(ti.getEventName())) {

					if (ti.getWaitType() == "wait")
						System.err
								.println(getStateInfo(smg, state)
										+ getTransitionInfo(trans)
										+ " Transition having condition or event cannot have wait type wait!");
					valid = false;

				} else if (ti.getWaitType() == "before") {
					if (ti.getCondition().isEmpty()
							&& ti.getEventName().isEmpty())
						System.err
								.println(getStateInfo(smg, state)
										+ getTransitionInfo(trans)
										+ " Transition must either have a condition or an event!");
					valid = false;
				}
			}
		}
		return valid;
	}

	// check whether initial state has incoming transitions
	private boolean initialHasInTransValidate(State state,
			StateMachineForGeneration smg) {
		boolean hasincoming = true;
		if (state.getIncomingTransitions().size() != 0) {
			System.err.println(getStateInfo(smg, state)
					+ " Initial state cannot have any incoming transitions! ");
		} else
			hasincoming = false;

		return hasincoming;
	}

	// perform checks for outgoing transitions from initial state
	private boolean initialOutValidate(State state,
			StateMachineForGeneration smg) {
		boolean hasoutgoing = true;
		if (state.getOutgoingTransitions().size() > 1) {
			System.err.println(getStateInfo(smg, state)
					+ " cannot have more than 1 outgoing transitions!");
			hasoutgoing = false;
		}
		return hasoutgoing;
	}

	// check whether final has any outgoing transitions
	private boolean finalOutValidate(State state, StateMachineForGeneration smg) {
		boolean outgoing = false;
		if (state.getOutgoingTransitions().size() != 0) {
			System.err.println(getStateInfo(smg, state)
					+ " : has outgoing transitions!");
			outgoing = true;
		}
		return outgoing;
	}

	// check whether state has incoming transitions
	private boolean stateInTransValidate(State state,
			StateMachineForGeneration smg) {
		boolean incoming = true;
		if (state.getIncomingTransitions().size() == 0) {
			System.err.println(getStateInfo(smg, state)
					+ " : has no incoming transitions!");
			incoming = false;
		}
		return incoming;
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
					// TODO use Sven's Warnings class
					System.err
							.format("ERROR: Transition with wait condition starts at state '%s', which doesn't support actions\n",
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
			// TODO use Sven's Warnings class
			System.err
					.format("WARN: Actual wait time is off by %1.2f%% (%s instead of %s)\n",
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
			// TODO use Sven's Warnings class
			System.err
					.format("ERROR: Unsupported wait type '%s' is treated like 'wait'\n",
							waitType);

			operator = ">=";
		}

		return timerVar + " " + operator + " " + ticks;
	}
}
