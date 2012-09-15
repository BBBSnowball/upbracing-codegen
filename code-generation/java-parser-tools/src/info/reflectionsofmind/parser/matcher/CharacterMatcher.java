package info.reflectionsofmind.parser.matcher;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.node.StringNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class CharacterMatcher implements Matcher
{
	public CharacterMatcher()
	{
	}
	
	public abstract boolean predicate(char c);
	
	@Override
	public final List<ResultTree> match(final String input)
	{
		if (input.isEmpty()) return Collections.<ResultTree> emptyList();
		
		char ch = input.charAt(0);
		if (predicate(ch))
			return Arrays.asList(new ResultTree(new StringNode(Character.toString(ch)), 1));
		
		return Collections.<ResultTree> emptyList();
	}
	
	public static RangeMatcher range(char from, char to) {
		return new RangeMatcher(from, to);
	}
	
	public static CharacterMatcher notIn(final char... forbidden_characters) {
		if (forbidden_characters.length > 3) {
			Arrays.sort(forbidden_characters);
			return new CharacterMatcher() {
				@Override
				public boolean predicate(char c) {
					return Arrays.binarySearch(forbidden_characters, c) < 0;
				}
			};
		} else {
			return new CharacterMatcher() {
				@Override
				public boolean predicate(char c) {
					for (int i=0;i<forbidden_characters.length;i++)
						if (forbidden_characters[i] == c)
							return false;
					
					return true;
				}
			};
		}
	}

	public static Matcher any() {
		return new CharacterMatcher() {
			@Override
			public boolean predicate(char c) {
				return true;
			}
		};
	}
}
