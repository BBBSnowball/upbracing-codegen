package info.reflectionsofmind.parser.matcher.common;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.node.StringNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BooleanMatcher
implements Matcher
{
	@Override
	public List<ResultTree> match(String input)
	{
		int i= 0;
		if (input.startsWith("true")) {
			i= 4;
		}
		else if (input.startsWith("false"))
			i= 5;

		if (i <= 0) 
			return Collections.<ResultTree> emptyList();

		return Arrays.asList(new ResultTree(new StringNode(input.substring(0, i)), i));
	}
}
