package info.reflectionsofmind.parser.matcher.common;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.node.StringNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuotedStringMatcher
implements Matcher
{
	@Override
	public List<ResultTree> match(String input)
	{
		if (input.length() <= 0 || input.charAt(0) != '"' )
			return Collections.<ResultTree> emptyList();
		
		int pos = input.indexOf('"', 1);
		while (pos > 0 && input.charAt(pos - 1) == '\'')
			pos = input.indexOf('"', pos+1);

		if (pos == -1 || input.charAt(pos) != '"') 
			return Collections.<ResultTree> emptyList();
		pos++;

		String text= input.substring(0, pos).replaceAll("\'\"", "\"");
		return Arrays.asList(new ResultTree(new StringNode(text), pos));
	}

}
