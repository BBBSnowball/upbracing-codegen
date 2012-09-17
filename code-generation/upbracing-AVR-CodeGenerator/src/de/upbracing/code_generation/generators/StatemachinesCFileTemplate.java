package de.upbracing.code_generation.generators;

import java.util.*;
import java.util.Map.Entry;

import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.config.*;
import de.upbracing.code_generation.fsm.model.*;
import de.upbracing.code_generation.fsm.model.StateVariables.AllOf;
import de.upbracing.code_generation.fsm.model.StateVariables.VariableContainer;
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

			generateCodeForGlobalCodeBoxes(stringBuffer, statemachines);

			generateLockMacros(stringBuffer, statemachines);

			for (StateMachineForGeneration smg : statemachines) {
				this.smg = smg;
				
				Collection<State> sorted_states = smg.sortStates(smg.getStates());
				Collection<StateWithActions> states_with_actions
					= smg.filterStatesWithActions(sorted_states);

				stringBuffer.append('\n');
				stringBuffer.append('\n');
				hugeBanner(smg.getName());
				stringBuffer.append('\n');

				generateStatemachineData(stringBuffer, smg, sorted_states);

				generateActionFunctions(stringBuffer, smg, states_with_actions);

				generateInitFunction(stringBuffer, smg);

				generateTickFunction(stringBuffer, smg, states_with_actions);

				generateEventFunctions(stringBuffer, smg, states_with_actions);
			}
		} // for each statemachine

		this.stringBuffer = null;
		this.smg = null;
		this.existing_action_methods = null;

		return stringBuffer.toString();
	} // end of method generate(...)

	private void generateCodeForGlobalCodeBoxes(
			final StringBuffer stringBuffer, StatemachinesConfig statemachines) {
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
					if (!box.getInHeaderFile()) {
						stringBuffer.append('\n');
						stringBuffer.append(box.getCode().replace("###", "\n").trim());
						stringBuffer.append('\n');
					}
				}
			}
		}
	}

	private void generateLockMacros(final StringBuffer stringBuffer,
			StatemachinesConfig statemachines) {
		stringBuffer.append('\n');
		banner("lock definitions");
		stringBuffer.append('\n');

		for (StateMachineForGeneration smg : statemachines) {
			String sm_name = smg.getName();
			String enter = sm_name + "_enter_critical";
			String exit = sm_name + "_exit_critical";
			String semaphore_name = sm_name + "_lock";
			// must be so long and complex that it cannot collide with another variable
			String interrupt_state_variable = "__" + sm_name + "_saved_interrupt_state__";
			switch (smg.getLockMethod()) {
			case NO_LOCK:
				stringBuffer.append("\n// " + smg.getName() + ": no locking\n"
						+ "// Use locks, if you call any method of this statemachine; or make\n"
						+ "// sure they are only called from one thread (no interrupts!).\n"
						+ "#define " + enter + "() /* empty */\n"
						+ "#define " + exit + "() /* empty */\n");
				break;
			case OS:
				stringBuffer.append("\n// " + smg.getName() + ": use OS locks\n"
						+ "#include <OSEK.h>\n"
						+ "#define " + enter + "() OS_ENTER_CRITICAL()\n"
						+ "#define " + exit + "()  OS_EXIT_CRITICAL()\n");
				break;
			case SEMAPHORE:
				stringBuffer.append("\n// " + smg.getName() + ": use a semaphore\n"
						+ "#include <semaphores/semaphores.h>\n"
						+ "// You can adjust the queue length by defining this macro in a global\n"
						+ "// code box. Please note, that you cannot define it in another source file.\n"
						+ "#ifndef " + sm_name + "_SEMAPHORE_QUEUE_LENGTH\n"
						+ "#  define " + sm_name + "_SEMAPHORE_QUEUE_LENGTH 5\n"
						+ "#endif\n"
						+ "SEMAPHORE(" + semaphore_name + ", 1, " + sm_name + "_SEMAPHORE_QUEUE_LENGTH);\n"
						+ "#define " + enter + "() sem_wait(" + semaphore_name + ")\n"
						+ "#define " + exit + "()  sem_signal(" + semaphore_name + ")\n");
				break;
			case INTERRUPT:
				stringBuffer.append("\n// " + smg.getName() + ": disable interrupts\n"
						+ "#include <avr/interrupt.h>\n"
						+ "#include <avr/io.h>\n"
						+ "#define " + enter + "() " + interrupt_state_variable + " = SREG; cli()\n"
						+ "#define " + exit + "()  SREG = " + interrupt_state_variable + "\n");
				break;
			case CUSTOM:
				stringBuffer.append("\n// " + smg.getName() + ": custom locking\n"
						+ "// Please provide the functions or macros " + enter + " and\n"
						+ "// " + exit + ". They must be available in this file, so you have to\n"
						+ "// use a global code box to declare them.\n");
				break;
			}
		}
	}

	private void generateStatemachineData(final StringBuffer stringBuffer,
			StateMachineForGeneration smg, Collection<State> sorted_states) {
		stringBuffer.append('\n');
		banner("data");
		stringBuffer.append('\n');

		String sm_name = smg.getName();

		/*stringBuffer.append("typedef enum {\n");
		for (State state : smg.sortStatesForEnum(sorted_states)) {
			if (!(state instanceof InitialState))
				stringBuffer.append("\t" + stateName(state) + ",\n");
		}
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
		stringBuffer.append('\n');*/
		
		VariableContainer container = smg.getStateVariables().planStructure(sm_name + "_state");
		generateCodeForNamedTypesInConatiner(container);

		String statemachine_root_data_type = sm_name + "_state_var_t";
		stringBuffer.append("\ntypedef ");
		generateCodeForVariableContainer("", container);
		stringBuffer.append(" " + statemachine_root_data_type + ";\n");
		
		stringBuffer.append('\n');
		if (!smg.isForTest())
			stringBuffer.append("static ");
		stringBuffer.append(statemachine_root_data_type + " " + container.name + ";\n");
	}

	private void generateCodeForNamedTypesInConatiner(
			VariableContainer container) {
		
		for (StateVariable var : container.variables) {
			if (var.getType() != null && !var.getType().isEmpty() && var.getDeclaration() != null && !var.getDeclaration().isEmpty()) {
				stringBuffer.append("typedef ");
				stringBuffer.append(var.getDeclaration());
				stringBuffer.append(" ");
				stringBuffer.append(var.getType());
				stringBuffer.append(";\n");
			}
		}
		
		for (VariableContainer child : container.children) {
			generateCodeForNamedTypesInConatiner(child);
		}
	}

	private void generateCodeForVariableContainer(String indent, VariableContainer container) {
		if (container instanceof AllOf)
			stringBuffer.append("struct");
		else
			stringBuffer.append("union");
		
		if (container.name != null && !container.name.isEmpty())
			stringBuffer.append(" " + container.name);
		
		stringBuffer.append(" {\n");
		
		for (StateVariable var : container.variables) {
			stringBuffer.append(indent + "\t");
			
			if (var.getType() != null && !var.getType().isEmpty())
				stringBuffer.append(var.getType());
			else
				stringBuffer.append(var.getDeclaration().replace("\n", "\n" + indent));
			
			String name = container.variable_names.get(var);
			if (name == null)
				name = var.getName();
			stringBuffer.append(' ');
			//TODO This is not the right name. We must use the one from the names map inside PlanStructures.
			stringBuffer.append(name);
			stringBuffer.append(";\n");
		}
		
		for (VariableContainer child : container.children) {
			stringBuffer.append(indent + "\t");
			generateCodeForVariableContainer(indent + "\t", child);
			stringBuffer.append(' ');
			stringBuffer.append(child.name);
			stringBuffer.append(";\n");
		}
		
		stringBuffer.append(indent + "}");
	}

	private void generateActionFunctions(final StringBuffer stringBuffer,
			StateMachineForGeneration smg,
			Collection<StateWithActions> states_with_actions) {
		stringBuffer.append('\n');
		banner("action functions");
		stringBuffer.append('\n');

		this.existing_action_methods = new HashSet<String>();
		for (StateWithActions state : states_with_actions) {
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
					genTrace(70, "\t", method_name + "()");
					printActions("\t", actions);
					stringBuffer.append("}\n");
				}
			}
		}
	}

	private void generateInitFunction(final StringBuffer stringBuffer,
			StateMachineForGeneration smg) {
		stringBuffer.append('\n');
		banner("init function");
		stringBuffer.append('\n');
		
		String sm_name = smg.getName();
		State firstState = findFirstState(smg.getStates());

		stringBuffer.append("void " + sm_name + "_init() {\n");
		
		stringBuffer.append("\t" + sm_name + "_enter_critical();\n\n");

		if (firstState == null) {
			stringBuffer
					.append("#error Cannot find a first state. Please add an InitialState which has exactly one edge to a normal state.\n");
		} else {
			genTrace(10,  "\t","$name_init()");
			
			genTrace(30,  "\t","$name: state <- " + stateName(firstState));
			stringBuffer.append("\t" + getStateVariableName(firstState.getParent()) + " = "
					+ stateName(firstState) + ";\n");
			printActionsFor("\t", null, firstState, firstState);

			for (Transition t : smg.getTransitions()) {
				if (t.getSource() instanceof InitialState
						&& t.getDestination() == firstState)
					printCode("\t", smg.getTransitionInfo(t)
							.getAction());
			}
		}

		
		stringBuffer.append("\n\t" + sm_name + "_exit_critical();\n");

		stringBuffer.append("}\n");
	}

	private String getStateVariableName(StateParent containing_state) {
		return smg.getStateVariables().getVariable(containing_state, StateVariablePurposes.STATE).getRealName();
	}

	private void generateTickFunction(final StringBuffer stringBuffer,
			StateMachineForGeneration smg,
			Collection<StateWithActions> states_with_actions) {
		stringBuffer.append('\n');
		banner("tick function");
		stringBuffer.append('\n');
		
		// generate tick function
		// null means no event -> tick function
		// We have to use "" instead of null for the map of events, because
		// Java/JRuby chokes on a null value.
		generateEventFunction(null, states_with_actions, smg.getEvents().get(""));
	}

	private void generateEventFunctions(final StringBuffer stringBuffer,
			StateMachineForGeneration smg,
			Collection<StateWithActions> states_with_actions) {
		stringBuffer.append('\n');
		banner("event functions");
		
		for (Entry<String, Set<Transition>> entry : smg.getEvents().entrySet()) {
			String event = entry.getKey();
			Set<Transition> transitions = entry.getValue();
			
			if (event == null || event.equals(""))
				// skip transitions which are handled by the tick function
				continue;

			stringBuffer.append('\n');
			generateEventFunction(event, states_with_actions, transitions);
		}
	}

	private StringBuffer stringBuffer;
	private StateMachineForGeneration smg;
	private Set<String> existing_action_methods;

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

	public String getName(Object state) {
		return smg.getName(state);
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
		return smg.stateName(state);
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
				genTrace(30, indent, "$name: state <- " + stateName(trans.getDestination()));
				//TODO We have to do some more things, if source and destination don't have the same parent.
				stringBuffer.append(indent + getStateVariableName(trans.getSource().getParent()) + " = "
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
		
		code = StateVariable.replaceTemporaryVariables(code);
		
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
		return smg.findFirstState(states);
	}

	@SuppressWarnings("unused")
	private State findFinalState(List<State> states) {
		return smg.findFinalState(states);
	}

	private String formatTime(double time) {
		return FSMParsers.formatTime(time);
	}

	private void generateEventFunction(String event, Collection<StateWithActions> states_with_actions, Iterable<Transition> transitions) {
		String sm_name = smg.getName();
		if (transitions == null)
			transitions = smg.getTransitions();
		
		stringBuffer.append("void " + sm_name + "_" + (event != null ? "event_" + event : "tick") + "() {\n");

		stringBuffer.append("\t" + sm_name + "_enter_critical();\n\n");
		
		if (event == null) {
			if (genTrace(100, "\t", "$name_tick()"))
				stringBuffer.append('\n');
		} else {
			if (genTrace(50, "\t", "$name_event_" + event))
				stringBuffer.append('\n');
		}
		
		stringBuffer.append("\tswitch (" + getStateVariableName(smg.getStateMachine()) + ") {\n");
		stringBuffer.append('\n');
		
		for (StateWithActions state : states_with_actions) {
			stringBuffer.append("\tcase " + stateName(state) + ":\n");
			
			if (event == null) {
				// If we are generating the tick function, print actions
				// that should be executed for the state.
				if (executeDuringState("\t\t", state))
					stringBuffer.append('\n');
			}
			
			SortedSet<Transition> ts = smg.sortedTransitionSet();
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
		stringBuffer.append("\n\t" + sm_name + "_exit_critical();\n");
		stringBuffer.append("}\n");
	}

	private boolean genTrace(int level, String indent, String message) {
		if (!smg.shouldPrintTraceForLevel(level))
			return false;
		
		message += "\n";
		
		message = message.replace("$name", smg.getName());
		
		message = message
				.replace("\\", "\\\\")
				.replace("\r", "\\r")
				.replace("\n", "\\n")
				.replace("\t", "\\t")
				.replace("\'", "\\\'")
				.replace("\"", "\\\"");
		
		stringBuffer.append(indent + 
				smg.getTracePrinter() + "_P(PSTR(\""
				+ message + "\"));\n");
		
		return true;
	}
}
