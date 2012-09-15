package info.reflectionsofmind.parser.contrib;

import info.reflectionsofmind.parser.Matchers;
import info.reflectionsofmind.parser.matcher.Matcher;

public class ContribMatchers
{
	public static Matcher longest(final Matcher... matchers)
	{
		return new LongestMatcher(matchers);
	}

	public static Matcher ex(final Matcher matcher, final Matcher... filters)
	{
		return new ExcludingMatcher(matcher, filters);
	}

	public static Matcher longs(final Matcher... matchers)
	{
		return longest(Matchers.seq(matchers));
	}

	public static Matcher longc(final Matcher... matchers)
	{
		return longest(Matchers.cho(matchers));
	}
}
