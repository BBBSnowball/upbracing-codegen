package de.upbracing.code_generation.generators.fsm;

import java.util.regex.Pattern;

import statemachine.FinalState;
import statemachine.InitialState;
import statemachine.NormalState;
import statemachine.Region;
import statemachine.State;
import statemachine.StateMachine;
import statemachine.StateParent;
import statemachine.StateScope;
import statemachine.SuperState;
import statemachine.Transition;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;

public final class Helpers {
	private Helpers() { }

	public static String escapeString(String name) {
		if (Pattern.matches("^[a-zA-Z0-9_-]+$", name))
			return name;
		else
			return "'" + name.replaceAll("([\\'\"])", "\\\\$1").replace("\n", "\\n").replace("\t", "\\t") + "'";
	}
	
	private static <STATE extends StateScope> void addStateFormatter(final Messages messages, Class<STATE> cls, final String type_name) {
		messages.addObjectFormatter(cls,
				new Messages.ObjectFormatter<STATE>() {
					public String format(int type, STATE obj) {
						String name = escapeString(StateMachineForGeneration.getName(obj));
						
						switch (type) {
						case SHORT:
							return name;
						case NORMAL:
							String self = type_name + " " + name;
							
							StateParent parent = obj.getParent();
							if (parent != null)
								return messages.format(type, parent) + " -> " + self;
							else
								return self;
						case LONG:
							return type_name + " " + name;
						default:
							throw new IllegalArgumentException("unexpected type");
						}
					}
				});
	}
	
	public static void addStatemachineFormatters(final Messages messages, final Iterable<StateMachineForGeneration> statemachines) {
		messages.addObjectFormatter(StateMachineForGeneration.class,
				new Messages.ObjectFormatter<StateMachineForGeneration>() {
					public String format(int type, StateMachineForGeneration obj) {
						switch (type) {
						case SHORT:
							return obj.getName();
						case NORMAL:
						case LONG:
							return "StateMachineForGeneration " + obj.getName();
						default:
							throw new IllegalArgumentException("unexpected type");
						}
					}
				});

		messages.addObjectFormatter(StateMachine.class,
				new Messages.ObjectFormatter<StateMachine>() {
					public String format(int type, StateMachine obj) {
						StateMachineForGeneration smg = null;
						for (StateMachineForGeneration smg2 : statemachines) {
							if (smg2.getStateMachine() == obj) {
								smg = smg2;
								break;
							}
						}
						
						String name = (smg != null ? escapeString(smg.getName()) : "?");
						
						switch (type) {
						case SHORT:
							return name;
						case NORMAL:
						case LONG:
							return "StateMachine " + name;
						default:
							throw new IllegalArgumentException("unexpected type");
						}
					}
				});
		
		addStateFormatter(messages, State.class, "unknown kind of state");
		addStateFormatter(messages, InitialState.class, "initial state");
		addStateFormatter(messages, FinalState.class, "final state");
		addStateFormatter(messages, NormalState.class, "normal state");
		addStateFormatter(messages, SuperState.class, "super state");
		addStateFormatter(messages, Region.class, "region");
		
		messages.addObjectFormatter(Transition.class,
				new Messages.ObjectFormatter<Transition>() {
					public String format(int type, Transition obj) {
						StringBuffer sb = new StringBuffer();
						if (type >= NORMAL)
							sb.append("transition: ");
						
						sb.append(messages.format(type, obj.getSource()));
						sb.append(" --> ");
						sb.append(messages.format(type, obj.getDestination()));
						
						if (type >= LONG)
							sb.append(" (" + obj.getTransitionInfo() + ")");
						
						return sb.toString();
					}
				});
	}
}
