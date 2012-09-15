package info.reflectionsofmind.parser.exception;

@SuppressWarnings("serial")
public abstract class GrammarParsingException extends Exception
{
	public GrammarParsingException(String message)
	{
		super(message);
	}
}
