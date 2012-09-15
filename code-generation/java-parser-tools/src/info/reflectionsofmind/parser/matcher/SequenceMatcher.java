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

	public SequenceMatcher(Matcher[] matchers)
	{
		this.matchers = matchers;
	}

	public static Matcher[] tail(final Matcher[] array)
	{
		return Arrays.asList(array).subList(1, array.length).toArray(new Matcher[] {});
	}

	@Override
	public List<ResultTree> match(final String input)
	{
		if (matchers.length == 0)
			return Arrays.asList(new ResultTree(new SequenceNode(), 0));

		final List<ResultTree> results = matchers[0].match(input);
		final List<ResultTree> combinedResults = new ArrayList<ResultTree>();

		for (final ResultTree result : results)
		{
			if (result.root != null)
			{
				List<ResultTree> subResults = Matchers.seq(Matchers.tail(matchers)).match(input.substring(result.rest));
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