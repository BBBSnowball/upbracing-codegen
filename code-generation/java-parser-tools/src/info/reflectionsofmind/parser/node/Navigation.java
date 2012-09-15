package info.reflectionsofmind.parser.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Navigation
{

	/**
	 * @return first found node with denoted id.  Returns null if not found.
	 */
	public static AbstractNode findDecendentById(AbstractNode root, String id)
	{
		List<AbstractNode> todo = new ArrayList<AbstractNode>();
		HashSet<AbstractNode> done = new HashSet<AbstractNode>();
		todo.addAll(root.children);

		while (!todo.isEmpty()) 
		{
			AbstractNode node= todo.remove(0);
			if (done.contains(node))
				continue;
			
			if (id.equals(node.id))
				return node;
			
			todo.addAll(node.children);
		}

		return null;
	}	

	public static List<AbstractNode> findAllDecendentsById(AbstractNode root, String id)
	{
		List<AbstractNode> matches = new ArrayList<AbstractNode>();
		List<AbstractNode> todo = new ArrayList<AbstractNode>();
		HashSet<AbstractNode> done = new HashSet<AbstractNode>();
		todo.addAll(root.children);

		while (!todo.isEmpty()) 
		{
			AbstractNode node= todo.remove(0);
			if (done.contains(node))
				continue;
			
			if (id.equals(node.id))
				matches.add(node);
			
			todo.addAll(node.children);
		}

		return matches;
	}	

	public static List<NamedNode> getNamedChildren(AbstractNode root)
	{
		List<NamedNode> namedNodes = new ArrayList<NamedNode>();

		for (AbstractNode node : root.children)
		{
			if (node instanceof NamedNode)
			{
				namedNodes.add((NamedNode) node);
			}
			else
			{
				namedNodes.addAll(getNamedChildren(node));
			}
		}

		return namedNodes;
	}	
}
