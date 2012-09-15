package info.reflectionsofmind.parser.node;

public class RegexNode extends PrimitiveNode {
	public String[] groups;
	
	public RegexNode()
	{
		super("#REGEX");
	}
	
	public RegexNode(String text, String[] groups)
	{
		this();
		this.text = text;
		this.groups = groups;
	}
}
