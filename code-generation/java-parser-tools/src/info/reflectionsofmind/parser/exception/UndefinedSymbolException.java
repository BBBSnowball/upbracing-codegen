package info.reflectionsofmind.parser.exception;

@SuppressWarnings("serial")
public class UndefinedSymbolException extends GrammarParsingException
{
	public UndefinedSymbolException(String symbol)
	{
		super("Undefined symbol:"+symbol);
	}
}
