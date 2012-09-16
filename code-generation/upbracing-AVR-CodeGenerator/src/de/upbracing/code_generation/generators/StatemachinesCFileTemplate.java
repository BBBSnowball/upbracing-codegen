package de.upbracing.code_generation.generators;

import java.util.*;
import java.util.Map.Entry;

import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.config.*;
import de.upbracing.code_generation.fsm.model.*;
import Statecharts.*;

//NOTE This used to be a JET template, but I was using stringBuffer.append(...) anyway and with code completion it's MUCH easier :-)
public class StatemachinesCFileTemplate implements ITemplate {

	protected static String nl;

	public static synchronized StatemachinesCFileTemplate create(String lineSeparator) {
		StatemachinesCFileTemplate result = new StatemachinesCFileTemplate();
		return result;
	}

	public void warn(StringBuffer stringBuffer, String message) {
		stringBuffer.append("\n#warning ");
		stringBuffer.append(message);
		stringBuffer.append("\n");
	}

	/*
	 * (non-javadoc)
	 * 
	 * @see IGenerator#generate(Object)
	 */
	public String generate(MCUConfiguration config, Object generator_data) {
		final StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("/*\n * statemachines.c\n *\n * This file defines all statemachines.\n *\n"
				+" * Generated automatically. DO NOT MODIFY! Change config.rb instead.\n */\n\n");

		if (config.getStatemachines().isEmpty()) {

			stringBuffer.append("// no statemachines loaded\n"
					+ "// use $config.statemachines.load(statechart_file)\n");

		} else {

			this.stringBuffer = stringBuffer;

			StatemachinesConfig statemachines = config.getStatemachines();

			stringBuffer.append("#include \"statemachines.h\"\n");

			stringBuffer.append('\n');
			banner("code for statemachines");
			stringBuffer.append('\n');

			for (StateMachineForGeneration smg : statemachines) {
				String sm_name = smg.getName();

				if (smg.hasCFileCodeBoxes()) {

					stringBuffer.append("\n// code from global code boxes in statemachine ");
					stringBuffer.append(sm_name);
					stringBuffer.append("\n");

					for (GlobalCode box : smg.getGlobalCodeBoxes()) {
						if (!box.getInHeaderFile())
							stringBuffer.append(box.getCode().replace("###", "\n"));
					}
				}
			} // has code boxes for C file

			for (StateMachineForGeneration smg : statemachines) {
				this.smg = smg;
				String sm_name = smg.getName();

				final Comparator<State> cmpStates =
						new Comparator<State>() {
							@Override
							public int compare(State a,
									State b) {
								String nameA = getName(a);
								String nameB = getName(b);
		
								int cmp = nameA.compareToIgnoreCase(nameB);
								if (cmp != 0)
									return cmp;
								else
									return -nameA.compareTo(nameB);
							}
						};
				cmpTransitions =
						new Comparator<Transition>() {
							@Override
							public int compare(Transition t1, Transition t2) {
								int cmp = t1.getPriority() - t2.getPriority();
								if (cmp != 0)
									return cmp;
								
								cmp = cmpStates.compare(t1.getDestination(), t2.getDestination());
								if (cmp != 0)
									return cmp;
								
								cmp = t1.getTransitionInfo().compareTo(t2.getTransitionInfo());
								return cmp;
							}
						};
				SortedSet<StateWithActions> states = new TreeSet<StateWithActions>(cmpStates);
				for (State state : smg.getStates()) {
					if (state instanceof StateWithActions)
						states.add((StateWithActions) state);
				}

				stringBuffer.append('\n');
				stringBuffer.append('\n');
				hugeBanner(sm_name);
				stringBuffer.append('\n');

				stringBuffer.append('\n');
				banner("data");
				stringBuffer.append('\n');

				stringBuffer.append("typedef enum {\n");
				State firstState = findFirstState(smg.getStates());
				State finalState = findFinalState(smg.getStates());
				if (firstState != null)
					stringBuffer.append("\t" + stateName(firstState) + ",\n");
				for (State state : states) {
					if (state != firstState && state != finalState)
						stringBuffer.append("\t" + stateName(state) + ",\n");
				}
				if (finalState != null)
					stringBuffer.append("\t" + stateName(finalState) + ",\n");
				stringBuffer.append("} " + sm_name + "_state_t;\n");

				stringBuffer.append('\n');
				stringBuffer.append("typedef struct {\n"
						+ "\tcounter_state_t state;\n"
						+ "\t//TODO only include time variable, if we need it\n"
						+ "\tuint8_t state_time;\n"
						+ "} " + sm_name + "_state_var_t;\n");
				if (!smg.isForTest())
					stringBuffer.append("static ");
				stringBuffer.append(sm_name + "_state_var_t " + sm_name + ";");
				stringBuffer.append('\n');

				stringBuffer.append('\n');
				banner("action functions");
				stringBuffer.append('\n');

				this.existing_action_methods = new HashSet<String>();
				for (StateWithActions state : states) {
					String name = getName(state);

					for (ActionType actionType : ActionType.values()) {
						List<Action> actions = filterActionsByType(
								smg.getActions(state), actionType);

						if (actions != null && !actions.isEmpty()) {
							String method_name = actionMethod(name, actionType);
							existing_action_methods.add(method_name);

							stringBuffer.append('\n');
							stringBuffer.append("static void " + method_name
									+ "() {\n");
							printActions("\t", actions);
							stringBuffer.append("}\n");
						}
					}
				}

				stringBuffer.append('\n');
				banner("init function");
				stringBuffer.append('\n');

				stringBuffer.append("void " + sm_name + "_init() {\n");

				if (firstState == null) {
					stringBuffer
							.append("#error Cannot find a first state. Please add an InitialState which has exactly one edge to a normal state.\n");
				} else {
					stringBuffer.append("\t" + sm_name + ".state = "
							+ stateName(firstState) + ";\n");
					printActionsFor("\t", null, firstState, firstState);

					for (Transition t : smg.getTransitions()) {
						if (t.getSource() instanceof InitialState
								&& t.getDestination() == firstState)
							printCode("\t", smg.getTransitionInfo(t)
									.getAction());
					}
				}

				stringBuffer.append("}\n");

				stringBuffer.append('\n');
				banner("tick function");
				stringBuffer.append('\n');
				
				// generate tick function
				// null means no event -> tick function
				// We have to use "" instead of null for the map of events, because
				// Java/JRuby chokes on a null value.
				generateEventFunction(null, states, smg.getEvents().get(""));

				stringBuffer.append('\n');
				banner("event functions");
				stringBuffer.append('\n');
				
				for (Entry<String, Set<Transition>> entry : smg.getEvents().entrySet()) {
					String event = entry.getKey();
					Set<Transition> transitions = entry.getValue();
					
					if (event == null || event.equals(""))
						// skip transitions which are handled by the tick function
						continue;
					
					generateEventFunction(event, states, transitions);
					stringBuffer.append('\n');
				}
			}
		} // for each statemachine

		this.stringBuffer = null;
		this.smg = null;
		this.existing_action_methods = null;
		this.cmpTransitions = null;

		return stringBuffer.toString();
	} // end of method generate(...)

	private StringBuffer stringBuffer;
	private StateMachineForGeneration smg;
	private Set<String> existing_action_methods;
	private Comparator<Transition> cmpTransitions;

	@SuppressWarnings("unused")
	private void times(int n, String s) {
		for (int i = 0; i < n; i++)
			stringBuffer.append(s);
	}

	private void times(int n, char c) {
		for (int i = 0; i < n; i++)
			stringBuffer.append(c);
	}

	private void banner(String message) {
		int width = 50;
		int padding = 4;

		// split message into lines
		String lines[] = message.split("\n");

		// increase width, if any line is too long
		for (String line : lines)
			if (line.length() > width - padding)
				width = line.length() + padding;

		// print banner
		times(width, '/');
		stringBuffer.append('\n');
		for (String line : lines) {
			stringBuffer.append("//");
			int whitespace = width - 4 - line.length();
			times(whitespace / 2, ' ');
			stringBuffer.append(line);
			times(whitespace - whitespace / 2, ' ');
			stringBuffer.append("//\n");
		}
		times(width, '/');
		stringBuffer.append('\n');
	}

	private void hugeBanner(String message) {
		int width = 50;
		int padding = 4;

		// split message into lines
		String lines[] = message.split("\n");

		// increase width, if any line is too long
		for (String line : lines)
			if (line.length() > width - padding)
				width = line.length() + padding;

		// print banner
		times(width, '/');
		stringBuffer.append('\n');
		times(width, '/');
		stringBuffer.append('\n');
		stringBuffer.append("/////");
		times(width - 10, ' ');
		stringBuffer.append("/////\n");
		for (String line : lines) {
			stringBuffer.append("//");
			int whitespace = width - 4 - line.length();
			times(whitespace / 2, ' ');
			stringBuffer.append(line);
			times(whitespace - whitespace / 2, ' ');
			stringBuffer.append("//\n");
		}
		stringBuffer.append("/////");
		times(width - 10, ' ');
		stringBuffer.append("/////\n");
		times(width, '/');
		stringBuffer.append('\n');
		times(width, '/');
		stringBuffer.append('\n');
	}

	public static String getName(Object state) {
		if (state instanceof NamedItem)
			return ((NamedItem) state).getName();
		else if (state instanceof StateMachine)
			return "#diagram";
		else if (state instanceof Transition) {
			Transition t = (Transition) state;
			return "transition(" + getName(t.getSource()) + " -> "
					+ getName(t.getDestination()) + ")";
		} else if (state == null)
			return "(null)";
		else
			throw new RuntimeException("should not get here, unexpected object is " + state);
	}

	private List<Action> filterActionsByType(List<Action> actions,
			ActionType type) {
		List<Action> actions2 = new LinkedList<Action>();
		for (Action action : actions)
			if (action.getType() == type)
				actions2.add(action);
		return actions2;
	}

	private String actionMethod(String state_name, ActionType actionType) {
		return smg.getName() + "_" + state_name + "_"
				+ actionType.toString().toLowerCase();
	}

	private String stateName(State state) {
		return smg.getName() + "_" + getName(state) + "_state";
	}

	public static String timeVariableForState(StateMachineForGeneration smg,
			StateWithActions state) {
		return smg.getName() + ".state_time";
	}

	@SuppressWarnings("unused")
	private String timeVariableForState(StateWithActions state) {
		return timeVariableForState(smg, state);
	}

	private boolean printActions(String indent, List<Action> actions) {
		boolean printedSomething = false;
		int i = 0;
		for (Action action : actions) {
			printedSomething |= printCode(indent, action.getAction());
			if (++i < actions.size())
				stringBuffer.append('\n');
		}
		return printedSomething;
	}

	@SuppressWarnings("unused")
	private boolean printActions(String indent, Action action) {
		return printActions(indent, Arrays.asList(action));
	}

	private boolean printActionsFor(String indent, State source,
			State destination, StateWithActions self) {
		/*
		 * List<Action> active_actions = new LinkedList<Action>();
		 * 
		 * for (Action action : smg.getActions(self)) if
		 * (action.getType().shouldExecuteFor(source, destination, self))
		 * active_actions.add(action);
		 * 
		 * return printActions(indent, active_actions);
		 */

		String state_name = getName(self);

		StringBuffer sb = new StringBuffer();
		for (ActionType action_type : ActionType.values()) {
			String method_name = actionMethod(state_name, action_type);
			if (action_type.shouldExecuteFor(source, destination, self)
					&& existing_action_methods.contains(method_name))
				sb.append(method_name + "();\n");
		}

		return printCode(indent, sb.toString());
	}

	private boolean printActionsFor(String indent, State source,
			State destination, State self) {
		if (self instanceof StateWithActions)
			return printActionsFor(indent, source, destination,
					(StateWithActions) self);
		else
			return false;
	}
	
	private boolean executeDuringState(String indent, State state) {
		boolean printedCode = false;
		
		for (ActionType type : ActionType.values()) {
			String actionMethodName = actionMethod(getName(state), type);
			if (type.shouldExecuteFor(null, state) && existing_action_methods.contains(actionMethodName))
				printedCode |= printCode(indent, actionMethodName + "();\n");
		}
		
		return printedCode;
	}

	private void executeTransition(String indent, Transition trans) {
		stringBuffer.append(indent + "// " + getName(trans.getSource()) + " -> " + getName(trans.getDestination()) + "\n");
		for (ActionType type : ActionType.values()) {
			if (type == ActionType.DURING) {
				// this is a good place to execute transition actions
				
				// set state variable
				stringBuffer.append(indent + smg.getName() + ".state = "
						+ stateName(trans.getDestination()) + ";\n");
				
				// execute transition action
				printCode(indent, smg.getTransitionInfo(trans).getAction());
			}
			
			State self = trans.getSource();
			String actionMethodName = actionMethod(getName(self), type);
			if (type.shouldExecuteFor(trans, self) && existing_action_methods.contains(actionMethodName))
				printCode(indent, actionMethodName + "();\n");

			if (trans.getSource() != trans.getDestination()) {
				self = trans.getDestination();
				actionMethodName = actionMethod(getName(self), type);
				if (type.shouldExecuteFor(trans, self) && existing_action_methods.contains(actionMethodName))
					printCode(indent, actionMethodName + "();\n");
			}
		}
	}

	private boolean printCode(String indent, String code, boolean inline) {
		if (code == null)
			return false;
		
		String lines[] = code.trim().split("\n+");

		if (lines.length <= 0 || lines.length == 1
				&& lines[0].trim().equals(""))
			return false;

		for (int i = 0; i < lines.length; i++) {
			if (i > 0 || !inline)
				stringBuffer.append(indent);

			String line = lines[i];
			stringBuffer.append(line.trim());

			if (i == lines.length - 1 && !inline) {
				// add semicolon after last line, if appropiate
				if (line.length() > 0 && line.charAt(line.length() - 1) != '}'
						&& line.charAt(line.length() - 1) != ';')
					stringBuffer.append(';');
			}

			if (!inline || i < lines.length - 1)
				stringBuffer.append('\n');
		}

		return true;
	}

	private boolean printCode(String indent, String code) {
		return printCode(indent, code, false);
	}

	private State findFirstState(List<State> states) {
		List<Transition> transitions = smg.getTransitions();
		State finalStateIsAlsoFirst = null;

		for (State state : states) {
			if (state instanceof InitialState) {
				for (Transition t : transitions) {
					if (t.getSource() == state
							&& t.getDestination() instanceof StateWithActions)
						return t.getDestination();
					else if (t.getSource() == state
							&& t.getDestination() instanceof FinalState)
						finalStateIsAlsoFirst = t.getDestination();
				}
			}
		}

		return finalStateIsAlsoFirst;
	}

	private State findFinalState(List<State> states) {
		for (State state : states) {
			if (state instanceof FinalState)
				return state;
		}

		return null;
	}

	private String formatTime(double time) {
		return FSMParsers.formatTime(time);
	}

	private void generateEventFunction(String event, SortedSet<StateWithActions> states, Iterable<Transition> transitions) {
		String sm_name = smg.getName();
		if (transitions == null)
			transitions = smg.getTransitions();
		
		stringBuffer.append("void " + sm_name + "_" + (event != null ? "event_" + event : "tick") + "() {\n");
		stringBuffer.append("\tswitch (" + sm_name + ".state) {\n");
		stringBuffer.append('\n');
		
		for (StateWithActions state : states) {
			stringBuffer.append("\tcase " + stateName(state) + ":\n");
			
			if (event == null) {
				// If we are generating the tick function, print actions
				// that should be executed for the state.
				if (executeDuringState("\t\t", state))
					stringBuffer.append('\n');
			}
			
			SortedSet<Transition> ts = new TreeSet<Transition>(cmpTransitions);
			for (Transition t : transitions) {
				if (t.getSource() == state) {
					boolean isForThisEvent;
					
					// equals fails for the null value, the '==' operator doesn't work for objects
					// -> use the appropiate one depending on the value of event
					if (event != null)
						isForThisEvent = smg.getTransitionInfo(t).getEventName().equals(event);
					else
						isForThisEvent = smg.getTransitionInfo(t).getEventName() == null;
					
					if (isForThisEvent)
						ts.add(t);
				}
			}
			
			boolean first = true;
			for (Transition t : ts) {
				TransitionInfo tinfo = smg.getTransitionInfo(t);
				
				stringBuffer.append("\t\t");
				if (first)
					first = false;
				else
					stringBuffer.append("} else ");
				
				stringBuffer.append("if (");
				String condition = tinfo.getCondition();
				if (condition == null || condition.trim().equals(""))
					condition = "1";
				printCode("\t\t\t\t\t", condition, true);
				stringBuffer.append(") {");
				if (tinfo.isWaitTransition()) {
					// print some documentation
					stringBuffer.append("  // " + tinfo.getWaitType() + "(" + formatTime(tinfo.getWaitTime()) + ")");
					/*if (tinfo.getCondition() != null && !condition.trim().equals(""))
						// not so useful because it contains the rewritten wait condition
						stringBuffer.append(" [" + tinfo.getCondition() + "]");*/
				}
				stringBuffer.append('\n');
				executeTransition("\t\t\t", t);
			}
			if (!first)
				stringBuffer.append("\t\t}\n\n");
			
			stringBuffer.append("\t\tbreak;\n");
			stringBuffer.append('\n');
		}

		stringBuffer.append("\t}\n");
		stringBuffer.append("}\n");
	}
}
