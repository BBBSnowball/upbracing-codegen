package de.upbracing.code_generation.fsm.model;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map3;

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
				withWhitespace(event_name.optional()),
				withWhitespace(condition.optional()),
				Parsers.sequence(
						withWhitespace(Scanners.string("/")),
						getActionParser(""))
					.optional(),
				new Map3<String, String, String, TransitionInfo>() {
					@Override
					public TransitionInfo map(String event_name, String condition, String action) {
						return new TransitionInfo(event_name, condition, action);
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
}
