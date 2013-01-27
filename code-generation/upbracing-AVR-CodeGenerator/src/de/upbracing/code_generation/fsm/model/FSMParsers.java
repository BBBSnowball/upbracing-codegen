package de.upbracing.code_generation.fsm.model;

import static de.upbracing.code_generation.common.TransformHelpers.transformCopyValueFromChild;
import static de.upbracing.code_generation.common.TransformHelpers.transformToList;
import static de.upbracing.code_generation.common.TransformHelpers.transformUseTextAsValue;
import static info.reflectionsofmind.parser.Matchers.cho;
import static info.reflectionsofmind.parser.Matchers.strI;
import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.exception.GrammarParsingException;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.matcher.NamedMatcher;
import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.node.Nodes;
import info.reflectionsofmind.parser.transform.AbstractTransformer;
import info.reflectionsofmind.parser.transform.ChildTransformer1;
import info.reflectionsofmind.parser.transform.ChildTransformer2;
import info.reflectionsofmind.parser.transform.ChildTransformer5;
import info.reflectionsofmind.parser.transform.ITransform;
import info.reflectionsofmind.parser.transform.Transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.upbracing.code_generation.common.Parser;
import de.upbracing.code_generation.common.Times;
import de.upbracing.code_generation.fsm.model.TransitionInfo.EventName;
import de.upbracing.code_generation.fsm.model.TransitionInfo.ISREventName;
import de.upbracing.code_generation.fsm.model.TransitionInfo.NormalEventName;

/** parsers for parts of the statemachine model: transition text and state actions
 * 
 * @author benny
 *
 */
public final class FSMParsers {
	/** parse state actions
	 * 
	 * Each line starts with an action type (e.g. ENTER or EXIT) and a slash. After that,
	 * there can be almost arbitrary text. The parser matches parentheses, square brackets,
	 * curly braces and double-quoted strings. The action extends to the next line, if is
	 * enclosed in some kind of parentheses. Furthermore, a backslash can be used to extend
	 * the current line (which will be part of the output!)
	 * 
	 * @param text action string
	 * @return a list of action objects
	 */
	@SuppressWarnings("unchecked")
	public static List<Action> parseStateActions(String text) {
		// we cannot use newlines in the editor, so we use '###' to simulate it
		text = text.replace("###", "\n");
		
		return (List<Action>)getParser().parse("state-actions", text + "\n");
	}

	/** parse transition text
	 * 
	 * The transition text starts with an event name or a wait term. If it has both, the event
	 * is first and they are separated by a colon (':'). The condition is enclosed in square
	 * brackets. After a forward slash, there can be an action.
	 * 
	 * All the parts are optional. An empty string is perfectly valid.
	 * 
	 * The event name must be a valid identifier. The condition and action can be
	 * almost arbitrary text. The parser matches parentheses, square brackets,
	 * curly braces and double-quoted strings. The condition or action extends to the next line, if is
	 * enclosed in some kind of parentheses. Furthermore, a backslash can be used to extend
	 * the current line (which will be part of the output!)
	 * 
	 * @param text action string
	 * @return a list of action objects
	 */
	public static TransitionInfo parseTransitionInfo(String text) {
		// we cannot use newlines in the editor, so we use '###' to simulate it
		text = text.replace("###", "\n");

		return (TransitionInfo)getParser().parse("transition-info", text);
	}
	
	private static Matcher getStateActionTypeMatcher() {
		ActionType[] values = ActionType.values();
		
		Matcher options[] = new Matcher[values.length];
		int i = 0;
		for (ActionType value : values)
			//TODO make it case-insensitive
			options[i++] = strI(value.name());
		
		return new NamedMatcher("state-action-type").define(cho(options));
	}
	
	private static Parser parser = null;
	public static Parser getParser() {
		if (parser == null) {
			try {
				parser = createParser();
			} catch (GrammarParsingException e) {
				throw new RuntimeException(e);
			}
		}
		
		return parser;
	}

	private static Parser createParser() throws GrammarParsingException {
		Map<String, Matcher> previous_defs = new HashMap<String, Matcher>();
		previous_defs.put("state-action-type", getStateActionTypeMatcher());
		
		return new Parser(FSMParsers.class, "fsm-grammar.g", createTransform(),
				Parser.getCommonParser(),
				Times.getParser(),
				new Parser(previous_defs, null));
	}

	private static Transform createTransform() {
		Transform transform = new Transform();
		
		transformUseTextAsValue(transform, "wait-event-type");
		
		transformUseTextAsValue(transform, "condition-text");
		
		transformCopyValueFromChild(transform, "condition", ">condition-text");
		
		transformUseTextAsValue(transform, "normal-event-name");
		
		transform.add(new ChildTransformer1<String>(String.class,
				"isr-event", ">>identifier#text") {
			@Override
			protected Object transform(String isr_name) {
				return new ISREventName(isr_name);
			}
		});
		
		transform.add(new ChildTransformer2<ISREventName, String>(ISREventName.class, String.class,
				"event-name",  "?>>isr-event", "?>>normal-event-name") {
			@Override
			protected Object transform(ISREventName isr, String event_name) {
				if (isr != null)
					return isr;
				else
					return new NormalEventName(event_name);
			}
		});
		
		transform.add(new ChildTransformer5<EventName, String, Double, String, String>(
				EventName.class, String.class, Double.class, String.class, String.class,
				"transition-info", "~ws", 
				"?>>event-name", "?>>wait-event-type#text", "?>>time",
				"?>condition", "?>transition-action#text") {
			@Override
			protected Object transform(EventName event_name, String wait_type, Double wait_time,
					String condition, String action) {
				// most things can be null and we don't care
				// However, for the Double value this would cause a NullPointerException,
				// when it is cast into a double. Therefore, we replace it.
				if (wait_time == null)
					wait_time = Double.NaN;
				
				if (condition != null)
					condition = condition.trim();
				
				if (action != null)
					action = action.trim();
				
				return new TransitionInfo(event_name, condition, action, wait_type, wait_time);
			}
		});
		
		transform.add(new AbstractTransformer("state-action-type") {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				node.value = ActionType.valueOf(node.getText().toUpperCase());
			}
		});
		transform.add(new ChildTransformer2<ActionType, String>(
				ActionType.class, String.class,
				"state-action", "~ws",
				">state-action-type", ">state-action-text#text") {
			@Override
			protected Object transform(ActionType type, String action) {
				action = action.trim();
				
				return new Action(type, action);
			}
		});
		
		transformToList(transform, "state-actions", "state-action");
		
		return transform;
	}
	
	public static void main(String args[]) throws Exception {
		String parserName, text;
		
		parserName = "clock-time"; text = "1:30";
		parserName = "time"; text = "100ms";
		parserName = "wait-event"; text = "wait(1ms)";
		parserName = "wait-event"; text = "after 1/3 hour";
		parserName = "transition-info"; text = "blub:wait(10min) [a>7] / blub();";
		parserName = "transition-info"; text = "blub:wait(10min) [a[0]>7] / blub();";
		parserName = "transition-info"; text = "blub [a>7] / blub();";
		parserName = "state-action"; text = "EXIT / blub(a,b/2)";
		parserName = "state-actions"; text = "\n\nEXIT / blub(a,b/2)\n\nENTER/abc\n\n\n";
		parserName = "state-actions"; text = "ENTER/DDRB = 0xff \n ENTER/PORTB++ \n ALWAYS/wdt_reset()\n";
		//parserName = "state-actions"; text = "ENTER/DDRB = 0xff \n \n"; //ENTER/PORTB++ \n ALWAYS/wdt_reset()\n";
		
		List<ResultTree> result = getParser().parseToTrees(parserName, text);
		
		System.out.println("We have " + result.size() + " trees.");
		if (!result.isEmpty()) {
			System.out.println("Longest tree:");
			ResultTree longest = result.get(0);
			for (ResultTree tree : result) {
				if (tree.rest > longest.rest)
					longest = tree;
			}
			System.out.println(Nodes.toStringFull(longest.root));
			System.out.println("Used " + longest.rest + " of " + text.length() + " chars");
			System.out.println();
			
			ITransform transform = getParser().getTransform();
			
			transform.transform(longest.root);
			System.out.println(Nodes.toStringWithValue(longest.root));

			System.out.println("We have " + result.size() + " trees.");
			System.out.println("We were using parser " + parserName + " on:\n  " + text);
			System.out.println("The tree has been transformed into this value:\n  " + longest.root.value);
		}
	}
}
