package info.reflectionsofmind.parser.contrib;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.matcher.Matcher;

import java.util.ArrayList;
import java.util.List;

public final class LongestMatcher implements Matcher
{
	private final Matcher[] matchers;

	public LongestMatcher(Matcher... matchers)
	{
		this.matchers = matchers;
	}

	@Override
	public List<ResultTree> match(final String input)
	{
		final List<ResultTree> allResults = new ArrayList<ResultTree>();
		int maxRest = 0;

		for (int i = 0; i < matchers.length; i++)
		{
			for (final ResultTree result : matchers[i].match(input))
			{
				allResults.add(result);
				if (maxRest < result.rest)
					maxRest = result.rest;
			}
		}

		if (allResults.isEmpty())
			return allResults;

		final List<ResultTree> longestResults = new ArrayList<ResultTree>();
		for (ResultTree result : allResults)
			if (maxRest <= result.rest)
				longestResults.add(result);

		return longestResults;
	}
}