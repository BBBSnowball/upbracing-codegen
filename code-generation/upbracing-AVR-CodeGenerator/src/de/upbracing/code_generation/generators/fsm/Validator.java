package de.upbracing.code_generation.generators.fsm;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NamedItem;
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
		this.messages = config.getMessages();
	}

	/**
	 * constructor used for tests
	 * 
	 * Only a few methods can be used, if you built the object with this
	 * constructor.
	 */
	public Validator(Messages messages) {
		this.messages = messages;
		Helpers.addStatemachineFormatters(messages,
				Collections.<StateMachineForGeneration> emptyList());
	}

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public boolean validate() {
		if (!validateNull())
			return false;

		// make sure that we have the appropiate values on the root object
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			ContextItem smg_context = messages.pushContext(smg);

			if (smg.getBasePeriodAsString() == null) {
				messages.error("Statemachine needs a base rate");

				// set some rate because the program will crash, if it finds a
				// null in there
				smg.setBasePeriod("1s");
			}

			try {
				smg.getBasePeriod();
			} catch (ParserException e) {
				messages.error("Cannot parse time ('%s'): %s",
						smg.getBasePeriodAsString(), e.toString());

				smg.setBasePeriod("1s");
			}

			smg_context.pop();
		}

		// check states and regions for duplicate names
//		for (StateMachineForGeneration smg : config.getStatemachines()) {
//			if (!duplicateNames(smg, smg.getStates()))
//				return false;
//		}
		
		if (!after_update_config) {
			for (StateMachineForGeneration smg : config.getStatemachines()) {
				// statemachine validation

				ContextItem statemachine_context = messages.pushContext(smg);

				// check whether the statemachine has a valid name
				if (!nameValidate(smg)) {
					setNameErrorMess(smg.getName());
				}

				// event name validation
				if (!smg.getEvents().isEmpty()) {
					for (String event : smg.getEvents().keySet())
						evNameValidate(event, smg.getEvents().get(event), smg);
				}

				// traverse all states and regions and run various validators
				// for them
				typeCheckAndValidate(smg.getStates(), smg);

				statemachine_context.pop();
			}
		}

		return messages.getHighestSeverity().ordinal() < Severity.ERROR
				.ordinal();

	}

	public boolean duplicateNames(StateMachineForGeneration smg, List<State> states) {
		ContextItem statemachine_context = messages.pushContext(smg);
		Set<String> region_names = new HashSet<String>();
		duplicateNames(states, smg.getStates().size());
		boolean valid = true;
		
		for (State state : states) {
			if (state instanceof SuperState) {
				SuperState super_state = (SuperState) state;
				ContextItem super_context = messages.pushContext(super_state);
				
				for (Region region : super_state.getRegions()) 
					region_names.add(region.getName());
					
					if (region_names.size() < super_state.getRegions().size()) {
						duplicateNamesErrorMessage("regions");
						valid = false;
					}
					
					for (Region region : super_state.getRegions()) {
						ContextItem region_context = messages.pushContext(region);
						List<State> inner_states = region.getStates();
						duplicateNames(inner_states, region.getStates().size());
					
						region_context.pop();
					}
				super_context.pop();
			}
		}
		statemachine_context.pop();
		return valid;
	}
	
	private boolean duplicateNames(List<State> list, int size) {
		Set<String> state_names = new HashSet<String>();
		boolean valid = true;

		for (State state : list) 
			state_names.add(state.getName());
		
		if (state_names.size() < size) {
			duplicateNamesErrorMessage("states");
			valid = false;
		}
		
		return valid;
	}

	private void duplicateNamesErrorMessage(String type) {
		messages.error("More than one %s have same name!", type);
	}

	// check all the state types and perform validation
	private void typeCheckAndValidate(List<State> s,
			StateMachineForGeneration smg) {
		for (State state : s) {
			ContextItem state_context = messages.pushContext(state);

			if (state instanceof InitialState)
				initalValidate(state, smg);

			else if (state instanceof FinalState)
				finalValidate(state, smg);

			else if (state instanceof NormalState)
				normSupValidate((NormalState) state, smg);

			else if (state instanceof SuperState) {
				SuperState sstate = (SuperState) state;

				normSupValidate(sstate, smg);

				for (Region region : sstate.getRegions()) {
					ContextItem region_context = messages.pushContext(region);

					nameValidate(region);

					typeCheckAndValidate(region.getStates(), smg);

					region_context.pop();
				}
			}

			state_context.pop();
		}

		// check number of initial states for each parent
		checkInitialCount(smg.getStates());
	}

	// perform validation of initial state
	public void initalValidate(State state, StateMachineForGeneration smg) {
		nameValidate(state);

		initialHasInTransValidate(state);

		initialOutValidate(state);

		initialTransValidate(state, smg);
	}

	// count number of initial states
	public void checkInitialCount(List<State> states) {
		int initialcount = 0;
		for (State state : states) {
			if (state instanceof InitialState) {
				initialcount++;

			}
		}

		if (initialcount > 1)
			messages.error("More than 1 start states");
		else if (initialcount < 1 && !states.isEmpty())
			messages.error("No initial state");
	}

	// validate all final states
	public void finalValidate(State state, StateMachineForGeneration smg) {
		nameValidate(state);

		finalOutValidate(state);

		normalTransValidate(state, smg);
	}

	// validate normal and super states
	public void normSupValidate(State state, StateMachineForGeneration smg) {
		nameValidate(state);

		normalTransValidate(state, smg);
	}

	/** return whether we have any null values in inappropiate places */
	private boolean validateStatesNotNull(Iterable<State> states) {
		for (State state : states) {
			ContextItem state_context = messages.pushContext(state);

			validateNotNull(state.getIncomingTransitions(),
					"incoming transitions");
			validateNotNull(state.getOutgoingTransitions(),
					"outgoing transitions");

			if (state instanceof SuperState) {
				for (Region region : ((SuperState) state).getRegions()) {
					if (!validateStatesNotNull(region.getStates()))
						return false;
				}
			}

			state_context.pop();
		}
		return true;
	}

	/**
	 * print fatal error, if there are any null values in the list
	 * 
	 * @param xs
	 *            the items that shouldn't be null
	 * @param list_name
	 *            a description of the items in the list to use in the error
	 *            message (should be in plural, e.g. "states")
	 * @return true, if there isn't any null value
	 */
	private boolean validateNotNull(Iterable<?> xs, String list_name) {
		for (Object x : xs) {
			if (x == null) {
				messages.fatal("FATAL: At least one element in the list of "
						+ list_name + " is null");
				return false;
			}
		}
		return true;
	}

	/** return whether we have any null values in inappropiate places */
	private boolean validateNull() {
		// make sure that we don't have any unexpected null values
		ContextItem task_context = messages
				.pushContext("check for null values in inappropiate places");
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			ContextItem smg_context = messages.pushContext(smg);

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
					messages.fatal(
							"FATAL: Each transition must have a valid source and destination. Invalid transition: %s",
							t);
					return false;
				}
			}

			smg_context.pop();
		}
		task_context.pop();

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
	public boolean nameValidate(NamedItem state) {
		String name = state.getName();

		if (emptyOrNull(name) || isNameValid(name))
			return true;
		else {
			setNameErrorMess(name);
			return false;
		}
	}

	public boolean isNameValid(String nametobevalidated) {
		Pattern name_pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
		return name_pattern.matcher(nametobevalidated).matches();
	}

	// event name validator
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

	private void setNameErrorMess(String name) {
		messages.error("'%s' is not a valid C identifier!", name);
	}

	// validate initial state transitions
	private void initialTransValidate(State state, StateMachineForGeneration smg) {
		for (Transition trans : state.getOutgoingTransitions()) {
			TransitionInfo ti = smg.getTransitionInfo(trans);

			ContextItem trans_context = messages.pushContext(trans);

			if (ti.isWaitTransition())
				messages.error("Initial transitions cannot be waiting transitions");

			if (!emptyOrNull(ti.getEventName()))
				messages.error("Initial transitions cannot be triggered by an event");

			if (!emptyOrNull(ti.getCondition())
					&& !ti.getCondition().equals("1")
					&& !ti.getCondition().equals("true"))
				messages.error("Initial transitions cannot have a condition");

			trans_context.pop();
		}
	}

	// validate normal/super state transition
	private void normalTransValidate(State state, StateMachineForGeneration smg) {
		for (Transition trans : state.getIncomingTransitions()) {
			TransitionInfo ti = smg.getTransitionInfo(trans);

			// initial transitions are checked somewhere else
			if (trans.getSource() instanceof InitialState)
				continue;

			ContextItem trans_context = messages.pushContext(trans);

			String condition = ti.getCondition();
			String eventname = ti.getEventName();
			String waitType = ti.getWaitType();

			if (waitType != null) {
				if (waitType.equals("wait")) {
					// If an edge has a condition or an event, the waitType
					// cannot
					// be "wait" ("after" and all the others are ok).
					if (!emptyOrNull(condition) || !emptyOrNull(eventname))
						messages.error("Transition has waitType 'wait', but condition or event not empty");
				} else if (waitType.equals("before")) {
					// If an edge has waitType=="before", it must have a
					// condition
					// or an event (or both)
					if (emptyOrNull(condition) && emptyOrNull(eventname))
						messages.error("Transition has waitType 'before' but neither condition nor event");
				}
			}

			trans_context.pop();
		}
	}

	// check whether initial state has incoming transitions
	private void initialHasInTransValidate(State state) {
		for (Transition trans : state.getIncomingTransitions()) {
			ContextItem trans_context = messages.pushContext(trans);
			messages.error("Initial states cannot have incoming transitions!");
			trans_context.pop();
		}
	}

	// perform checks for outgoing transitions from initial state
	private void initialOutValidate(State state) {
		if (state.getOutgoingTransitions().size() != 1) {
			messages.error("Initial state must have exactly one outgoing transition");
		}
	}

	// check whether final has any outgoing transitions
	private void finalOutValidate(State state) {
		if (!state.getOutgoingTransitions().isEmpty()) {
			messages.error("Final states cannot have outgoing transitions!");
		}
	}
}
