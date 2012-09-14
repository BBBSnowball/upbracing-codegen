package info.reflectionsofmind.parser.node;

import java.util.Iterator;
import java.util.List;

public class NamedNode extends AbstractNode implements Iterable<NamedNode>
{
	public NamedNode(final String id)
	{
		super(id);
	}
	
	public NamedNode(final String id, final String text)
	{
		super(id, text);
	}
	
	public List<NamedNode> getNamedChildren()
	{
		return Navigation.getNamedChildren(this);
	}
	
	public NamedNode get(final int index)
	{
		return getNamedChildren().get(index);
	}
	
	public Iterator<NamedNode> iterator()
	{
		return getNamedChildren().iterator();
	}
	
	public boolean is(final String id)
	{
		return this.id.equals(id);
	}
	
	@Override
	public String toString()
	{
		return "[" + this.id + ": " + getText() + "]";
	}
}