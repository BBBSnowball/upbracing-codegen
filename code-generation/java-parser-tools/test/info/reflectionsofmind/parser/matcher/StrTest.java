/**
 * 
 */
package info.reflectionsofmind.parser.matcher;

import static info.reflectionsofmind.parser.Matchers.str;

import info.reflectionsofmind.parser.ResultTree;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class StrTest
{
	@Test
	public void strShouldFailOnSubstring()
	{
		List<ResultTree> results = str("test").match("test1");

		Assert.assertEquals(1, results.size());
		Assert.assertEquals(4, results.get(0).rest);
		Assert.assertTrue(results.get(0).root.children.isEmpty());
	}

	@Test
	public void strShouldFailOnInequal()
	{
		List<ResultTree> results = str("test").match("tes1t");

		Assert.assertEquals(0, results.size());
	}
}