/**
 * 
 */
package info.reflectionsofmind.parser.matcher;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.node.StringNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class StringMatcher implements Matcher
{
	private final String string;

	public StringMatcher(String string)
	{
		this.string = string;
	}

	@Override
	public List<ResultTree> match(final String input)
	{
		return input.startsWith(string) ? Arrays.asList(new ResultTree(new StringNode(string), string.length())) : Collections.<ResultTree> emptyList();
	}
}