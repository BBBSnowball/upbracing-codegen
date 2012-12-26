package de.upbracing.code_generation.generators.fsm;

import java.util.*;
import java.util.Map.Entry;

import statemachine.*;

import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.config.*;
import de.upbracing.code_generation.fsm.model.*;
import de.upbracing.code_generation.fsm.model.StateVariables.AllOf;
import de.upbracing.code_generation.fsm.model.StateVariables.VariableContainer;
import de.upbracing.code_generation.utils.Util;
import static de.upbracing.code_generation.common.Times.formatTime;
import static de.upbracing.code_generation.fsm.model.StateMachineForGeneration.*;

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
			
			printWarningsAndErrors(stringBuffer, config.getMessages());

			generateCodeForGlobalCodeBoxes(stringBuffer, statemachines);

			generateLockMacros(stringBuffer, statemachines);

			for (StateMachineForGeneration smg : statemachines) {
				this.smg = smg;
				
				stringBuffer.append('\n');
				stringBuffer.append('\n');
				hugeBanner(smg.getName());
				stringBuffer.append('\n');

				generateStatemachineData(stringBuffer, smg);

				generateActionFunctions(stringBuffer, smg);

				generateInitFunction(stringBuffer, smg);

				generateTickFunction(stringBuffer, smg, smg.getStateMachine());

				generateEventFunctions(stringBuffer, smg, smg.getStateMachine());
			}
		} // for each statemachine

		this.stringBuffer = null;
		this.smg = null;
		this.existing_action_methods = null;

		return Util.fixNL(stringBuffer.toString());
	} // end of method generate(...)

	private void printWarningsAndErrors(StringBuffer stringBuffer,
			Messages messages) {
		if (!messages.isEmpty()) {
			stringBuffer.append('\n');
			banner("messages from validation and config update");
			stringBuffer.append('\n');
			
			messages.summarizeForCode(stringBuffer);
		}
	}

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
			StateMachineForGeneration smg) {
		stringBuffer.append('\n');
		banner("data");
		stringBuffer.append('\n');

		String sm_name = smg.getName();
		
		VariableContainer container = smg.getStateVariables().planStructure(sm_name + "_state");
		generateCodeForNamedTypesInContainer(container);

		String statemachine_root_data_type = sm_name + "_state_var_t";
		stringBuffer.append("\ntypedef ");
		generateCodeForVariableContainer("", container);
		stringBuffer.append(" " + statemachine_root_data_type + ";\n");
		
		stringBuffer.append('\n');
		if (!smg.isForTest())
			stringBuffer.append("static ");
		stringBuffer.append(statemachine_root_data_type + " " + container.name + ";\n");
	}

	private void generateCodeForNamedTypesInContainer(
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
			generateCodeForNamedTypesInContainer(child);
		}
	}

	private void generateCodeForVariableContainer(String indent, VariableContainer container) {
		if (container instanceof AllOf)
			stringBuffer.append("struct");
		else
			stringBuffer.append("union");
		
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
			StateMachineForGeneration smg) {
		stringBuffer.append('\n');
		banner("action functions");
		stringBuffer.append('\n');

		this.existing_action_methods = new HashSet<String>();
		
		generateActionFunctions(stringBuffer, smg, smg.getStateMachine());
	}

	private void generateActionFunctions(final StringBuffer stringBuffer,
			StateMachineForGeneration smg,
			StateParent parent) {
		//NOTE a SuperState will return all the states in its regions
		Collection<State> sorted_states = smg.sortStates(parent.getStates());

		for (State state_ : sorted_states) {
			if (state_ instanceof StateWithActions) {
				StateWithActions state = (StateWithActions) state_;
	
				for (ActionType actionType : ActionType.values()) {
					List<Action> actions = filterActionsByType(
							smg.getActions(state), actionType);
	
					if (actions != null && !actions.isEmpty()) {
						String method_name = actionMethod(state, actionType);
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
			
			if (state_ instanceof StateParent) {
				StateParent state = (StateParent) state_;
				
				generateActionFunctions(stringBuffer, smg, state);
			}
		}
	}

	private void generateInitFunction(final StringBuffer stringBuffer,
			StateMachineForGeneration smg) {
		stringBuffer.append('\n');
		banner("init function");
		stringBuffer.append('\n');
		
		String sm_name = smg.getName();
		//Transition initial_transition = smg.findInitialTransition(smg.getStates());

		stringBuffer.append("void " + sm_name + "_init() {\n");
		
		stringBuffer.append("\t" + sm_name + "_enter_critical();\n\n");

		/*if (initial_transition == null) {
			stringBuffer.append("#error Cannot find a first state. Please add an InitialState which has exactly one edge to a normal state.\n");
		} else {
			genTrace(10,  "\t","$name_init()");
			
			State firstState = initial_transition.getDestination();
			
			genTrace(30,  "\t","$name: state <- " + stateName(firstState));
			stringBuffer.append("\t" + getStateVariableName(firstState.getParent()) + " = "
					+ stateName(firstState) + ";\n");
			printActionsFor("\t", null, firstState, firstState);

			printCode("\t", smg.getTransitionInfo(initial_transition)
					.getAction());
		}*/
		
		for (ActionType type : ActionType.values()) {
			printInitialActionsForChildren("\t", type, smg.getStateMachine());
		}

		
		stringBuffer.append("\n\t" + sm_name + "_exit_critical();\n");

		stringBuffer.append("}\n");
	}

	private String getStateVariableName(StateParent containing_state) {
		StateVariable state_variable = smg.getStateVariables()
				.getVariable(containing_state, StateVariablePurposes.STATE);
		if (state_variable == null)
			System.out.println(containing_state + " => is null");
		return state_variable.getRealName();
	}

	private void generateTickFunction(final StringBuffer stringBuffer,
			StateMachineForGeneration smg,
			StateParent parent) {
		stringBuffer.append('\n');
		banner("tick function");
		stringBuffer.append('\n');
		
		// generate tick function
		// null means no event -> tick function
		// We have to use "" instead of null for the map of events, because
		// Java/JRuby chokes on a null value.
		generateEventFunction(null, parent, smg.getEvents().get(""));
	}

	private void generateEventFunctions(final StringBuffer stringBuffer,
			StateMachineForGeneration smg, StateParent parent) {
		stringBuffer.append('\n');
		banner("event functions");
		
		for (Entry<String, Set<Transition>> entry : smg.getEvents().entrySet()) {
			String event = entry.getKey();
			Set<Transition> transitions = entry.getValue();
			
			if (event == null || event.equals(""))
				// skip transitions which are handled by the tick function
				continue;

			stringBuffer.append('\n');
			generateEventFunction(event, parent, transitions);
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

	private List<Action> filterActionsByType(List<Action> actions,
			ActionType type) {
		List<Action> actions2 = new LinkedList<Action>();
		for (Action action : actions)
			if (action.getType() == type)
				actions2.add(action);
		return actions2;
	}

	private String actionMethod(State state, ActionType actionType) {
		String state_name = getFullStateName(state);
		
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
	
	private boolean executeDuringState(String indent, State state) {
		boolean printedCode = false;
		
		for (ActionType type : ActionType.values()) {
			String actionMethodName = actionMethod(state, type);
			if (type.shouldExecuteFor(null, state) && existing_action_methods.contains(actionMethodName))
				printedCode |= printCode(indent, actionMethodName + "();\n");
		}
		
		return printedCode;
	}

	private void executeTransition(String indent, Transition trans) {
		stringBuffer.append(indent + "// " + getName(trans.getSource()) + " -> " + getName(trans.getDestination()) + "\n");
		
		// If this transition goes across superstates, we may have to run actions for the parents as well. This
		// can go up to the level below the common parent.
		StateScope common_parent = StateVariable.findCommonParent(trans.getSource(), trans.getDestination());
		List<StateWithActions> source_with_parents = getStatesBelow(trans.getSource(), common_parent);
		List<StateWithActions> destination_with_parents = getStatesBelow(trans.getDestination(), common_parent);
		// For the source, we start at the bottom (deepest state), but we must enter the destination top-down.
		// Therefore, we change the order of that list.
		Collections.reverse(destination_with_parents);
		
		// If source and destination is the same state, both lists will be
		// empty. We don't want to execute actions more than once, so we
		// add the state to exactly one of the lists.
		// We add it to the source list because the destination list is also
		// used for setting the state variables.
		if (source_with_parents.isEmpty() && destination_with_parents.isEmpty()) {
			State source = trans.getSource();
			if (source instanceof StateWithActions) {
				source_with_parents.add((StateWithActions)trans.getSource());
			}
		}
		
		//TODO All states in the parents lists may have other regions. We have to
		//     generate action switch-case statements for them.
		
		for (ActionType type : ActionType.values()) {
			if (type == ActionType.DURING) {
				// this is a good place to execute transition actions
				
				// set state variable
				if (!destination_with_parents.isEmpty())
					genTrace(30, indent, "$name: state <- " + stateName(trans.getDestination()));
				else
					genTrace(40, indent, "$name: staying in state " + stateName(trans.getDestination()));
				for (StateWithActions state : destination_with_parents) {
					stringBuffer.append(indent + getStateVariableName(state.getParent()) + " = "
							+ stateName(state) + ";\n");
				}
				
				//TODO If destinations_with_parents.last() has regions, we must enter the initial state in each of them.
				
				// execute transition action
				printCode(indent, smg.getTransitionInfo(trans).getAction());
			}
			
			printActionsForChildren(indent, type, trans.getSource(), trans.getSource(), trans.getDestination(), true);
			
			for (State self : source_with_parents) {
				String actionMethodName = actionMethod(self, type);
				if (type.shouldExecuteFor(self, trans.getDestination(), self) && existing_action_methods.contains(actionMethodName))
					printCode(indent, actionMethodName + "();\n");
			}

			for (State self : destination_with_parents) {
				String actionMethodName = actionMethod(self, type);
				if (type.shouldExecuteFor(trans.getSource(), self, self) && existing_action_methods.contains(actionMethodName))
					printCode(indent, actionMethodName + "();\n");
			}
			
			printInitialActionsForChildren(indent, type, trans.getDestination());
		}
	}

	private List<StateWithActions> getStatesBelow(State state,
			StateScope common_parent) {
		List<StateWithActions> parents = new LinkedList<StateWithActions>();
		StateScope state2 = state;
		while (state2 != null && state2 != common_parent) {
			if (state2 instanceof StateWithActions)
				parents.add((StateWithActions)state2);
			
			state2 = state2.getParent();
		}
		return parents;
	}

	private void printActionsForStateAndChildren(String indent, ActionType type, State self,
			State source, State destination, boolean bottom_up) {
		if (!bottom_up) {
			printActionsForState(indent, type, self, source, destination);
		}
		
		printActionsForChildren(indent, type, self, source, destination, bottom_up);

		if (bottom_up) {
			printActionsForState(indent, type, self, source, destination);
		}
	}

	private void printActionsForChildren(String indent, ActionType type, State self, State source,
			State destination, boolean bottom_up) {
		if (self instanceof SuperState) {
			SuperState superstate = (SuperState) self;
			
			for (Region region : superstate.getRegions()) {
				stringBuffer.append(indent + "switch (" + getStateVariableName(region) + ") {\n");
				stringBuffer.append('\n');

				Collection<State> sorted_states = smg.sortStates(region.getStates());
				Collection<StateWithActions> states_with_actions
					= smg.filterStatesWithActions(sorted_states);
				
				for (StateWithActions child_state : states_with_actions) {
					stringBuffer.append(indent + "case " + stateName(child_state) + ":\n");
					
					printActionsForStateAndChildren(indent+"\t", type, child_state, child_state, destination, bottom_up);
					
					stringBuffer.append(indent + "\tbreak;\n");
					stringBuffer.append('\n');
				}
				
				stringBuffer.append(indent + "}\n");
			}
		}
	}

	private void printInitialActionsForStateAndChildren(String indent, ActionType type, State self) {
		printActionsForState(indent, type, self, null, self);
		
		printInitialActionsForChildren(indent, type, self);
	}

	private void printActionsForState(String indent, ActionType type,
			State self, State source, State destination) {
		String actionMethodName = actionMethod(self, type);
		if (type.shouldExecuteFor(source, destination, self) && existing_action_methods.contains(actionMethodName))
			printCode(indent, actionMethodName + "();\n");
	}

	private void printInitialActionsForChildren(String indent, ActionType type, State self) {
		if (self instanceof StateParent)
			printInitialActionsForChildren(indent, type, (StateParent)self);
	}

	private void printInitialActionsForChildren(String indent, ActionType type, StateParent self) {
		if (self instanceof SuperState) {
			SuperState superstate = (SuperState) self;
			
			for (Region region : superstate.getRegions()) {
				printInitialActionsForChildren(indent, type, region);
			}
		} else {
			Transition initial_transition = smg.findInitialTransition(self.getStates());
			if (initial_transition == null || initial_transition.getDestination() == null) {
				stringBuffer.append("#error Cannot find a first state for " + describe(self) + ". Please add an InitialState which has exactly one edge to a normal state.\n");
				return;
			}

			State first_state = initial_transition.getDestination();
			
			if (type == ActionType.DURING) {
				// set state variable
				genTrace(30, indent, "$name: state <- " + stateName(first_state) + " (initial state)");
				stringBuffer.append(indent + getStateVariableName(self) + " = "
						+ stateName(first_state) + ";\n");
				
				printCode(indent, smg.getTransitionInfo(initial_transition).getAction());
			}
			
			printInitialActionsForStateAndChildren(indent, type, first_state);
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

	private void generateEventFunction(String event, StateParent parent, Iterable<Transition> transitions) {
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
		
		generateEventSwitchCase("\t", event, parent, transitions);
		
		stringBuffer.append("\n\t" + sm_name + "_exit_critical();\n");
		stringBuffer.append("}\n");
	}

	private void generateEventSwitchCase(String indent, String event,
			StateParent parent, Iterable<Transition> transitions) {
		//TODO If this is a nested switch-case, a previous one might
		//      have executed a transition. If this has changed the
		//      state, we shouldn't do anything here.
		//      -> Check parent states in an "if".
		
		stringBuffer.append(indent + "switch (" + getStateVariableName(parent) + ") {\n");
		stringBuffer.append('\n');

		Collection<State> sorted_states = smg.sortStates(parent.getStates());
		Collection<StateWithActions> states_with_actions
			= smg.filterStatesWithActions(sorted_states);
		
		for (StateWithActions state : states_with_actions) {
			stringBuffer.append(indent + "case " + stateName(state) + ":\n");
			
			if (event == null) {
				// If we are generating the tick function, print actions
				// that should be executed for the state.
				if (executeDuringState(indent + "\t", state))
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
				
				stringBuffer.append(indent + "\t");
				if (first)
					first = false;
				else
					stringBuffer.append("} else ");
				
				stringBuffer.append("if (");
				String condition = tinfo.getCondition();
				if (condition == null || condition.trim().equals(""))
					condition = "1";
				printCode(indent + "\t\t\t\t", condition, true);
				stringBuffer.append(") {");
				if (tinfo.isWaitTransition()) {
					// print some documentation
					stringBuffer.append("  // " + tinfo.getWaitType() + "(" + formatTime(tinfo.getWaitTime()) + ")");
					/*if (tinfo.getCondition() != null && !condition.trim().equals(""))
						// not so useful because it contains the rewritten wait condition
						stringBuffer.append(" [" + tinfo.getCondition() + "]");*/
				}
				stringBuffer.append('\n');
				executeTransition(indent + "\t\t", t);
			}
			if (!first)
				stringBuffer.append(indent + "\t}\n\n");
	
			// If this is a superstate, we must take care of its children.
			if (state instanceof SuperState) {
				SuperState superstate = (SuperState)state;
				
				stringBuffer.append('\n');
				stringBuffer.append(indent + "\t// execute transitions for the children, unless some transition has changed the state\n");
				stringBuffer.append(indent + "\tif (" + getStateVariableName(parent) + " == " + stateName(superstate) + ") {\n");

				for (Region region : superstate.getRegions())
					generateEventSwitchCase(indent + "\t\t", event, region, transitions);
				
				stringBuffer.append(indent + "\t}\n");
			}
			
			stringBuffer.append(indent + "\tbreak;\n");
			stringBuffer.append('\n');
		}
		
		stringBuffer.append(indent + "}\n");
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

	public static String getFullStateName(StateScope state) {
		if (state instanceof StateMachine)
			return null;
		else if (state instanceof NamedItem) {
			StateParent parent = state.getParent();
			String parent_name = (parent != null ? getFullStateName(parent) : null);
			String name = ((NamedItem)state).getName();
			if (parent_name != null)
				name = parent_name + "_" + name;
			return name;
		} else
			throw new IllegalArgumentException("Expecting a StateMachine or something with a name (a state or region)");
	}
}
