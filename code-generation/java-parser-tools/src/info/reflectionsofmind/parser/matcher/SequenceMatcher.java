/**
 * 
 */
package info.reflectionsofmind.parser.matcher;

import info.reflectionsofmind.parser.Matchers;
import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.node.SequenceNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SequenceMatcher implements Matcher
{
	private final Matcher[] matchers;
	private final int match_index;
	private final Matcher tail_matcher;

	public SequenceMatcher(Matcher[] matchers)
	{
		this.matchers = matchers;
		this.match_index = 0;
		this.tail_matcher = createTailMatcher();
	}

	private SequenceMatcher(Matcher[] matchers, int match_index)
	{
		this.matchers = matchers;
		this.match_index = match_index;
		this.tail_matcher = createTailMatcher();
	}

	private Matcher createTailMatcher() {
		if (match_index < matchers.length)
			//return Matchers.seq(Matchers.tail(matchers));
			return new SequenceMatcher(matchers, match_index+1);
		else
			return null;
	}

	@Override
	public List<ResultTree> match(final String input)
	{
		if (matchers.length <= match_index)
			return Arrays.asList(new ResultTree(new SequenceNode(), 0));

		final List<ResultTree> results = matchers[match_index].match(input);
		final List<ResultTree> combinedResults = new ArrayList<ResultTree>();

		for (final ResultTree result : results)
		{
			if (result.root != null)
			{
				List<ResultTree> subResults = tail_matcher.match(input.substring(result.rest));
				for (final ResultTree subResult : subResults)
				{
					final AbstractNode node = new SequenceNode();
					node.children.add(result.root);
					node.children.addAll(subResult.root.children);

					combinedResults.add(new ResultTree(node, result.rest + subResult.rest));
				}
			}
		}

		return combinedResults;
	}
}