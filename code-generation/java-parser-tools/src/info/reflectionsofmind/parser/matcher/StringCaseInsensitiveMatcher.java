package info.reflectionsofmind.parser.matcher;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.node.StringNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class StringCaseInsensitiveMatcher implements Matcher {
	private final String string;

	public StringCaseInsensitiveMatcher(String string) {
		this.string = string;
	}

	@Override
	public List<ResultTree> match(final String input)
	{
		if (input.length() < string.length())
			return Collections.<ResultTree> emptyList();
		
		String prefix = input.substring(0, string.length());
		if (prefix.equalsIgnoreCase(string))
			return Arrays.asList(new ResultTree(new StringNode(prefix), prefix.length()));
		else
			return Collections.<ResultTree> emptyList();
	}
}