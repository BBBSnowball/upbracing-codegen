package de.upbracing.code_generation.fsm.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map2;
import org.codehaus.jparsec.functors.Map3;
import org.codehaus.jparsec.functors.Pair;
import org.codehaus.jparsec.pattern.Patterns;

/** parsers for parts of the statemachine model: transition text and state actions
 * 
 * @author benny
 *
 */
public class FSMParsers {
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
	public static List<Action> parseStateActions(String text) {
		@SuppressWarnings("unchecked")
		Parser<ActionType> action_names[] = new Parser[ActionType.values().length];
		for (int i=0;i<ActionType.values().length;i++) {
			ActionType at = ActionType.values()[i];
			action_names[i] =
					Parsers.sequence(
						Scanners.stringCaseInsensitive(at.name()),
						Parsers.constant(at));
		}
		Parser<ActionType> action_name = Parsers.or(action_names);
		
		Parser<List<Action>> action_parser = Parsers.sequence(
				withWhitespace(action_name),
				withWhitespace(Scanners.string("/")),
				getActionParser("\n"),
				new Map3<ActionType, Void, String, Action>() {
					@Override
					public Action map(ActionType a, Void b, String c) {
						return new Action(a, c);
					}
				}).endBy(withWhitespace(Scanners.string("\n")));
		
		return Parsers.sequence(Scanners.WHITESPACES.optional(), action_parser)
				.parse(text + "\n");
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
		Parser<String> event_name = Scanners.IDENTIFIER.source();
		Parser<String> condition = getActionParser("]").between(Scanners.string("["), Scanners.string("]"));
		
		Parser<Pair<String, Pair<String, Double>>> event_and_wait =
				Parsers.or(
						asSecondOfStringPair(waitEventParser()),
						Parsers.pair(
								withWhitespace(event_name).optional(),
								Parsers.sequence(
										withWhitespace(Scanners.string(":")),
										waitEventParser()).optional()));
		
		Parser<TransitionInfo> parser = Parsers.sequence(
				withWhitespace(event_and_wait),
				withWhitespace(condition.optional()),
				Parsers.sequence(
						withWhitespace(Scanners.string("/")),
						getActionParser(""))
					.optional(),
				new Map3<Pair<String, Pair<String, Double>>, String, String, TransitionInfo>() {
					@Override
					public TransitionInfo map(Pair<String, Pair<String, Double>> event_wait,
							String condition, String action) {
						String event_name = null;
						String wait_type = null;
						double wait_time = Double.NaN;
						if (event_wait != null) {
							event_name = event_wait.a;
							if (event_wait.b != null) {
								wait_type = event_wait.b.a;
								wait_time = event_wait.b.b;
							}
						}
						return new TransitionInfo(event_name, condition, action, wait_type, wait_time);
					}
				});

		return Parsers.sequence(Scanners.WHITESPACES.optional(), parser)
				.parse(text);
	}
	
	/** return a pair with null and the parser result */
	private static <T, U> Parser<Pair<T, U>> asSecondOfPair(Parser<U> p) {
		return p.map(new Map<U, Pair<T, U>>() {
			public Pair<T, U> map(U from) {
				return new Pair<T, U>(null, from);
			}
		});
	}

	/** return a pair with null and the parser result */
	private static <U> Parser<Pair<String, U>> asSecondOfStringPair(Parser<U> p) {
		return asSecondOfPair(p);
	}

	/** parse an action (or a condition) which doesn't have notAllowedChars except
	 * within strings or parentheses */
	private static Parser<String> getActionParser(String notAllowedChars) {
		return textWithMatchingParens(
				Parsers.or(
						Scanners.string("\\\n"),
						Scanners.notAmong("([{\"" + notAllowedChars),
						Scanners.DOUBLE_QUOTE_STRING)
					.source());
	}
	
	/** skip whitespace after parsing with p, returns result of p */
	private static <T> Parser<T> withWhitespace(Parser<T> p) {
		return p.followedBy(Scanners.WHITESPACES.optional());
	}

	/** helper for getActionParser, parse an action (or condition), textparser.many() is used outside parentheses
	 * 
	 * NOTE: textparser must match things that start with an opening parenthesis/bracket/brace or a double quote
	 * 
	 * @param textparser parser that is used outside of parentheses
	 * @return the parsed string
	 */
	private static Parser<String> textWithMatchingParens(Parser<String> textparser) {
		Parser<String> textparser_without_parens = textparser;
				/*Parsers.sequence(
					Scanners.among("([{").not(),
					textparser).atomic();*/
		Parser.Reference<String> parens_content = Parser.newReference();
		Parser<String> text_in_parens = Parsers.or(
				textInParens("(", ")", parens_content.lazy()),
				textInParens("[", "]", parens_content.lazy()),
				textInParens("{", "}", parens_content.lazy()));
		parens_content.set(
				joinStringList(
					Parsers.or(
							text_in_parens,
							Parsers.or(Scanners.DOUBLE_QUOTE_STRING, Scanners.notAmong("([{}])\"", "open parens")).source())
						.many()));
		return joinStringList(Parsers.or(text_in_parens, textparser_without_parens).many1());
	}
	
	/** like Parser.between(), but it returns the parentheses as well */
	private static Parser<String> textInParens(final String open, final String close, Parser<String> inner) {
		return inner.between(Scanners.string(open), Scanners.string(close))
				.map(new Map<String, String>() {
					@Override
					public String map(String inner_string) {
						return open + inner_string + close;
					}
				});
	}
	
	/** concatenate a list of strings */
	private static Parser<String> joinStringList(Parser<List<String>> strings) {
		return strings.map(new Map<List<String>, String>() {
			@Override
			public String map(List<String> strings) {
				StringBuilder sb = new StringBuilder();
				for (String s : strings)
					sb.append(s);
				return sb.toString();
			}
		});
	}

	@SuppressWarnings("unused")
	/** concatenate an array of strings */
	private static Parser<String> joinStrings(Parser<String[]> strings) {
		return joinStringList(asList(strings));
	}

	/** convert array to list */
	private static <T> Parser<List<T>> asList(Parser<T[]> xs) {
		return xs.map(new Map<T[], List<T>>() {
			@Override
			public List<T> map(T[] from) {
				return Arrays.asList(from);
			}
		});
	}

	
	/** parse a float (actually it returns a double) */
	private static Parser<Double> floatParser() {
		return Scanners.pattern(
				Patterns.regex("[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)([eE][+-]?[0-9]+)?"),
				"float")
			.source()
			.map(new Map<String, Double>() {
				@Override
				public Double map(String from) {
					return Double.parseDouble(from);
				}
			});
	}
	
	/** parse a ratio, e.g. 1/4 */
	private static Parser<Double> ratioParser() {
		return Scanners.pattern(
				Patterns.regex("[0-9]+/[0-9]+([eE][+-]?[0-9]+)?"),
				"ratio")
			.source()
			.map(new Map<String, Double>() {
				@Override
				public Double map(String from) {
					String parts[] = from.split("/", 2);
					return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
				}
			});
	}
	
	/** parse a clock time like 1:30:02.7 */
	private static Parser<Double> clockTimeParser() {
		return Scanners.pattern(
				Patterns.regex("[0-9]+(:[0-9]+)*(\\.[0-9]+)?"),
				"clock time")
			.source()
			.map(new Map<String, Double>() {
				@Override
				public Double map(String from) {
					String parts[] = from.split(":");
					double time = 0;
					double factor = 1;
					for (int i=0;i<parts.length;i++) {
						time += factor * Double.parseDouble(parts[parts.length-i-1]);
						factor *= 60;
					}
					return time;
				}
			});
	}
	
	/** parse a time which can be "1.7ms", "1:30:02.7" or even "1/3 day" */
	private static Parser<Double> timeParser() {
		final java.util.Map<String, Double> factors = new HashMap<String, Double>();
		factors.put("days", 24*60*60.0);
		factors.put("day",  24*60*60.0);
		factors.put("hours",   60*60.0);
		factors.put("hour",    60*60.0);
		factors.put("h",       60*60.0);
		factors.put("min",        60.0);
		factors.put("m",          60.0);
		factors.put("s",           1.0);
		factors.put("ms", 1e-3);
		factors.put("us", 1e-6);
		factors.put("ns", 1e-9);
		factors.put("ps", 1e-12);
		factors.put("fs", 1e-15);
		
		List<Parser<Void>> suffixes = new ArrayList<Parser<Void>>(factors.size());
		for (String suffix : factors.keySet())
			suffixes.add(Scanners.string(suffix));
		
		Parser<Double> numberParser = Parsers.or(ratioParser(), floatParser());
		
		Parser<Double> timeWithSuffix =
				Parsers.sequence(
						withWhitespace(numberParser),
						Parsers.or(suffixes).source(),
						new Map2<Double, String, Double>() {
							@Override
							public Double map(Double time, String factor) {
								return time * factors.get(factor);
							}
						});
		
		return Parsers.or(timeWithSuffix, clockTimeParser());
	}
	
	/** parse a wait term like "wait(1ms)" or "after 1/3 hour" */
	private static Parser<Pair<String, Double>> waitEventParser() {
		return
			Parsers.pair(
				withWhitespace(
					Parsers.or(
							Scanners.string("wait"),
							Scanners.string("at"),
							Scanners.string("before"),
							Scanners.string("after"))
						.source()),
				Parsers.or(
					withWhitespace(timeParser())
						.between(
							withWhitespace(Scanners.string("(")),
							withWhitespace(Scanners.string(")"))),
					withWhitespace(timeParser())));
	}
}
