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

		ContextItem validator_context = messages
				.pushContext("statemachine validator");

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
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			if ((!duplicateNames(smg, smg.getStates())) && after_update_config)
				return false;
		}

		// run the validator before the updator has been run
		if (!after_update_config) {
			for (StateMachineForGeneration smg : config.getStatemachines()) {

				// push the statemachine context
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

				// statemachine must have a final state
				// if (!statemachineHasFinal(smg))
				// return false;

				// check number of initial states for each parent
				checkInitialCount(smg.getStates());

				// traverse all states and regions and run various validators
				// for them
				typeCheckAndValidate(smg.getStates(), smg);

				// pop statemachine context
				statemachine_context.pop();
			}
		}

		// returns false if there are any error messages
		boolean valid = messages.getHighestSeverityInContext().ordinal() < Severity.ERROR
				.ordinal();

		// pops the validator context
		validator_context.pop();

		return valid;
	}

	/**
	 * Check for any duplicate state/region names in the statemachine
	 * 
	 * @param smg
	 *            The statemachine which is to be parsed for duplicate names
	 * @param states
	 *            The list of states that have statemachine as their parent
	 * @return true, if there are no duplicate names
	 */
	public boolean duplicateNames(StateMachineForGeneration smg,
			List<State> states) {
		boolean valid = true;

		// push state machine context
		ContextItem statemachine_context = messages.pushContext(smg);
		// call duplicateNames for evaluating normal states
		if (!duplicateStateNames(states, smg.getStates().size()))
			valid = false;

		// check for any super states
		for (State state : states)
			if (state instanceof SuperState) {
				// call duplicateNames for evaluating the SuperState
				if (!duplicateNames(state))
					valid = false;
			}
		// pop state machine context
		statemachine_context.pop();
		return valid;
	}

	// check for duplicate region names and state names in a super state
	private boolean duplicateNames(State state) {
		boolean valid = true;

		// cast state to Super state
		SuperState super_state = (SuperState) state;

		// push super state context
		ContextItem super_context = messages.pushContext(super_state);

		// set valid to false if there are any duplicate regions
		if (!duplicateRegionNames(super_state.getRegions(), super_state
				.getRegions().size()))
			valid = false;

		// parse through all the regions in the super state
		for (Region region : super_state.getRegions()) {

			// push region context
			ContextItem region_context = messages.pushContext(region);

			// collect the states of the region
			List<State> inner_states = region.getStates();

			// set valid to false if there are any duplicate names in the region
			if (!duplicateStateNames(inner_states, region.getStates().size()))
				valid = false;

			// get the states in the region
			for (State st : region.getStates())
				// check if there are any super states in the region
				if (st instanceof SuperState) {
					// call itself and return true or false depending on
					// whether there are any duplicate names in this super
					// state
					if (!duplicateNames(st))
						valid = false;
				}
			// pop the context of the region
			region_context.pop();
		}
		super_context.pop();
		return valid;
	}

	// check for duplicate region names
	private boolean duplicateRegionNames(List<Region> list, int size) {
		boolean valid = true;
		Set<String> region_names = new HashSet<String>();

		// add regions to the set
		for (Region reg : list)
			region_names.add(reg.getName());

		// if there are fewer regions in the set region_names (duplicate names)
		// than
		// the size of the list then set valid false
		if (region_names.size() < size) {
			duplicateNamesErrorMessage("regions");
			valid = false;
		}

		return valid;
	}

	// check for duplicate state names
	private boolean duplicateStateNames(List<State> list, int size) {
		boolean valid = true;
		Set<String> state_names = new HashSet<String>();

		// add states to the set
		for (State state : list)
			state_names.add(state.getName());

		// if the size of the set is less than the size of the list
		// then set the valid to false
		if (state_names.size() < size) {
			duplicateNamesErrorMessage("states");
			valid = false;
		}

		return valid;
	}

	// sets the duplicate names error messages
	private void duplicateNamesErrorMessage(String type) {
		messages.error("More than one %s have same name!", type);
	}

	// check all the state types and perform validation
	private void typeCheckAndValidate(List<State> s,
			StateMachineForGeneration smg) {

		// iterate through all the states
		for (State state : s) {

			// push the context of the state
			ContextItem state_context = messages.pushContext(state);

			// if state is an initial state perform validation for
			// initial state and so on for final, normal, and super states
			if (state instanceof InitialState)
				initalValidate(state, smg);

			else if (state instanceof FinalState)
				finalValidate(state, smg);

			else if (state instanceof NormalState)
				normSupValidate((NormalState) state, smg);

			else if (state instanceof SuperState) {
				SuperState sstate = (SuperState) state;

				normSupValidate(sstate, smg);

				// get the regions in the super state
				for (Region region : sstate.getRegions()) {
					// push the context of the region
					ContextItem region_context = messages.pushContext(region);

					// validate region name
					nameValidate(region);

					// call the method again for processing the states in the
					// region
					typeCheckAndValidate(region.getStates(), smg);

					// pop the context of the region
					region_context.pop();
				}
			}
			// pop the context of the state
			state_context.pop();
		}
	}

	/**
	 * Performs the validation of the initial state
	 * 
	 * @param state
	 *            the initial state for which the validation is to be performed
	 * @param smg
	 *            the statemachine in which this initial state is contained
	 */
	public void initalValidate(State state, StateMachineForGeneration smg) {
		// validate the name of the initial state
		nameValidate(state);

		// check whether the initial state has incoming transitions
		initialHasInTransValidate(state);

		// check whether the initial state has only one outgoing transition
		initialOutValidate(state);

		// check whether the initial transition has a condition/event/waitType
		initialTransValidate(state, smg);
	}

	/**
	 * Count the number of initial states that have the same parent -
	 * statemachine/region. If the number of initial states is greater than one
	 * then an error message is printed.
	 * 
	 * @param states
	 *            the list of the states in the parent(statemachine/region)
	 */
	public void checkInitialCount(List<State> states) {
		// initial state counter
		int initialcount = 0;

		// iterate through all the states in the parent and check for the
		// initial state. If there is an initial state initialcount is
		// incremented
		for (State state : states)
			if (state instanceof InitialState)
				initialcount++;

		// check for any super states, and if there are any super states then
		// the
		// then validation for initial states in these super states is performed
		for (State state : states) {
			if (state instanceof SuperState) {

				// push context of the super state
				ContextItem super_context = messages.pushContext(state);

				// collect all the regions of the super state
				for (Region region : ((SuperState) state).getRegions()) {

					// push context of the region
					ContextItem region_context = messages.pushContext(region);

					// count the initial states contained in the region
					checkInitialCount(region.getStates());

					// pop context of the region
					region_context.pop();
				}
				// pop super state context
				super_context.pop();
			}
		}

		// if initial states are greater than one then print an error message
		if (initialcount > 1)
			messages.error("More than 1 start states");

		// if there are some states in the region, but there is no initial state
		// then set an error message
		else if (initialcount < 1 && !states.isEmpty())
			messages.error("No initial state");
	}

	/**
	 * Perform all the checks relevant for the validation of the final state
	 * 
	 * @param state
	 *            the final state which is to be validates
	 * @param smg
	 *            the statemachine in which the final state is contained
	 */
	public void finalValidate(State state, StateMachineForGeneration smg) {
		// validate name of the final state
		nameValidate(state);

		// validate outgoing transitions of the final state
		finalOutValidate(state);

		// validate the trigger for the transition
		normalTransValidate(state, smg);
	}

	/**
	 * Perform all the relevant checks for the normal and super states
	 * 
	 * @param state
	 *            the normal/super state for which the validation is to be
	 *            performed
	 * @param smg
	 *            the statemachine in which this normal or super state is
	 *            contained
	 */
	public void normSupValidate(State state, StateMachineForGeneration smg) {
		// validate the name of the state
		nameValidate(state);

		// validate the transition triggers for normal and super states
		normalTransValidate(state, smg);
	}

	/** return whether we have any null values in inappropiate places */
	private boolean validateStatesNotNull(Iterable<State> states) {
		for (State state : states) {
			// push the statemachine context
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

	/** return whether we have any null values in inappropriate places */
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

	// returns true if the string passed is empty or null
	private static boolean emptyOrNull(String obj) {
		return obj == null || obj.isEmpty();
	}

	/**
	 * Validate the name of the statemachine.
	 * 
	 * @param smg
	 *            the statemachine whose name is to be validated
	 * @return true, if the name of the statemachine is a valid C identifier
	 */
	public boolean nameValidate(StateMachineForGeneration smg) {
		// if state name is empty or null then set an error message
		if (emptyOrNull(smg.getName())) {
			messages.error("Statemachine name is empty or null!", smg);
			return false;

		}
		// if state name is not a valid C identifier then set an error message
		else if (!isNameValid(smg.getName())) {
			setNameErrorMess(smg.getName());
			return false;

		} // if state name is a valid C identifier return true
		else
			return true;
	}

	/**
	 * Validate name of the state/region
	 * 
	 * @param state
	 *            the state whose name is to be validated
	 * @return true, if the name of the state is a valid C identifier
	 */
	public boolean nameValidate(NamedItem state) {
		String name = state.getName();
		
		if (emptyOrNull(name) || isNameValid(name))
			return true;
		else {
			setNameErrorMess(name);
			return false;
		}
	}
	
	//match the name of the state/region/statemachine against the regular expression
	//for a valid C identifier
	private boolean isNameValid(String nametobevalidated) {
		Pattern name_pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
		return name_pattern.matcher(nametobevalidated).matches();
	}

	//validate the name of the event. If the name is a valid C identifier, return true
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
	
	//error message for states/events/statemachines/region names which are not
	//valid C identifiers
	private void setNameErrorMess(String name) {
		messages.error("'%s' is not a valid C identifier!", name);
	}

	// validate initial state transitions
	private void initialTransValidate(State state, StateMachineForGeneration smg) {
		for (Transition trans : state.getOutgoingTransitions()) {
			//get transition info of the outgoing transitions from the state
			TransitionInfo ti = smg.getTransitionInfo(trans);
			
			//push context of the transition
			ContextItem trans_context = messages.pushContext(trans);
			
			//if the transition is triggered by waitType then set an error message
			if (ti.isWaitTransition())
				messages.error("Initial transitions cannot be waiting transitions");
			
			//if the transition is triggered by an event then set an error message
			if (!emptyOrNull(ti.getEventName()))
				messages.error("Initial transitions cannot be triggered by an event");
			
			//if transition is triggered by a condition then set an error message. The transition
			//condition should be either empty or null, or equal to "1" or "true"
			if (!emptyOrNull(ti.getCondition())
					&& !ti.getCondition().equals("1")
					&& !ti.getCondition().equals("true"))
				messages.error("Initial transitions cannot have a condition");

			trans_context.pop();
		}
	}

	// validate normal/super state transition
	private void normalTransValidate(State state, StateMachineForGeneration smg) {
		if (state.getIncomingTransitions().size() == 0)
			messages.error("State has no incoming transitions!");

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

//	private boolean statemachineHasFinal(StateMachineForGeneration smg) {
//		int final_count = 0;
//
//		for (State state : smg.getStates())
//			if (state instanceof FinalState)
//				final_count++;
//
//		return final_count > 0 ? true : false;
//	}
}
