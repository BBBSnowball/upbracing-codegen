package de.upbracing.code_generation.fsm.model;

import static info.reflectionsofmind.parser.Matchers.cho;
import static info.reflectionsofmind.parser.Matchers.str;
import static info.reflectionsofmind.parser.Matchers.strI;
import info.reflectionsofmind.parser.Grammar;
import info.reflectionsofmind.parser.Matchers;
import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.exception.GrammarParsingException;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.matcher.NamedMatcher;
import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.node.NamedNode;
import info.reflectionsofmind.parser.node.Navigation;
import info.reflectionsofmind.parser.node.Nodes;
import info.reflectionsofmind.parser.transform.AbstractTransformer;
import info.reflectionsofmind.parser.transform.ChildTransformer2;
import info.reflectionsofmind.parser.transform.ChildTransformer5;
import info.reflectionsofmind.parser.transform.ITransform;
import info.reflectionsofmind.parser.transform.Transform;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.upbracing.code_generation.utils.Util;

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
		
		return (List<Action>)parse("state-actions", text + "\n");
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

		return (TransitionInfo)parse("transition-info", text);
	}
	
	/** parse a time like "1.7ms", "1:30:02.7" or even "1/3 day"
	 * 
	 * @param time text to parse
	 * @return parsed time in seconds
	 */
	public static double parseTime(String time) {
		return (Double)parse("time", time.trim());
	}

	/** format a time value
	 * 
	 * The value is meaningful for a user, e.g. it could be "100ms". The value can also
	 * be parsed with {@link #parseTime(String)} which yields the almost same value as
	 * before. There can be a round-off error and the textual representation is less
	 * exact than the double value.
	 * 
	 * @param time time to format for the user
	 * @return the time as a string
	 */
	public static String formatTime(double time) {
		final Map<String, Double> factors = new HashMap<String, Double>();
		factors.put("ms", 1e-3);
		factors.put("us", 1e-6);
		factors.put("ns", 1e-9);
		factors.put("ps", 1e-12);
		factors.put("fs", 1e-15);
		
		String sign = "";
		if (time < 0) {
			sign = "-";
			time = -time;
		}
		
		// big numbers are handled specially because for times the factors are quite weird...
		if (time > 60) {
			if (time < 60*60*24 * 5) {
				int mins = ((int)time) / 60 % 60;
				int hours = ((int)time) / 60 / 60;
				double secs = time - mins*60 - hours*60*60;
				return String.format("%s%02d:%02d:%06.3f", sign, hours, mins, secs);
			} else {
				return String.format("%4.2f days", time / (60*60*24));
			}
		}
		
		// shift it in the range 1 to 10
		int e = 0;
		double t = time;
		while (t >= 10) {
			t /= 10;
			e += 1;
		}
		while (t < 1) {
			t *= 10;
			e -= 1;
		}
		
		// now: time == t*10^e
		
		// find out how many digits we need, so we won't ignore a non-zero one
		int significant_digits = 1;
		int factor = 10;
		for (int i=2;i<6;i++) {
			if (Math.round(t * factor) % 10 != 0)
				significant_digits = i;
			factor *= 10;
		}
		
		// for more than 5s we always write it in seconds, but with adjusted precision
		if (time > 5 || e > 0) {
			// significant digits without the ones before the point (e+1)
			int precision = significant_digits - (e+1);
			
			return String.format("%2." + precision + "f sec", time);
		}
		
		// we have two possibilities: use the suffix that makes the number short or the one below
		e = -e;	// make it positive
		int unit = (e+2) / 3;
		int digitsBeforePoint = e % 3 + 1;
		int digitsAfterPoint = significant_digits - digitsBeforePoint;
		
		if (digitsAfterPoint > 2 && unit < 5) {
			unit++;
			digitsBeforePoint += 3;
			digitsAfterPoint -= 3;
		}
		
		if (digitsAfterPoint < 0)
			digitsAfterPoint = 0;
		
		if (unit > 5)
			// I won't implement anything smaller than femto-seconds. No ato-seconds... sorry ;-)
			// Don't adjust digitsAfterPoint because it is quite already too much.
			unit = 5;
		
		for (int i=0;i<unit;i++)
			time *= 1e3;
		
		String number = String.format("%s%2." + digitsAfterPoint + "f", sign, time);
		
		switch (unit) {
		case 0:
			return number + " sec";
		case 1:
			return number + " ms";
		case 2:
			return number + " us";
		case 3:
			return number + " ns";
		case 4:
			return number + " ps";
		case 5:
			return number + " fs";
		default:
			throw new RuntimeException("Should never get here!");
		}
		
	}
	
	private static Map<String, Double> getTimeFactors() {
		final Map<String, Double> factors = new HashMap<String, Double>();
		factors.put("days", 24*60*60.0);
		factors.put("day",  24*60*60.0);
		factors.put("hours",   60*60.0);
		factors.put("hour",    60*60.0);
		factors.put("h",       60*60.0);
		factors.put("min",        60.0);
		factors.put("m",          60.0);
		factors.put("sec",         1.0);
		factors.put("s",           1.0);
		factors.put("ms", 1e-3);
		factors.put("us", 1e-6);
		factors.put("ns", 1e-9);
		factors.put("ps", 1e-12);
		factors.put("fs", 1e-15);
		return factors;
	}

	private static Matcher getTimeSuffixMatcher() {
		Set<String> suffixes = getTimeFactors().keySet();
		
		Matcher options[] = new Matcher[suffixes.size()];
		int i = 0;
		for (String suffix : suffixes)
			options[i++] = str(suffix);
		
		return new NamedMatcher("time-suffix").define(cho(options));
	}
	
	private static Matcher getStateActionTypeMatcher() {
		ActionType[] values = ActionType.values();
		
		Matcher options[] = new Matcher[values.length];
		int i = 0;
		for (ActionType value : values)
			//TODO make it case-insensitive
			options[i++] = strI(value.name());
		
		return new NamedMatcher("state-event-name").define(cho(options));
	}

	private static Map<String, Matcher> createGrammarDefinition()
			throws GrammarParsingException {
		Map<String, Matcher> previous_defs = new HashMap<String, Matcher>();
		previous_defs.put("time-suffix", getTimeSuffixMatcher());
		previous_defs.put("state-action-type", getStateActionTypeMatcher());
		
		Map<String, Matcher> matchers = Grammar.generateDefinitions(
				Util.loadResource(FSMParsers.class, "fsm-grammar.g"), previous_defs);
		return matchers;
	}

	private static Map<String, Matcher> cached_grammar_definition;
	private static Map<String, Matcher> getGrammarDefinition() {
		if (cached_grammar_definition == null) {
			try {
				cached_grammar_definition = createGrammarDefinition();
			} catch (GrammarParsingException e) {
				throw new RuntimeException(e);
			}
		}
		return cached_grammar_definition;
	}

	private static void transformChoice(Transform transform, String... ids) {
		transform.add(new AbstractTransformer(ids) {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				transform.transform(node.children);
				
				List<NamedNode> children = Navigation.getNamedChildren(node);
				assert children.size() == 1;
				
				node.value = children.get(0).value;
			}
		});
	}

	private static void transformUseTextAsValue(Transform transform, String... ids) {
		transform.add(new AbstractTransformer(ids) {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				node.value = node.getText();
			}
		});
	}

	private static Transform createTransform() {
		Transform transform = new Transform();
		
		transform.add(new AbstractTransformer("positive-number") {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				node.value = Integer.parseInt(node.getText());
			}
		});
		
		transform.add(new AbstractTransformer("float-number", "simple-unsigned-float-number") {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				node.value = Double.parseDouble(node.getText());
			}
		});
		
		transform.add(new ChildTransformer2<Integer, Double>(Integer.class, Double.class, "ratio-number") {
			protected Object transform(Integer num, Double denom) {
				return num / denom;
			}
		});
		
		transform.add(new AbstractTransformer("clock-time") {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				transform.transform(node.children);
				
				List<AbstractNode> xs = node.findNamedChildren();
				System.out.println("clock-time:");
				for (AbstractNode child : xs) {
					System.out.println("  " + child);
				}
				
				Collections.reverse(xs);

				double time = 0;
				double factor = 1;
				for (AbstractNode node2 : xs) {
					double x;
					if (node2.value instanceof Integer)
						x = (Integer)node2.value;
					else
						x = (Double)node2.value;
					
					time += factor * x;
					factor *= 60;
				}
				
				node.value = time;
			}
		});
		
		transform.add(new ChildTransformer2<Double, String>(Double.class, String.class, "time-with-suffix", "~ws", "*", "#text") {
			@Override
			protected Object transform(Double time, String factor_name) {
				return time * getTimeFactors().get(factor_name);
			}
		});
		
		transformChoice(transform, "time");
		
		transformUseTextAsValue(transform, "wait-event-type");
		
		transform.add(new ChildTransformer5<String, String, Double, String, String>(
				String.class, String.class, Double.class, String.class, String.class,
				"transition-info", "~ws", 
				"?>>event-name#text", "?>>wait-event-type#text", "?>>time",
				"?>condition#text", "?>transition-action#text") {
			@Override
			protected Object transform(String event_name, String wait_type, Double wait_time,
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
		
		transform.add(new AbstractTransformer("state-event-type") {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				node.value = ActionType.valueOf(node.getText().toUpperCase());
			}
		});
		transform.add(new ChildTransformer2<ActionType, String>(
				ActionType.class, String.class,
				"state-action", "~ws",
				">state-action-type", ">state-action-text") {
			@Override
			protected Object transform(ActionType type, String action) {
				action = action.trim();
				
				return new Action(type, action);
			}
		});
		
		return transform;
	}

	private static Transform cached_transform;
	private static Transform getTransform() {
		if (cached_transform == null)
			cached_transform = createTransform();
		return cached_transform;
	}
	
	private static Object parse(String parserName, String text) {
		ResultTree result = parseToTree(parserName, text);
		
		return result.root.value;
	}

	private static ResultTree parseToTree(String parserName, String text) {
		List<ResultTree> results = parseToTrees(parserName, text);
		
		if (results.size() < 1)
			//TODO should not be a RuntimeException
			throw new RuntimeException("no result");
		
		ResultTree result;
		boolean accept_ambiguous = true;
		if (accept_ambiguous) {
			result = results.get(0);
			for (ResultTree tree : results) {
				if (tree.rest > result.rest)
					result = tree;
			}
		} else {
			if (results.size() > 1)
				//TODO should not be a RuntimeException
				throw new RuntimeException("ambiguous result");
			
			result = results.get(0);
		}

		Transform transform = getTransform();
		transform.transform(result.root);
		return result;
	}

	private static List<ResultTree> parseToTrees(String parserName, String text) {
		Map<String, Matcher> matchers = getGrammarDefinition();
		
		Matcher matcher = matchers.get(parserName);
		if (matcher == null)
			throw new IllegalArgumentException("parser definition not found: " + parserName);
		List<ResultTree> results = Matchers.fullMatch(matcher, text);
		return results;
	}

	public static void main(String args[]) throws Exception {
		String parserName, text;
		
		parserName = "clock-time"; text = "1:30";
		parserName = "time"; text = "100ms";
		parserName = "wait-event"; text = "wait(1ms)";
		parserName = "wait-event"; text = "after 1/3 hour";
		parserName = "transition-info"; text = "blub:wait(10min) [a>7] / blub();";
		parserName = "transition-info"; text = "blub:wait(10min) [a[0]>7] / blub();";
		//parserName = "transition-info"; text = "blub [a>7] / blub();";
		
		List<ResultTree> result = parseToTrees(parserName, text);
		
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
			
			Transform transform = getTransform();
			
			transform.transform(longest.root);
			System.out.println(Nodes.toStringWithValue(longest.root));

			System.out.println("We have " + result.size() + " trees.");
			System.out.println("We were using parser " + parserName + " on:\n  " + text);
			System.out.println("The tree has been transformed into this value:\n  " + longest.root.value);
		}
	}
}
