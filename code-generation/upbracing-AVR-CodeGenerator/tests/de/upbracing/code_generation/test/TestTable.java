package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upbracing.code_generation.Table;

public class TestTable {
	@Test
	public void testIt() {
		// test base functionality
		StringBuffer sb = new StringBuffer();
		sb.append("some text\n");
		Table t = new Table(sb);
		sb.append("some more text\n");
		t.start();
		sb.append("abc&&&def&&&g&&&xy\na&&&defghi&&&&&&\n&&&&&&&&&xyz");
		t.finish(",");
		sb.append("\nend");
		assertEquals("some text\nsome more text\nabc,def   ,g,xy\na  ,defghi, ,\n   ,      , ,xyz\nend",
				sb.toString());
		
		// test using it a second time, no column seperator
		t.start();
		sb.append("a&&&bc\nab&&&c");
		t.finish();
		assertEquals("some text\nsome more text\nabc,def   ,g,xy\na  ,defghi, ,\n   ,      , ,xyz\nenda bc\nabc",
				sb.toString());
		
		// test: using it a second time, spaces at end of line, empty lines, shorter lines, custom seperator
		sb.setLength(0);
		t = new Table(sb, "-", true);
		sb.append("x\n");
		t.start();
		sb.append("a-bc-def\n\nab-cde-f\na-b\n\n");
		t.finish();
		assertEquals("x\na bc def\n\nabcdef  \na b  \n\n",
				sb.toString());
		
		// test: shorter line with no padding spaces at the end
		sb.setLength(0);
		t = new Table(sb, "-", false);
		t.start();
		sb.append("aaa-bcd\nab\nab-");
		t.finish();
		assertEquals("aaabcd\nab\nab ",
				sb.toString());
		
		// test: lots of empty lines and columns
		sb.setLength(0);
		t.start();
		sb.append("------\n\n\n\n--\n\n\n\n\n");
		t.finish("-");
		assertEquals("------\n\n\n\n--\n\n\n\n\n",
				sb.toString());
	}
}
