package info.reflectionsofmind.parser;

import info.reflectionsofmind.parser.exception.GrammarParsingException;
import info.reflectionsofmind.parser.exception.InvalidGrammarException;
import info.reflectionsofmind.parser.exception.UndefinedSymbolException;

import org.junit.Test;

public class GrammarTest
{
	@Test(expected = UndefinedSymbolException.class)
	public void shouldThrowExceptionOnUndefinedSymbols() throws GrammarParsingException
	{
		String grammar = "a ::= (b c) b ::= %digit%";
		Grammar.generate(grammar, "a");
	}
	
	@Test
	public void shouldAllowAnyNumberOfSpaces() throws GrammarParsingException
	{
		String grammar = "         a   ::=   ( b   b )           b   ::=  %digit%              ";
		Grammar.generate(grammar, "a");
	}
	
	@Test(expected = InvalidGrammarException.class)
	public void shouldFailIfGrammarIsInvalid() throws GrammarParsingException
	{
		Grammar.generate("a ::= (%digi", "a");
	}
}