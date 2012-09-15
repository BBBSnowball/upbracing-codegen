/**
 * 
 */
package info.reflectionsofmind.parser.matcher;

import static info.reflectionsofmind.parser.Matchers.seq;
import static info.reflectionsofmind.parser.Matchers.str;
import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.matcher.Matcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SeqTest
{
	@Test
	public void seqShouldSucceedOnExactSequence()
	{
		Matcher seq = seq(str("1"), str("2"));
		List<ResultTree> results = seq.match("123");

		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, results.get(0).rest);
		Assert.assertEquals(2, results.get(0).root.children.size());
	}

	@Test
	public void seqShouldFailOnGarbageInSequence()
	{
		Matcher seq = seq(str("1"), str("2"));
		List<ResultTree> results = seq.match("132");

		Assert.assertTrue(results.isEmpty());
	}
}