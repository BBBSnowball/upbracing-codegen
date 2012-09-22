package de.upbracing.code_generation.generators.fsm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.StateParent;
import Statecharts.SuperState;
import Statecharts.Transition;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.Severity;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.ParserException;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.fsm.model.TransitionInfo;
import de.upbracing.code_generation.generators.StatemachineGenerator;

/** The validation part of {@link StatemachineGenerator} */
public class Validator {
	private MCUConfiguration config;
	private boolean after_update_config;
	@SuppressWarnings("unused")
	private Object generator_data;
	private Messages messages;
	
	public Validator(MCUConfiguration config, boolean after_update_config,
			Object generator_data) {
		this.config = config;
		this.after_update_config = after_update_config;
		this.generator_data = generator_data;
		this.messages = config.getStatemachines().getMessages();
	}
	
	/** constructor used for tests
	 * 
	 * Only a few methods can be used, if you built the object
	 * with this constructor.
	 */
	public Validator(Messages messages) {
		this.messages = messages;
	}
	
	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public boolean validate() {
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
				if (!nameValidate(smg))
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
		
		return messages.getHighestSeverity().ordinal() < Severity.ERROR.ordinal();
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
				typeCheckAndValidate(superInnerStateValidate((SuperState)state, smg), smg);
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
	private List<State> superInnerStateValidate(SuperState state,
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
	
	public static boolean nameValidate(StateMachineForGeneration smg) {
		if (emptyOrNull(smg.getName())) {
			System.err.println("Statemachine name cannot be empty!");
			return false;
		} else if (!isNameValid(smg.getName())) {
			System.err.println("Statemachine name '" + smg.getName() + "'"
					+ " is not a valid C identifier!");
			return false;
		} else
			return true;
	}

	// validate state name
	public static boolean nameValidate(State state,
			StateMachineForGeneration smg) {
		String name = state.getName();

		if (emptyOrNull(name) || isNameValid(name))
			return true;
		else {
			System.err.println(getStateInfo(smg, state)
					+ " is not a valid C identifier!");
			return false;
		}
	}

	public static boolean isNameValid(String nametobevalidated) {
		Pattern name_pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
		return name_pattern.matcher(nametobevalidated).matches();
	}

	// event name validator
	//TODO pass Transition to this method and use it in the error message.
	private boolean evNameValidate(StateMachineForGeneration smg, String event) {
		if (emptyOrNull(event) || isNameValid(event))
			return true;
		else {
			System.err.println(getStateInfo(smg, null) + " | Event name -> " + event
					+ " is not a valid C identifier!");
			return false;
		}
	}

	// get state information
	private static String getStateInfo(StateMachineForGeneration smg,
			State state) {
		StringBuilder str = new StringBuilder();
		str.append("Statemachine -> " + smg.getName());

		if (state != null)
			getStateInfo(str, state);

		return str.toString();
	}

	private static void getStateInfo(StringBuilder info, State state) {
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
}
