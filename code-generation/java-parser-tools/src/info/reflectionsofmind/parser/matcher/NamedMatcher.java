package info.reflectionsofmind.parser.matcher;

import java.util.ArrayList;
import java.util.List;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.node.NamedNode;

public class NamedMatcher implements Matcher
{
	private final String id;
	private Matcher definition;

	public NamedMatcher(String id)
	{
		this.id = id;
	}
	
	public NamedMatcher define(Matcher definition)
	{
		this.definition = definition;
		return this;
	}
	
	@Override
	public List<ResultTree> match(String input)
	{
		List<ResultTree> results = new ArrayList<ResultTree>();
		
		for (ResultTree result : this.definition.match(input))
		{
			final AbstractNode node = new NamedNode(this.id);
			node.children.add(result.root);
			results.add(new ResultTree(node, result.rest));
		}
		
		return results;
	}
}
