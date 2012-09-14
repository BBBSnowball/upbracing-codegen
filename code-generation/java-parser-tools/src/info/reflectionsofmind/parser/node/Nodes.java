package info.reflectionsofmind.parser.node;

import info.reflectionsofmind.util.Strings;

public final class Nodes
{
	private Nodes()
	{
		throw new UnsupportedOperationException("Utility class");
	}
	
	public static String toStringFull(final AbstractNode root)
	{
		final StringBuilder builder = new StringBuilder();

		builder.append(root.id).append(":[").append(root.getText().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r")).append("]");

		for (final AbstractNode node : root.children)
		{
			builder.append("\n").append(Strings.indent(toStringFull(node)));
		}

		return builder.toString();
	}
	
	public static String toStringNamed(final NamedNode root)
	{
		final StringBuilder builder = new StringBuilder();

		builder.append(root.id).append(":[").append(root.getText().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r")).append("]");

		for (final NamedNode node : root.getNamedChildren())
		{
			builder.append("\n").append(Strings.indent(toStringNamed(node)));
		}

		return builder.toString();
	}
}
