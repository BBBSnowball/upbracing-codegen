/**
 * 
 */
package info.reflectionsofmind.parser.matcher;

import static info.reflectionsofmind.parser.Matchers.reps;
import static info.reflectionsofmind.parser.Matchers.str;
import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.matcher.Matcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class RepTest
{
	@Test
	public void repShouldSuccedWithinBounds()
	{
		List<ResultTree> results = reps(1, 3, str("12")).match("121212121");

		Assert.assertEquals(3, results.size());
		Assert.assertEquals(2, results.get(0).rest);
		Assert.assertEquals(4, results.get(1).rest);
		Assert.assertEquals(6, results.get(2).rest);
	}

	@Test
	public void repShouldSucceedWithoutBounds()
	{
		Matcher rep = reps(str("12"));
		List<ResultTree> results = rep.match("1212121");

		Assert.assertEquals(4, results.size());
		Assert.assertEquals(0, results.get(0).rest);
		Assert.assertEquals(2, results.get(1).rest);
		Assert.assertEquals(4, results.get(2).rest);
		Assert.assertEquals(6, results.get(3).rest);
	}

	@Test
	public void repShouldFailWithinBounds()
	{
		Matcher rep = reps(2, 3, str("123"));
		List<ResultTree> results = rep.match("12312123");

		Assert.assertTrue(results.isEmpty());
	}
}