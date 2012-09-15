package info.reflectionsofmind.parser.exception;

import info.reflectionsofmind.parser.Matchers;
import info.reflectionsofmind.parser.ResultTree;

import java.util.List;
 
public class AmbiguousGrammarException extends GrammarParsingException
{
	public AmbiguousGrammarException(List<ResultTree> ambiguousResults)
	{
		super("May be parsed as [" + ambiguousResults.size() + "] trees:\n" + Matchers.toStringNamed(ambiguousResults));
	}
}
