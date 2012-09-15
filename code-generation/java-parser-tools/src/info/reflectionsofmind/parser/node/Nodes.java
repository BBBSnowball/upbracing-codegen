package info.reflectionsofmind.parser.node;

import info.reflectionsofmind.util.Strings;

public final class Nodes
{
	private Nodes()
	{
		throw new UnsupportedOperationException("Utility class");
	}
	
	public static void toStringFull(final AbstractNode root, String indent, StringBuilder builder)
	{
		builder.append(indent)
			.append(root.id)
			.append(":[").append(root.getText().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r")).append("]");

		for (final AbstractNode node : root.children)
		{
			builder.append("\n");
			builder.append(indent);
			toStringFull(node, indent + "  ", builder);
		}
	}
	
	public static String toStringFull(final AbstractNode root) {
		final StringBuilder builder = new StringBuilder();
		toStringFull(root, "", builder);
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
	
	public static void toStringWithValue(final AbstractNode root, String indent, StringBuilder builder)
	{
		builder.append(indent)
			.append(root.id);
		
		if (root.value != null)
			builder.append(" -> ").append(root.value.toString());

		for (final AbstractNode node : root.children)
		{
			builder.append("\n");
			builder.append(indent);
			toStringWithValue(node, indent + "  ", builder);
		}
	}
	
	public static String toStringWithValue(final AbstractNode root) {
		final StringBuilder builder = new StringBuilder();
		toStringWithValue(root, "", builder);
		return builder.toString();
	}
}
