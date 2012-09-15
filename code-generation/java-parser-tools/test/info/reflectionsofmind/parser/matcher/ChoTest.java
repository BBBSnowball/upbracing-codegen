/**
 * 
 */
package info.reflectionsofmind.parser.matcher;

import static info.reflectionsofmind.parser.Matchers.cho;
import static info.reflectionsofmind.parser.Matchers.str;

import info.reflectionsofmind.parser.ResultTree;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ChoTest
{
	@Test
	public void choShouldReturnBothTreesIfBothSucceed()
	{
		List<ResultTree> results = cho(str("12"), str("123")).match("1234");

		Assert.assertEquals(2, results.size());
		Assert.assertEquals(2, results.get(0).rest);
		Assert.assertEquals(3, results.get(1).rest);
		Assert.assertTrue(!results.get(0).root.children.isEmpty());
		Assert.assertTrue(!results.get(1).root.children.isEmpty());
	}

	@Test
	public void choShouldFailIfNoOptionMatches()
	{
		List<ResultTree> results = cho(str("12"), str("123")).match("132");

		Assert.assertTrue(results.isEmpty());
	}
}