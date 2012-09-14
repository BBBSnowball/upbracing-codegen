package info.reflectionsofmind.parser.node;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNode
{
	public final String id;
	public final List<AbstractNode> children = new ArrayList<AbstractNode>();
	public String text = null;

	public AbstractNode(final String id)
	{
		this.id = id;
	}

	public AbstractNode(final String id, final String text)
	{
		this(id);
		this.text = text;
	}

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
}
