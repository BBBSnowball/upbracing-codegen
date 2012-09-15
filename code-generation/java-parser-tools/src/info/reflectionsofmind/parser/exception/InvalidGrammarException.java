package info.reflectionsofmind.parser.exception;

import info.reflectionsofmind.parser.Matchers;
import info.reflectionsofmind.parser.ResultTree;

import java.util.List;

@SuppressWarnings("serial")
public class InvalidGrammarException extends GrammarParsingException
{
	public InvalidGrammarException(List<ResultTree> invalidResults)
	{
		super("Grammar cannot be fully parsed. Longest tree:\n" + Matchers.toStringNamed(getLongestResult(invalidResults)));
	}

	private static ResultTree getLongestResult(List<ResultTree> results)
	{
		ResultTree longestResult = results.get(0);

		for (ResultTree result : results)
			if (result.rest > longestResult.rest) longestResult = result;
		
		return longestResult;
	}
}
