package de.upbracing.code_generation.generators.fsm;

import java.util.List;
import java.util.Set;
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
	private static Messages messages;

	public Validator(MCUConfiguration config, boolean after_update_config,
			Object generator_data) {
		this.config = config;
		this.after_update_config = after_update_config;
		this.generator_data = generator_data;
		Validator.messages = config.getStatemachines().getMessages();
		initFormatters();
	}

	/**
	 * constructor used for tests
	 * 
	 * Only a few methods can be used, if you built the object with this
	 * constructor.
	 */
	public Validator(Messages messages) {
		Validator.messages = messages;
		initFormatters();
	}

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		Validator.messages = messages;
	}

	public boolean validate() {

		@SuppressWarnings("unused")
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

				ContextItem statemachine_context = messages.pushContext(smg);

				if (!nameValidate(smg)) {
					setNameErrorMess(smg.getName());
					valid = false;
				}

				// event validation
				if (smg.getEvents().keySet().size() > 0) {
					for (String event : smg.getEvents().keySet())
						if (!evNameValidate(event, smg.getEvents().get(event),
								smg))
							valid = false;
				} 
				
				if (!typeCheckAndValidate(smg.getStates(), smg))
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
			ContextItem state_context = messages.pushContext(state);

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

				for (Region region : ((SuperState) state).getRegions()) {
					ContextItem region_context = messages.pushContext(region);

					typeCheckAndValidate(region.getStates(), smg);
					region_context.pop();
				}
			}
			state_context.pop();
		}

		// check number of initial states for each parent
		if (!initialCountExpected(smg.getStates()))
			valid = false;
		
		return valid;
	}

	// perform validation of initial state
	public boolean initalValidate(State state, StateMachineForGeneration smg) {
		boolean valid = true;

		if (!nameValidate(state))
			valid = false;

		if (initialHasInTransValidate(state, smg))
			valid = false;

		if (!initialOutValidate(state, smg))
			valid = false;

		if (initialTransValidate(state, smg)) {
			valid = false;
		}
		return valid;
	}

	// count number of initial states
	public boolean initialCountExpected(List<State> states) {
		int initialcount = 0;
		boolean valid = true;

		for (State state : states) {
			if (state instanceof InitialState) {
				initialcount++;

				if (initialcount > 1) {
					messages.error("More than 1 start states");
					valid = false;
				}
			}
		}

		for (State s : states) {
			if (s instanceof SuperState) {
				ContextItem superstate_context = messages.pushContext(s);

				for (Region region : ((SuperState) s).getRegions()) {
					ContextItem region_context = messages.pushContext(region);
					initialCountExpected(region.getStates());
					region_context.pop();
				}
				superstate_context.pop();
			}
		}

		return valid;
	}
	
	// validate all final states
	public boolean finalValidate(State state, StateMachineForGeneration smg) {
		boolean valid = true;
		
		if (!nameValidate(state))
			valid = false;

		if (!finalOutValidate(state, smg))
			valid = false;

		if (!stateInTransValidate(state, smg))
			valid = false;

		if (!normalTransValidate(state, smg))
			valid = false;
		
		return valid;
	}

	// validate normal and super states
	public boolean normSupValidate(State state, StateMachineForGeneration smg) {
		boolean valid = true;

		if (!nameValidate(state))
			valid = false;

		if (!stateInTransValidate(state, smg))
			valid = false;

		if (!normalTransValidate(state, smg))
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

	private static boolean emptyOrNull(String obj) {
		return obj == null || obj.isEmpty();
	}

	public boolean nameValidate(StateMachineForGeneration smg) {
		if (emptyOrNull(smg.getName())) {
			messages.error("Statemachine name is empty or null!", smg);
			return false;

		} else if (!isNameValid(smg.getName())) {
			setNameErrorMess(smg.getName());
			return false;

		} else
			return true;
	}

	// validate state name
	public boolean nameValidate(State state) {
		String name = state.getName();

		if (emptyOrNull(name) || isNameValid(name))
			return true;
		else {
			setNameErrorMess(name);
			return false;
		}
	}

	public static boolean isNameValid(String nametobevalidated) {
		Pattern name_pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
		return name_pattern.matcher(nametobevalidated).matches();
	}

	// event name validator
	// TODO pass Transition to this method and use it in the error message.
	private boolean evNameValidate(String event, Set<Transition> trans,
			StateMachineForGeneration smg) {
		if (emptyOrNull(event) || isNameValid(event))
			return true;
		else {
			for (Transition t : trans) {
				ContextItem transition_context = messages.pushContext(t);
				setNameErrorMess(event);
				transition_context.pop();
			}
			return false;
		}
	}

	private static void setNameErrorMess(Object name) {
		messages.error("%s is not a valid C identifier!", name);
	}

	// validate initial state transitions
	private boolean initialTransValidate(State state,
			StateMachineForGeneration smg) {

		boolean valid = true;
		for (Transition trans : state.getOutgoingTransitions()) {
			TransitionInfo ti = smg.getTransitionInfo(trans);

			ContextItem trans_context = messages.pushContext(trans);
			
			if (ti.isWaitTransition()) {
				setTransitionErrorMessage("waiting transition");
				valid = false;
			}

			if (!emptyOrNull(ti.getEventName())) {
				setTransitionErrorMessage("events");
				valid = false;
			}
			
			if (!emptyOrNull(ti.getCondition())) {
				setTransitionErrorMessage("conditions");
				valid = false;
			}

			trans_context.pop();
		}
		return valid;
	}

	// validate normal/super state transition
	private boolean normalTransValidate(State state,
			StateMachineForGeneration smg) {

		boolean valid = true;
		for (Transition trans : state.getIncomingTransitions()) {
			TransitionInfo ti = smg.getTransitionInfo(trans);
			ContextItem trans_context = messages.pushContext(trans);

			if (!(trans.getSource() instanceof InitialState)) {
				if (!emptyOrNull(ti.getCondition())
						|| !emptyOrNull(ti.getEventName())) {
					
					if (ti.getWaitType().equals("wait"))
						setTransitionErrorMessage("waitType 'wait' but condition/event not empty");

					valid = false;

				} else if (ti.getWaitType().equals("before")) {
					if (emptyOrNull(ti.getCondition())
							&& emptyOrNull(ti.getEventName()))

						setTransitionErrorMessage("waitType 'before' but no condition/event");
					valid = false;
				}
			}
			trans_context.pop();
		}
		return valid;
	}

	// check whether initial state has incoming transitions
	private boolean initialHasInTransValidate(State state,
			StateMachineForGeneration smg) {
		boolean hasincoming = true;

		if (state.getIncomingTransitions().size() != 0) {
			for (Transition trans : state.getIncomingTransitions()) {
				ContextItem trans_context = messages.pushContext(trans);
				setStateTransitionError("incoming");
				trans_context.pop();
			}
		} else
			hasincoming = false;

		return hasincoming;
	}

	// perform checks for outgoing transitions from initial state
	private boolean initialOutValidate(State state, StateMachineForGeneration smg) {
		boolean hasoutgoing = true;

		if (state.getOutgoingTransitions().size() > 1) {
			setStateTransitionError("more than 1 outgoing");
			hasoutgoing = false;
		}
		return hasoutgoing;
	}

	// check whether final has any outgoing transitions
	private boolean finalOutValidate(State state, StateMachineForGeneration smg) {
		boolean outgoing = false;

		if (state.getOutgoingTransitions().size() != 0) {
			setStateTransitionError("outgoing");
			outgoing = true;
		}
		return outgoing;
	}

	// check whether state has incoming transitions
	private boolean stateInTransValidate(State state,
			StateMachineForGeneration smg) {
		boolean incoming = true;

		if (state.getIncomingTransitions().size() == 0) {
			setStateTransitionError("No incoming");
			incoming = false;
		}
		return incoming;
	}

	private void setTransitionErrorMessage(String s) {
		messages.error("Transition has %s!", s);
	}

	private void setStateTransitionError(String s) {
		messages.error("State has %s transitions!", s);
	}

	private void initFormatters() {
		messages.addObjectFormatter(StateMachineForGeneration.class,
				new Messages.ObjectFormatter<StateMachineForGeneration>() {
					public String format(int type, StateMachineForGeneration obj) {
						if (type == SHORT)
							return obj.getName();
						else
							return " StateMachine -> " + obj.getName();
					}
				});

		messages.addObjectFormatter(State.class,
				new Messages.ObjectFormatter<State>() {
					public String format(int type, State obj) {
						if (type == SHORT)
							return obj.getName();
						else
							return " State -> " + obj.getName();
					}
				});

		messages.addObjectFormatter(InitialState.class,
				new Messages.ObjectFormatter<InitialState>() {
					public String format(int type, InitialState obj) {
						if (type == SHORT)
							return obj.getName();
						else
							return " Start State -> " + obj.getName();
					}
				});

		messages.addObjectFormatter(FinalState.class,
				new Messages.ObjectFormatter<FinalState>() {
					public String format(int type, FinalState obj) {
						if (type == SHORT)
							return obj.getName();
						else
							return " Final State -> " + obj.getName();
					}
				});

		messages.addObjectFormatter(NormalState.class,
				new Messages.ObjectFormatter<NormalState>() {
					public String format(int type, NormalState obj) {
						if (type == SHORT)
							return obj.getName();
						else
							return " Normal State -> " + obj.getName();
					}
				});

		messages.addObjectFormatter(NormalState.class,
				new Messages.ObjectFormatter<NormalState>() {
					public String format(int type, NormalState obj) {
						if (type == SHORT)
							return obj.getName();
						else
							return " Normal State -> " + obj.getName();
					}
				});

		messages.addObjectFormatter(SuperState.class,
				new Messages.ObjectFormatter<SuperState>() {
					public String format(int type, SuperState obj) {
						if (type == SHORT)
							return obj.getName();
						else
							return " Super State -> " + obj.getName();
					}
				});

		messages.addObjectFormatter(Region.class,
				new Messages.ObjectFormatter<Region>() {
					public String format(int type, Region obj) {
						if (type == SHORT)
							return obj.getName();
						else
							return " Region -> " + obj.getName();

					}
				});

		messages.addObjectFormatter(Transition.class,
				new Messages.ObjectFormatter<Transition>() {
					public String format(int type, Transition obj) {
						if (type == SHORT)
							return "from State " + obj.getSource();
						else
							return " Transition: Source -> "
									+ obj.getSource().getName()
									+ " Destination -> "
									+ obj.getDestination().getName();
					}
				});
	}
}