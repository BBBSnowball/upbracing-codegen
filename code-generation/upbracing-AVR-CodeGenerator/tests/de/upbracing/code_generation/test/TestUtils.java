package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import de.upbracing.code_generation.utils.Util;

public class TestUtils {
	@Test
	public void testFixNL() {
		String nl = System.getProperty("line.separator");
		assertEquals(nl + "abc" + nl + "def" + nl + "ghi" + nl + nl,
				Util.fixNL("\nabc\r\ndef\rghi\n\r"));
	}
	
	@Test
	public void testAdjustToBeRelativeTo() {
		assertEquals(new File(".", "abc").toString(), Util.adjustToBeRelativeTo("abc", "."));
		assertEquals(new File(".", "abc").toString(), Util.adjustToBeRelativeTo("abc", new File(".").getAbsolutePath()));
		assertEquals(new File(new File(".", ".."), "abc").toString(), Util.adjustToBeRelativeTo("abc", "blub"));
		String cwd_name = new File("abc").getAbsoluteFile().getParentFile().getName();
		assertEquals(new File(new File(".", cwd_name), "abc").toString(), Util.adjustToBeRelativeTo("abc", ".."));
		
	}
}
