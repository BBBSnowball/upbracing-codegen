package de.upbracing.code_generation.generators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;

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
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.fsm.model.StateVariable;
import de.upbracing.code_generation.fsm.model.StateVariablePurposes;
import de.upbracing.code_generation.fsm.model.StateVariables;
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
					System.err.println("FATAL: Each transition must have a valid source and destination. Invalid transition: " + t);
					return false;
				}
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

				if (statemachine_name.startsWith("$"))
					System.err.println(statemachine_name
							+ ": Name cannot start with $.");
				else if (Character.isDigit(statemachine_name.charAt(0)))
					System.err.println(statemachine_name
							+ " : Name cannot start with a digit.");
				else
					System.out
							.println(statemachine_name
									+ " Validation successful for all statemachine names.");

				// state validation
				List<State> states = smg.getStates();

				for (int i = 0; i < states.size(); i++) {
					State state = states.get(i);

					// initial state validity
					if (state instanceof InitialState) {
						InitialState initial_state = (InitialState) state;
						EList<Transition> out_transitions = initial_state
								.getOutgoingTransitions();

						for (Transition t : out_transitions) {
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
							if (transitionInfo.isWaitTransition())
								System.err
										.println("Statemachine "
												+ " -> "
												+ statemachine_name
												+ " | Transition "
												+ " -> "
												+ "Source : "
												+ t.getSource()
												+ " Destination : "
												+ t.getDestination()
												+ " : Start state cannot have waiting transition!");

							else if (!emptyOrNull(transitionInfo.getEventName()))
								System.err.println("Statemachine " + " -> "
										+ statemachine_name + " | Transition "
										+ " -> " + "Source : " + t.getSource()
										+ " Destination : "
										+ t.getDestination()
										+ " : Start state cannot have events!");

							else if (!emptyOrNull(transitionInfo.getCondition()))
								System.err
										.println("Statemachine "
												+ " -> "
												+ statemachine_name
												+ " | Transition "
												+ " -> "
												+ "Source : "
												+ t.getSource()
												+ " Destination : "
												+ t.getDestination()
												+ " : Start state cannot have conditions !");

							else if (initial_state.getOutgoingTransitions().size() > 1) {
								System.err.println("Statemachine " + " -> "
										+ statemachine_name + " | State "
										+ " -> " + initial_state.getName()
										+ " : Validation failed.");
							}
						}

						if (!initial_state.getIncomingTransitions().isEmpty()) {
							System.err.println("Statemachine " + " -> "
									+ statemachine_name + " | State " + " -> "
									+ initial_state.getName()
									+ " : Validation failed.");
						}

						// TODO add initial_state name validation

					}

					// final state validity
					else if (state instanceof FinalState) {
						FinalState final_state = (FinalState) state;
						EList<Transition> in_transitions = final_state
								.getIncomingTransitions();
						// - if an edge has waitType=="before", it must have a
						// condition or an event (or both)

						for (Transition t : in_transitions) {
							TransitionInfo ti = smg.getTransitionInfo(t);
							if (!emptyOrNull(ti.getCondition())
									|| !emptyOrNull(ti.getEventName())) {
								// check wait type is "wait"?
								if (ti.getWaitType() == "wait")
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition having condition or event cannot have wait type wait!");
							}
							// ensure that when wait type is "before" either
							// getcondition is true or event is true
							else if (ti.getWaitType().equals("before")) {
								if (ti.getCondition()
										.isEmpty()
										&& ti
												.getEventName().isEmpty()) {
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition must either have a condition or an event!");
								}
							}
							// check whether there are any outgoing transitions
							else if (!final_state.getOutgoingTransitions()
									.isEmpty()) {
								System.err.println("Statemachine " + " -> "
										+ statemachine_name + " | State "
										+ " -> " + final_state.getName()
										+ " : Validation failed.");
							}
						}

						// TODO add final_state name validation
					}

					// normal state validity
					else if (state instanceof NormalState) {
						NormalState normal_state = (NormalState) state;
						EList<Transition> in_transitions = normal_state
								.getIncomingTransitions();
						EList<Transition> out_transitions = normal_state
								.getIncomingTransitions();

						for (Transition t : in_transitions) {
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
							if (!emptyOrNull(transitionInfo.getCondition())
									|| !emptyOrNull(transitionInfo.getEventName())) {
								if (transitionInfo.getWaitType() == "wait")
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition having condition or event cannot have wait type wait!");
							} else if (transitionInfo.getWaitType() == "before") {
								if (transitionInfo.getCondition()
										.isEmpty()
										&& transitionInfo
												.getEventName().isEmpty()) {
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition must either have a condition or an event!");
								}
							}
						}

						// TODO add normal_state name validation
					}

					// Superstates, regions, and contained states validation
					else if (state instanceof SuperState) {
						SuperState super_state = (SuperState) state;
						EList<Region> super_regions = super_state.getRegions();

						// TODO Superstate and region name validation
						for (Region r : super_regions) {
							EList<State> region_states = r.getStates();
							for (State s : region_states) {
								{

								}
							}
						}

					}

					// TODO event validation
				}
			}
		}
		return valid;
	}

	private boolean validateStatesNotNull(Iterable<State> states) {
		for (State state : states) {
			validateNotNull(state.getIncomingTransitions(), "incoming transitions of " + state);
			validateNotNull(state.getOutgoingTransitions(), "outgoing transitions of " + state);
			
			if (state instanceof SuperState) {
				for (Region region : ((SuperState)state).getRegions()) {
					if (region.getParent() != state) {
						System.err.println("FATAL: Corrupted hierarchy");
						return false;
					}
					
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
				System.err.println("FATAL: At least one element in the list of " + list_name + " is null");
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
				// regions have the right parent without our help
				for (Region region : ((SuperState)state).getRegions()) {
					// but the contained states need some help...
					updateParents(region.getStates(), region);
				}
			}
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
				addStateVariables(smg, (StateParent)state);
	}

	private void addStateVariable(StateMachineForGeneration smg, StateParent parent) {
		StringBuilder declaration = new StringBuilder();
		declaration.append("enum {\n");
		for (State state : smg.sortStates(parent.getStates())) {
			if (state instanceof InitialState)
				continue;
			declaration.append("\t");
			declaration.append(smg.stateName(state));
			declaration.append(",\n");
		}
		declaration.append("}");
		
		StateVariable var = new StateVariable("state", StateVariable.TYPE_AUTONAME,
				declaration.toString(),
				parent);
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

					String timerVar = timerVariableForState(smg, source);
					addCondition(
							tinfo,
							waitConditionFor(timerVar, tinfo,
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

				String timerVar = timerVariableForState(smg, state);

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
					.format("WARN: Actual wait time is off by %0.2%% (%s instead of %s)\n",
							error, FSMParsers.formatTime(ticks * basePeriod),
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

	private String timerVariableForState(StateMachineForGeneration smg,
			StateWithActions state) {
		return StatemachinesCFileTemplate.timeVariableForState(smg, state);
	}
}
