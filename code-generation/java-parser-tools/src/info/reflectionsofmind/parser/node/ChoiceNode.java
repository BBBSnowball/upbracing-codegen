package info.reflectionsofmind.parser.node;

public final class ChoiceNode extends PrimitiveNode
{
	private final int choiceIndex;
	
	public ChoiceNode(int choiceIndex)
	{
		super("#CHOICE[" + choiceIndex + "]");
		this.choiceIndex = choiceIndex;
	}
	
	public int getChoiceIndex()
	{
		return choiceIndex;
	}
}
