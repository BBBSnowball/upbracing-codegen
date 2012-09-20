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
				String statemachine_name = smg.getName();
				if (!nameValidator(statemachine_name))
					valid = false;

				// event validation
				if (smg.getEvents().keySet().size() > 0)
					for (String event : smg.getEvents().keySet())
						if (!eventNameValidator(statemachine_name, event))
							valid = false;

				int initial_state_count = 0;

				for (State state : smg.getStates()) {
					if (state instanceof InitialState) {
						InitialState initial_state = (InitialState) state;
						initial_state_count++;

						if (!nameValidator(statemachine_name, state.getName()))
							valid = false;
						else if (incomingTransitionCount(initial_state,
								statemachine_name)) {
							initialHasIncoming(initial_state, statemachine_name);
							valid = false;
						} else if (!outgoingTransitionCount(initial_state,
								statemachine_name))
							valid = false;
						else if (!initialTransitionValidator(initial_state, smg))
							valid = false;
					}

					// final state validity
					else if (state instanceof FinalState) {
						FinalState final_state = (FinalState) state;

						if (!nameValidator(statemachine_name,
								final_state.getName()))
							valid = false;

						else if (outgoingTransitionCount(final_state,
								statemachine_name)) {
							finalHasOutgoing(final_state, statemachine_name);
							valid = false;

						} else if (!incomingTransitionCount(final_state,
								statemachine_name))
							valid = false;

						else if (!normalTransitionValidator(final_state, smg))
							valid = false;

					}
					// normal state validity
					else if (state instanceof NormalState) {
						NormalState normal_state = (NormalState) state;

						if (!nameValidator(statemachine_name,
								normal_state.getName()))
							valid = false;

						else if (!incomingTransitionCount(normal_state,
								statemachine_name))
							valid = false;

						else if (!outgoingTransitionCount(normal_state,
								statemachine_name))
							valid = false;

						else if ((!normalTransitionValidator(normal_state, smg)))
							valid = false;
					}

					else if (state instanceof SuperState) {
						SuperState super_state = (SuperState) state;

						if (!nameValidator(statemachine_name,
								super_state.getName()))
							valid = false;

						else if (!outgoingTransitionCount(super_state,
								statemachine_name))
							valid = false;

						else if (!incomingTransitionCount(super_state,
								statemachine_name))
							valid = false;

						else if ((!normalTransitionValidator(super_state, smg)))
							valid = false;

						if (super_state.getRegions().size() != 0)
							for (Region r : super_state.getRegions()) {

								if (!nameValidator(statemachine_name,
										super_state.getName(), r.getName()))
									valid = false;
								else if (r.getStates().isEmpty()) {
									System.err.println("Statemachine -> "
											+ statemachine_name
											+ " | State name -> "
											+ super_state.getName()
											+ " | Region name -> "
											+ r.getName()
											+ " | Does not have any states.");
									valid = false;
								}

								int region_inital_count = 0;

								for (State s : r.getStates()) {
									InitialState initial_state_region;

									if (s instanceof InitialState) {
										initial_state_region = (InitialState) s;
										region_inital_count++;

										if (!nameValidator(statemachine_name,
												super_state.getName(),
												r.getName(),
												initial_state_region.getName()))
											valid = false;

										else if (incomingTransitionCount(
												initial_state_region,
												statemachine_name))
											valid = false;

										else if (!outgoingTransitionCount(
												initial_state_region,
												statemachine_name))
											valid = false;

										else if (!initialTransitionValidator(
												initial_state_region, smg,
												super_state.getName(),
												r.getName()))
											valid = false;
									}

									else if (s instanceof FinalState) {
										FinalState final_state_region = (FinalState) s;

										if (!nameValidator(statemachine_name,
												super_state.getName(),
												r.getName(),
												final_state_region.getName()))
											valid = false;

										else if (!incomingTransitionCount(
												final_state_region,
												statemachine_name))
											valid = false;

										else if (outgoingTransitionCount(
												final_state_region,
												statemachine_name))
											valid = false;

										else if (!normalTransitionValidator(
												final_state_region, smg,
												super_state.getName(),
												r.getName()))
											valid = false;
									}

									else if (s instanceof NormalState) {
										NormalState normal_state_region = (NormalState) s;

										if (!nameValidator(statemachine_name,
												super_state.getName(),
												r.getName(),
												normal_state_region.getName()))
											valid = false;

										else if (!incomingTransitionCount(
												normal_state_region,
												statemachine_name))
											valid = false;

										else if (!outgoingTransitionCount(
												normal_state_region,
												statemachine_name))
											valid = false;

										else if (!normalTransitionValidator(
												normal_state_region, smg,
												super_state.getName(),
												r.getName()))
											valid = false;
									}
								}

								if (region_inital_count != 1) {
									System.err
											.println("Statemachine -> "
													+ statemachine_name
													+ " | SuperState -> "
													+ super_state.getName()
													+ " | Region -> "
													+ r.getName()
													+ " | Statemachine must have only one initial state.");
									valid = false;
								}
							}
					}
				}
				if (initial_state_count != 1) {
					System.err
							.println("Statemachine -> "
									+ statemachine_name
									+ " | Statemachine must have only one initial state.");
					valid = false;
				}
			}
		}

		//TODO for Rishab: Validation fails without printing ANYTHING! I want to
		//    test my code. Please fix this! Never ever set valid to false without
		//    printing the reason!
		//return valid;
		return true;
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

	private boolean emptyOrNull(String obj) {
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

	private boolean nameValidator(String nametobevalidated, String... names) {
		Pattern digit = Pattern.compile("\\D\\w*");
		String[] name = names;
		boolean validname = true;

		if (!(digit.matcher(nametobevalidated).matches())
				|| emptyOrNull(nametobevalidated)) {
			switch (name.length) {
			case 0:
				if (nametobevalidated != null)
					System.err.println("Statemachine name-> "
							+ nametobevalidated
							+ " is not a valid C identifier!");
				break;
			case 1:
				System.err.println("Statemachine -> " + name[0]
						+ " | State name -> " + nametobevalidated
						+ " is not a valid C identifier!");
				break;
			case 2:
				System.err.println("Statemachine -> " + name[0]
						+ " | SuperState -> " + name[1] + " | Region name "
						+ nametobevalidated + " is not a valid C identfier");
				break;
			case 3:
				System.err.println("Statemachine -> " + name[0]
						+ " | SuperState -> " + name[1] + " | Region -> "
						+ name[2] + " | State name " + nametobevalidated
						+ " is not a valid C identfier");
			}
		} else
			validname = true;

		return validname;
	}

	// event name validator
	private boolean eventNameValidator(String statemachine, String event) {
		Pattern digit = Pattern.compile("\\D\\w*");
		boolean validevent = true;

		if (!(digit.matcher(event).matches()) && (!event.isEmpty())) {
			System.err.println("Statemachine -> " + statemachine
					+ " | Event name -> " + event
					+ " is not a valid C identifier!");
			validevent = false;
		}
		return validevent;
	}

	private boolean initialTransitionValidator(State state,
			StateMachineForGeneration smg, String... names) {

		boolean valid = false;
		for (Transition trans : state.getOutgoingTransitions()) {
			TransitionInfo ti = smg.getTransitionInfo(trans);
			String[] name = names;

			if (ti.isWaitTransition())
				System.err.println("Statemachine -> " + smg.getName()
						+ hasSuperState(name) + " | Transition -> "
						+ "Source : " + trans.getSource() + " Destination : "
						+ trans.getDestination()
						+ " : Start state cannot have waiting transition!");

			else if (!emptyOrNull(ti.getEventName()))
				System.err.println("Statemachine -> " + smg.getName()
						+ hasSuperState(names) + " | Transition -> "
						+ "Source : " + trans.getSource() + " Destination : "
						+ trans.getDestination()
						+ " : Start state cannot have events!");

			else if (!emptyOrNull(ti.getCondition()))
				System.err.println("Statemachine -> " + smg.getName()
						+ hasSuperState(names) + " | Transition -> "
						+ "Source : " + trans.getSource() + " Destination : "
						+ trans.getDestination()
						+ " : Start state cannot have conditions !");
			else
				valid = true;
		}
		return valid;
	}

	private String hasSuperState(String... names) {
		if (names.length == 2) {
			String[] state_region = names;
			return (" | SuperState -> " + state_region[0] + " | Region " + state_region[1]);
		} else
			return "";
	}

	private boolean normalTransitionValidator(State state,
			StateMachineForGeneration smg, String... names) {

		boolean valid = false;
		for (Transition trans : state.getIncomingTransitions()) {
			TransitionInfo ti = smg.getTransitionInfo(trans);
			String[] name = names;

			if (!(state instanceof InitialState)) {
				if (!emptyOrNull(ti.getCondition())
						|| !emptyOrNull(ti.getEventName())) {
					if (ti.getWaitType() == "wait")
						System.err
								.println("Statemachine -> "
										+ smg.getName()
										+ hasSuperState(names)
										+ " | Transition -> Source : "
										+ trans.getSource()
										+ " Destination : "
										+ trans.getDestination()
										+ " Transition having condition or event cannot have wait type wait!");

				} else if (ti.getWaitType() == "before") {
					if (ti.getCondition().isEmpty()
							&& ti.getEventName().isEmpty())
						System.err
								.println("Statemachine -> "
										+ smg.getName()
										+ hasSuperState(names)
										+ " | Transition -> Source : "
										+ trans.getSource()
										+ " Destination : "
										+ trans.getDestination()
										+ " Transition must either have a condition or an event!");
				} else
					valid = true;
			}
		}
		return valid;
	}

	private boolean incomingTransitionCount(State state,
			String statemachine_name) {

		boolean hasincoming = true;
		if (state instanceof FinalState || state instanceof NormalState
				|| state instanceof SuperState)
			if (state.getIncomingTransitions().isEmpty()) {
				System.err.println("Statemachine -> " + statemachine_name
						+ " | State name -> " + state.getName()
						+ " | does not have any incoming transitions!");
				hasincoming = false;
			}
		if (state instanceof InitialState
				&& (state.getIncomingTransitions().isEmpty())) {
			hasincoming = false;
		}
		return hasincoming;
	}

	private void initialHasIncoming(State state, String statemachine_name) {
		System.err.println("Statemachine -> " + statemachine_name
				+ " | State name -> " + ((InitialState) state).getName()
				+ " Initial state cannot have any incoming transitions! ");
	}

	private boolean outgoingTransitionCount(State state,
			String statemachine_name) {

		boolean hasoutgoing = true;
		if (state instanceof FinalState || state instanceof InitialState
				|| state instanceof NormalState || state instanceof SuperState) {
			if (state instanceof InitialState
					&& state.getOutgoingTransitions().size() > 1) {
				System.err.println("Statemachine -> " + statemachine_name
						+ "| State name -> " + state.getName()
						+ " cannot have more than 1 outgoing transitions!");
				hasoutgoing = false;
			} else if (state.getOutgoingTransitions().isEmpty()) {
				// nope, this is valid!!! I've told you several times...
			}
		}
		return hasoutgoing;
	}

	private void finalHasOutgoing(State state, String statemachine_name) {
		System.err.println("Statemachine -> " + statemachine_name
				+ " | State -> " + ((FinalState) state).getName()
				+ " : has outgoing transitions!");
	}

	private void removeFinalStates(MCUConfiguration config) {
		for (StateMachineForGeneration smg : config.getStatemachines()) {

			List<FinalState> final_parent = new ArrayList<FinalState>();
			List<FinalState> final_superstate = new ArrayList<FinalState>();

			for (State state : smg.getStates()) {
				if (state instanceof FinalState) {
					final_parent.add((FinalState) state);
					if (final_parent.size() > 1)
						removeFinalStates(final_parent);

				} else if (state instanceof SuperState) {
					for (Region region : ((SuperState) state).getRegions())
						if (state instanceof FinalState) {
							final_superstate.add((FinalState) state);
							if (final_superstate.size() > 1)
								removeFinalStates(final_superstate);
						}
				}
			}
		}
	}

	private void removeFinalStates(List<FinalState> state) {
		for (Transition transition : state.get(0).getIncomingTransitions()) {
			transition.setDestination(state.get(1));
		}
	}

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

			List<State> state_names = smg.getStates();
			for (State state : state_names) {
				if ((state instanceof InitialState)
						&& emptyOrNull(((InitialState) state).getName()))

					((InitialState) state).setName("initialState_"
							+ runningCounter++);

				else if (state instanceof FinalState
						&& emptyOrNull(((InitialState) state).getName()))

					((FinalState) state).setName("finalState_"
							+ runningCounter++);

				else if (state instanceof StateWithActions) {
					if (state instanceof NormalState
							&& emptyOrNull(((NormalState) state).getName()))

						((NormalState) state).setName("normalState_"
								+ runningCounter++);

					else if (state instanceof SuperState
							&& emptyOrNull(((SuperState) state).getName()))

						((SuperState) state).setName("superState_"
								+ runningCounter++);
				}
			}
		}
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
			if (state instanceof StateParent)
				addStateVariables(smg, (StateParent) state);
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
