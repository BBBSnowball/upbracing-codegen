package info.reflectionsofmind.parser.node;

import info.reflectionsofmind.parser.transform.NodePredicate;
import info.reflectionsofmind.parser.transform.Transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class AbstractNode
{
	public final String id;
	public final List<AbstractNode> children = new ArrayList<AbstractNode>();
	public String text = null;

	/** Interpretation of the parsing tree. Use {@link Transform} to fill it. */
	public Object value;

	public AbstractNode(final String id)
	{
		this.id = id;
	}

	public AbstractNode(final String id, final String text)
	{
		this(id);
		this.text = text;
	}

	/** concatenate text of all child nodes */
	public String getText()
	{
		if (this.text != null) return this.text;

		final StringBuilder builder = new StringBuilder();

		for (final AbstractNode node : this.children)
		{
			builder.append(node.getText());
		}

		return builder.toString();
	}
	
	/** find all nodes which match a predicate except children of matching nodes
	 * 
	 * It searches for nodes recursively, but if a node is
	 * added to the list, its children are be ignored.
	 * 
	 * If this node matches, it will be the only element of the list.
	 *
	 * The list has a fast {@link List#get(int)}.
	 */ 
	public List<AbstractNode> findNodes(NodePredicate predicate) {
		if (predicate.appliesTo(this))
			return Arrays.asList(this);
		
		ArrayList<AbstractNode> nodes = new ArrayList<AbstractNode>();
		
		findChildren(predicate, nodes);
		
		return nodes;
	}
	
	/** find all (indirect) children which match a predicate except children of matching nodes
	 * 
	 * It searches for nodes recursively, but if a node is
	 * added to the list, its children are be ignored.
	 * 
	 * This node itself will never be part of the list.
	 *
	 * The list has a fast {@link List#get(int)}.
	 */ 
	public List<AbstractNode> findChildren(NodePredicate predicate) {
		ArrayList<AbstractNode> nodes = new ArrayList<AbstractNode>();
		
		findChildren(predicate, nodes);
		
		return nodes;
	}

	public void findNodes(NodePredicate predicate, List<AbstractNode> nodes) {
		if (predicate.appliesTo(this))
			nodes.add(this);
		else
			findChildren(predicate, nodes);
	}

	public void findChildren(NodePredicate predicate, List<AbstractNode> nodes) {
		for (AbstractNode child : children)
			child.findNodes(predicate, nodes);
	}
	
	/** find all nodes which have a name
	 * 
	 * It searches for nodes recursively, but if a node is
	 * added to the list, its children are be ignored.
	 * 
	 * Named nodes have a name that doesn't start with a
	 * hash sign, e.g. "#SEQUENCE" will be ignored.
	 *
	 * The list has a fast {@link List#get(int)}.
	 */
	//TODO this is similar to Navigation.getNamedChildren and NamedNode.getNamedChildren
	public List<AbstractNode> findNamedChildren() {
		return findChildren(new NodePredicate() {
			@Override
			public Set<String> appliesToIDs() {
				return NodePredicate.ANY_ID;
			}
			
			@Override
			public boolean appliesTo(AbstractNode node) {
				return node.id != null && !node.id.equals("") && !node.id.startsWith("#");
			}
		});
	}
}
