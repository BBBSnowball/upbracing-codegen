package info.reflectionsofmind.parser.exception;

public class UndefinedSymbolException extends GrammarParsingException
{
	public UndefinedSymbolException(String symbol)
	{
		super("Undefined symbol:"+symbol);
	}
}
