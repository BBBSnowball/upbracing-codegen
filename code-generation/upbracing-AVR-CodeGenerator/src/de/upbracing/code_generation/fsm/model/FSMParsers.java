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

public class FSMParsers {
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
	
	public static TransitionInfo parseTransitionInfo(String text) {
		Parser<String> event_name = Scanners.IDENTIFIER.source();
		Parser<String> condition = getActionParser("]").between(Scanners.string("["), Scanners.string("]"));
		Parser<TransitionInfo> parser = Parsers.sequence(
				withWhitespace(
					Parsers.or(
							waitEventParser(),
							Parsers.pair(event_name, Parsers.constant(Double.NaN)))
						.optional()),
				withWhitespace(condition.optional()),
				Parsers.sequence(
						withWhitespace(Scanners.string("/")),
						getActionParser(""))
					.optional(),
				new Map3<Pair<String, Double>, String, String, TransitionInfo>() {
					@Override
					public TransitionInfo map(Pair<String, Double> event, String condition, String action) {
						String event_name = (event != null ? event.a : null);
						double wait_time = (event != null ? event.b : Double.NaN);
						return new TransitionInfo(event_name, wait_time, condition, action);
					}
				});

		return Parsers.sequence(Scanners.WHITESPACES.optional(), parser)
				.parse(text);
	}
	
	private static Parser<String> getActionParser(String notAllowedChars) {
		return textWithMatchingParens(
				Parsers.or(
						Scanners.string("\\\n"),
						Scanners.notAmong("([{\"" + notAllowedChars),
						Scanners.DOUBLE_QUOTE_STRING)
					.source());
	}
	
	private static <T> Parser<T> withWhitespace(Parser<T> p) {
		return p.followedBy(Scanners.WHITESPACES.optional());
	}
	
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
	
	private static Parser<String> textInParens(final String open, final String close, Parser<String> inner) {
		return inner.between(Scanners.string(open), Scanners.string(close))
				.map(new Map<String, String>() {
					@Override
					public String map(String inner_string) {
						return open + inner_string + close;
					}
				});
	}
	
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
	private static Parser<String> joinStrings(Parser<String[]> strings) {
		return joinStringList(asList(strings));
	}
	
	private static <T> Parser<List<T>> asList(Parser<T[]> xs) {
		return xs.map(new Map<T[], List<T>>() {
			@Override
			public List<T> map(T[] from) {
				return Arrays.asList(from);
			}
		});
	}

	
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
