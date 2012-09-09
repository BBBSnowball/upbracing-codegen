package de.upbracing.code_generation.fsm.model;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map2;
import org.codehaus.jparsec.functors.Map3;
import org.codehaus.jparsec.functors.Map4;
import org.codehaus.jparsec.functors.Pair;

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
				getActionParser(),
				new Map3<ActionType, Void, String, Action>() {
					@Override
					public Action map(ActionType a, Void b, String c) {
						return new Action(a, c);
					}
				}).endBy(withWhitespace(Scanners.string("\n")));
		
		return Parsers.sequence(Scanners.WHITESPACES.optional(), action_parser)
				.parse(text + "\n");
	}
	
	private static Parser<String> getActionParser() {
		return textWithMatchingParens(
				Parsers.or(
						Scanners.string("\\\n"),
						Scanners.notAmong("\n([{"))
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
							Scanners.notAmong("([{}])", "open parens").source())
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
