package info.reflectionsofmind.parser.matcher.common;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.node.StringNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IntegerMatcher
implements Matcher
{
	@Override
	public List<ResultTree> match(String input)
	{
		if (input.length() <= 0)
			return Collections.<ResultTree> emptyList();
		
		char c= input.charAt(0);
		int i= (c == '+' || c == '-') ? 1 : 0;
		
		if (!Character.isDigit(input.charAt(i++)))
			return Collections.<ResultTree> emptyList();
		
		while (Character.isDigit(input.charAt(i)))
			i++;
		
		return Arrays.asList(new ResultTree(new StringNode(input.substring(0, i)), i));
	}
}
