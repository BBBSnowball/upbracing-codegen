package info.reflectionsofmind.parser.matcher;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.node.RegexNode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class RegexMatcher implements Matcher {
	private final Pattern pattern;

	public RegexMatcher(String regex)
	{
		// match it only at the start of input
		this.pattern = Pattern.compile("\\A(?:" + regex + ")");
	}

	@Override
	public List<ResultTree> match(final String input)
	{
		java.util.regex.Matcher m = pattern.matcher(input);
		if (!m.matches())
			return Collections.<ResultTree> emptyList();
		
		String groups[] = new String[m.groupCount()];
		for (int i=0;i<groups.length;i++)
			groups[i] = m.group(i);
		
		RegexNode node = new RegexNode(m.group(), groups);
		
		return Arrays.asList(new ResultTree(node, m.end()));
	}
}
