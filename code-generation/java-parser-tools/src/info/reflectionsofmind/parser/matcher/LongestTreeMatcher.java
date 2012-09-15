package info.reflectionsofmind.parser.matcher;

import info.reflectionsofmind.parser.ResultTree;

import java.util.Collections;
import java.util.List;

public final class LongestTreeMatcher implements Matcher
{
	private final Matcher matcher;

	public LongestTreeMatcher(Matcher matcher)
	{
		this.matcher = matcher;
	}

	@Override
	public List<ResultTree> match(final String input)
	{
		List<ResultTree> results = matcher.match(input);
		
		if (results.size() > 0) {
			ResultTree longest = results.get(0);
			for (ResultTree result : results)
				if (result.rest > longest.rest)
					longest = result;
			return Collections.singletonList(longest);
		} else
			return Collections.emptyList();
	}
}