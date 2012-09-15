/**
 * 
 */
package info.reflectionsofmind.parser.matcher;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.node.ChoiceNode;

import java.util.ArrayList;
import java.util.List;

public final class ChoiceMatcher implements Matcher
{
	private final Matcher[] matchers;

	public ChoiceMatcher(Matcher[] matchers)
	{
		this.matchers = matchers;
	}

	@Override
	public List<ResultTree> match(final String input)
	{
		final List<ResultTree> combinedResults = new ArrayList<ResultTree>();

		for (int i = 0; i < matchers.length; i++)
		{
			for (final ResultTree result : matchers[i].match(input))
			{
				final AbstractNode node = new ChoiceNode(i);
				node.children.add(result.root);
				combinedResults.add(new ResultTree(node, result.rest));
			}
		}

		return combinedResults;
	}
}