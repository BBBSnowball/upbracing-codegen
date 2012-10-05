package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upbracing.code_generation.utils.Util;

public class TestUtils {
	@Test
	public void testFixNL() {
		String nl = System.getProperty("line.separator");
		assertEquals(nl + "abc" + nl + "def" + nl + "ghi" + nl + nl,
				Util.fixNL("\nabc\r\ndef\rghi\n\r"));
	}
}
