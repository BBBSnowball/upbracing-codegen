package de.upbracing.code_generation.generators.fsm;

import java.util.List;
import java.util.regex.Pattern;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.SuperState;
import Statecharts.Transition;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.ContextItem;
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

	/**
	 * constructor used for tests
	 * 
	 * Only a few methods can be used, if you built the object with this
	 * constructor.
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

				// set some rate because the program will crash, if it finds a
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
//			
				messages.addObjectFormatter(State.class, new Messages.ObjectFormatter<State>() {

					@Override
					public String format(int type, State obj) {
						// TODO Auto-generated method stub
						return obj.getName();
					}
				});
				
				ContextItem statemachine_context = messages.pushContext(smg);

				if (!nameValidate(smg)) {
					getNameErrorMess(smg.getName());
					valid = false;
				}

				// event validation
				else if (smg.getEvents().keySet().size() > 0) {
					for (String event : smg.getEvents().keySet())
						if (!evNameValidate(smg, event))
							valid = false;

				} else if (!typeCheckAndValidate(smg.getStates(), smg))
					valid = false;

				statemachine_context.pop();
			}
		}

		return messages.getHighestSeverity().ordinal() < Severity.ERROR
				.ordinal();
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
				ContextItem superstate_context = messages
						.pushContext((SuperState) state);

				if (!normSupValidate(state, smg))
					valid = false;

				for (Region region : ((SuperState) state).getRegions()) {
					ContextItem region_context = messages.pushContext(region);

					typeCheckAndValidate(region.getStates(), smg);
					region_context.pop();
				}
				superstate_context.pop();
			}
		}

		// check number of initial states for each parent
		if (!initialCount(smg.getStates()))
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
	private boolean initialCount(List<State> states) {
		int initialcount = 0;
		boolean valid = true;

		for (State state : states) {
			if (state instanceof InitialState) {
				initialcount++;

				if (initialcount > 1) {
					messages.error("%s has more than 1 start states",
							state.getName()).formatForCode(new StringBuffer());
					valid = false;
				}
			}
		}

		for (State s : states) {
			if (s instanceof SuperState) {
				ContextItem superstate_context = messages.pushContext(s);
				for (Region region : ((SuperState) s).getRegions()) {
					ContextItem region_context = messages.pushContext(region);
					initialCount(region.getStates());
					region_context.pop();
				}
				superstate_context.pop();
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

	public boolean nameValidate(StateMachineForGeneration smg) {
		if (emptyOrNull(smg.getName())) {
			messages.error("Statemachine name is empty or null!", smg)
					.formatForCode(new StringBuffer());
			return false;

		} else if (!isNameValid(smg.getName())) {
			getNameErrorMess(smg.getName());
			return false;

		} else
			return true;
	}

	// validate state name
	public boolean nameValidate(State state, StateMachineForGeneration smg) {
		String name = state.getName();

		if (emptyOrNull(name) || isNameValid(name))
			return true;
		else {
			getNameErrorMess(name);
			return false;
		}
	}

	public boolean isNameValid(String nametobevalidated) {
		Pattern name_pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
		return name_pattern.matcher(nametobevalidated).matches();
	}

	// event name validator
	// TODO pass Transition to this method and use it in the error message.
	private boolean evNameValidate(StateMachineForGeneration smg, String event) {
		if (emptyOrNull(event) || isNameValid(event))
			return true;
		else {
			getNameErrorMess(event);
			return false;
		}
	}

	private void getNameErrorMess(Object name) {
		messages.error("%s is not a valid C identifier!", name).formatForCode(
				new StringBuffer());
	}

	// validate initial state transitions
	private boolean initialTransValidate(State state,
			StateMachineForGeneration smg) {

		boolean valid = true;
		for (Transition trans : state.getOutgoingTransitions()) {
			TransitionInfo ti = smg.getTransitionInfo(trans);

			if (ti.isWaitTransition()) {
				getTransitionErrorMessage(trans.getSource(),
						trans.getDestination(), "waiting transition");
				valid = false;

			} else if (!emptyOrNull(ti.getEventName())) {

				getTransitionErrorMessage(trans.getSource(),
						trans.getDestination(), "events");
				valid = false;

			} else if (!emptyOrNull(ti.getCondition())) {

				getTransitionErrorMessage(trans.getSource(),
						trans.getDestination(), "conditions");
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

						getTransitionErrorMessage(trans.getSource(),
								trans.getDestination(), "waitType wait");
					valid = false;

				} else if (ti.getWaitType() == "before") {
					if (ti.getCondition().isEmpty()
							&& ti.getEventName().isEmpty())

						getTransitionErrorMessage(trans.getSource(),
								trans.getDestination(), "no condition/event");
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
			for (Transition trans : state.getIncomingTransitions())
				getTransitionErrorMessage(trans.getSource(),
						trans.getDestination(), "no incoming");
		} else
			hasincoming = false;

		return hasincoming;
	}

	// perform checks for outgoing transitions from initial state
	private boolean initialOutValidate(State state,
			StateMachineForGeneration smg) {
		boolean hasoutgoing = true;

		if (state.getOutgoingTransitions().size() > 1) {
			getStateTransitionError("more than 1 outgoing");
			hasoutgoing = false;
		}
		return hasoutgoing;
	}

	// check whether final has any outgoing transitions
	private boolean finalOutValidate(State state, StateMachineForGeneration smg) {
		boolean outgoing = false;

		if (state.getOutgoingTransitions().size() != 0) {
			getStateTransitionError("outgoing");
			outgoing = true;
		}
		return outgoing;
	}

	// check whether state has incoming transitions
	private boolean stateInTransValidate(State state,
			StateMachineForGeneration smg) {
		boolean incoming = true;

		if (state.getIncomingTransitions().size() == 0) {
			getStateTransitionError("no incoming");
			incoming = false;
		}
		return incoming;
	}

	private void getTransitionErrorMessage(Object... args) {
		messages.error(" Transition -> Source : %s Destination %s has %s!",
				args).formatForCode(new StringBuffer());
	}

	private void getStateTransitionError(String s) {
		messages.error(" State has %s transitions!", s).formatForCode(
				new StringBuffer());
	}
}