package info.reflectionsofmind.parser.contrib;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.matcher.Matcher;

import java.util.Iterator;
import java.util.List;

public final class ExcludingMatcher implements Matcher
{
	private final Matcher[] filters;
	private final Matcher matcher;

	public ExcludingMatcher(Matcher matcher, Matcher... filters)
	{
		this.filters = filters;
		this.matcher = matcher;
	}

	@Override
	public List<ResultTree> match(final String input)
	{
		final List<ResultTree> results = matcher.match(input);

		for (int i = 0; !results.isEmpty() && i < filters.length; i++)
		{
			for (Iterator<ResultTree> x = filters[i].match(input).iterator(); !results.isEmpty() && x.hasNext();)
			{
				final ResultTree xresult = x.next();
				for (Iterator<ResultTree> r = results.iterator(); r.hasNext();)
				{
					ResultTree result = r.next();
					if (result.rest == xresult.rest)
						r.remove();
				}
			}
		}

		return results;
	}
}