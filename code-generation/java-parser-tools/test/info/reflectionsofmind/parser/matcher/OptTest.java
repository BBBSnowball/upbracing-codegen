/**
 * 
 */
package info.reflectionsofmind.parser.matcher;

import static info.reflectionsofmind.parser.Matchers.cho;
import static info.reflectionsofmind.parser.Matchers.opt;
import static info.reflectionsofmind.parser.Matchers.str;
import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.matcher.Matcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class OptTest
{
	@Test
	public void optShouldSucceedOnMatch()
	{
		List<ResultTree> results = opt(str("12")).match("123");

		Assert.assertEquals(2, results.size());
	}

	@Test
	public void optShouldSucceedOnNoMatch()
	{
		Matcher opt = cho(str("12"), str(""));
		List<ResultTree> results = opt.match("132");

		Assert.assertEquals(1, results.size());
	}
}