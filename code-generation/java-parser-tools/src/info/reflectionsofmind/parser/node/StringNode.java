package info.reflectionsofmind.parser.node;

public final class StringNode extends PrimitiveNode
{
	public StringNode()
	{
		super("#STRING");
	}
	
	public StringNode(String text)
	{
		this();
		this.text = text;
	}
}
