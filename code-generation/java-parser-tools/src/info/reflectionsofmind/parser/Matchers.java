package info.reflectionsofmind.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.reflectionsofmind.parser.matcher.ChoiceMatcher;
import info.reflectionsofmind.parser.matcher.LongestTreeMatcher;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.matcher.RangeMatcher;
import info.reflectionsofmind.parser.matcher.RepetitionMatcher;
import info.reflectionsofmind.parser.matcher.SequenceMatcher;
import info.reflectionsofmind.parser.matcher.StringCaseInsensitiveMatcher;
import info.reflectionsofmind.parser.matcher.StringMatcher;
import info.reflectionsofmind.parser.node.NamedNode;
import info.reflectionsofmind.parser.node.Nodes;

public final class Matchers
{
	private Matchers()
	{
		throw new UnsupportedOperationException("Utility class");
	}

	public static Matcher[] tail(final Matcher[] array)
	{
		return Arrays.asList(array).subList(1, array.length).toArray(new Matcher[] {});
	}

	// ============================================================================================
	// === BASIC MATCHERS
	// ============================================================================================

	public static Matcher str(final String string)
	{
		return new StringMatcher(string);
	}

	public static Matcher strI(final String string)
	{
		return new StringCaseInsensitiveMatcher(string);
	}

	public static Matcher range(final char from, final char to)
	{
		return new RangeMatcher(from, to);
	}

	public static Matcher seq(final Matcher... matchers)
	{
		return new SequenceMatcher(matchers);
	}

	public static Matcher cho(final Matcher... matchers)
	{
		return new ChoiceMatcher(matchers);
	}
	
	public static Matcher one(final Matcher matcher)
	{
		return new LongestTreeMatcher(matcher);
	}

	// ============================================================================================
	// === SIMPLE REPETITION MATCHERS
	// ============================================================================================

	public static Matcher rep(final Matcher matchers)
	{
		return rep(0, Integer.MAX_VALUE, matchers);
	}

	public static Matcher rep(final int num, final Matcher matchers)
	{
		return rep(num, num, matchers);
	}

	public static Matcher min(final int min, final Matcher matchers)
	{
		return rep(min, Integer.MAX_VALUE, matchers);
	}

	public static Matcher max(final int max, final Matcher matchers)
	{
		return rep(0, max, matchers);
	}

	public static Matcher rep(final int min, final int max, final Matcher matcher)
	{
		return new RepetitionMatcher(min, max, matcher);
	}

	// ============================================================================================
	// === REPETITION OF SEQUENCE MATCHERS
	// ============================================================================================

	public static Matcher reps(final Matcher... matchers)
	{
		return reps(0, Integer.MAX_VALUE, matchers);
	}

	public static Matcher reps(final int num, final Matcher... matchers)
	{
		return reps(num, num, matchers);
	}

	public static Matcher mins(final int min, final Matcher... matchers)
	{
		return reps(min, Integer.MAX_VALUE, matchers);
	}

	public static Matcher maxs(final int max, final Matcher... matchers)
	{
		return reps(0, max, matchers);
	}

	public static Matcher reps(final int min, final int max, final Matcher... matchers)
	{
		return new RepetitionMatcher(min, max, RepetitionMatcher.Type.SEQUENCE, matchers);
	}

	// ============================================================================================
	// === REPETITION OF CHOICE MATCHERS
	// ============================================================================================

	public static Matcher repc(final Matcher... matchers)
	{
		return repc(0, Integer.MAX_VALUE, matchers);
	}

	public static Matcher repc(final int num, final Matcher... matchers)
	{
		return repc(num, num, matchers);
	}

	public static Matcher minc(final int min, final Matcher... matchers)
	{
		return repc(min, Integer.MAX_VALUE, matchers);
	}

	public static Matcher maxc(final int max, final Matcher... matchers)
	{
		return repc(0, max, matchers);
	}

	public static Matcher repc(final int min, final int max, final Matcher... matchers)
	{
		return new RepetitionMatcher(min, max, RepetitionMatcher.Type.CHOICE, matchers);
	}

	// ============================================================================================
	// === OPTIONAL MATCHERS
	// ============================================================================================

	public static Matcher opt(final Matcher matcher)
	{
		return cho(matcher, str(""));
	}

	public static Matcher opts(final Matcher... matchers)
	{
		return cho(seq(matchers), str(""));
	}

	public static Matcher optc(final Matcher... matchers)
	{
		return cho(cho(matchers), str(""));
	}

	// ============================================================================================
	// === MATCHING UTILITY METHODS
	// ============================================================================================

	public static List<ResultTree> fullMatch(final Matcher matcher, final String input)
	{
		final List<ResultTree> partResults = matcher.match(input);
		final List<ResultTree> fullResults = new ArrayList<ResultTree>();

		for (final ResultTree result : partResults)
		{
			if (result.rest == input.length())
			{
				fullResults.add(result);
			}
		}

		return fullResults;
	}

	public static String toStringFull(ResultTree result)
	{
		return "Matched [" + result.rest + "] symbols, tree:\n" + Nodes.toStringFull(result.root) + "\n";
	}

	public static String toStringNamed(ResultTree result)
	{
		return "Matched [" + result.rest + "] symbols, tree:\n" + Nodes.toStringNamed((NamedNode) result.root) + "\n";
	}

	public static String toStringFull(List<ResultTree> results)
	{
		StringBuilder builder = new StringBuilder();

		for (ResultTree result : results)
			builder.append(toStringFull(result));

		return builder.toString();
	}

	public static String toStringNamed(List<ResultTree> results)
	{
		StringBuilder builder = new StringBuilder();

		for (ResultTree result : results)
			builder.append(toStringNamed(result));

		return builder.toString();
	}
}
