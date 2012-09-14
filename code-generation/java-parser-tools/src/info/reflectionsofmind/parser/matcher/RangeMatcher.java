package info.reflectionsofmind.parser.matcher;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.node.StringNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RangeMatcher implements Matcher
{
	private final char to;
	private final char from;
	
	public RangeMatcher(final char from, final char to)
	{
		this.to = to;
		this.from = from;
	}
	
	@Override
	public List<ResultTree> match(final String input)
	{
		if (input.isEmpty()) return Collections.<ResultTree> emptyList();
		
		for (char ch = this.from; ch <= this.to; ch++)
		{
			if (input.charAt(0) == ch) return Arrays.asList(new ResultTree(new StringNode("" + ch), 1));
		}
		
		return Collections.<ResultTree> emptyList();
	}
}