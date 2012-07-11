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

	@Test
	public void testAdvancedFeatures() {
		StringBuffer sb = new StringBuffer();
		Table t = new Table(sb, "&%OPT%&");

		// test column alignment
		sb.setLength(0);
		t.start();
		sb.append("a&>&bc&-&xyz\nabc&&xxxxx&&a\na&<&b&&zzzzzz");
		t.finish("|");
		assertEquals("a  |   bc| xyz\nabc|xxxxx|  a\na  |b    |zzzzzz",
				sb.toString());
		
		// test alignment for first column
		sb.setLength(0);
		t.start();
		sb.append("&_-&a&>&bc&<&xyz\na&&xxxxx&-&a\naaaaaa&-&b&&zzzzzz\n&_>&a&-&b&&c\n");
		t.finish("|");
		assertEquals("  a   |   bc|xyz\n  a   |xxxxx|  a\naaaaaa|  b  |zzzzzz\n     a|  b  |c\n",
				sb.toString());

		// test simple table options
		sb.setLength(0);
		t.start();
		sb.append("&:<->:&\n&_>&a&&b&&c\nd&<&e&&f\naaa&&bbb&&ccc");
		t.finish("|");
		assertEquals("  a| b |  c\nd  |e  |  f\naaa|bbb|ccc",
				sb.toString());
		
		// test column sets
		sb.setLength(0);
		t.start();
		sb.append("abcde&&abcd&&abcde\n&b&ab&&c\n&_>&a&&bc\n&>&x\n&a-&j&-&k&-&l\n&b>&x");
		t.finish("-");
		assertEquals("abcde-abcd-abcde\nab-c\n a-bc\n  - x\n  j  - k  -  l\n x",
				sb.toString());
		
		// test column spanning
		sb.setLength(0);
		t.start();
		sb.append("&_2&abcdef&&ghi&&jkl&2&mnopqr\nab&2&cdefgh&2&jklmn&&qr\na&&cd&2&ghijkl&2&nop");
		t.finish("");
		assertEquals("abcdefghijklmnopqr\nab cdefghjklmn  qr\na  cd ghijklnop",
				sb.toString());

		// test column spanning with column seperator
		sb.setLength(0);
		t.start();
		sb.append("&_2&abcdef&&ghi&&jkl&2&mnopqr\nab&2&cdefgh&2&jklmn&&qr\na&&cd&2&ghijkl&2&nop&0&ignored");
		t.finish("-");
		assertEquals("abcdef-ghi-jkl-mnopqr\nab -cdefgh-jklmn  -qr\na  -cd-ghijkl -nop",
				sb.toString());
		
		// test ignored columns (span = 0)
		sb.setLength(0);
		t.start();
		sb.append("&_2&abcdef&0&ignored&&ghi&&jkl&2&mnopqr&0&ignored\n&_0&ignored&&ab&2&cdefgh&2&jklmn&&qr\na&&cd&2&ghijkl&2&nop&0&ignored");
		t.finish("-");
		assertEquals("abcdef-ghi-jkl-mnopqr\nab -cdefgh-jklmn  -qr\na  -cd-ghijkl -nop",
				sb.toString());
		
		// test zero spanning and column alignment with spanning
		sb.setLength(0);
		t.start();
		sb.append("&_2&abcdef&&ghi&&jkl&2&mnopqr\n&_>&ab&2&cdefgh&2&jklmn&&qr\na&&cd&2&ghijkl&2>&nop");
		t.finish("-");
		assertEquals("abcdef-ghi-jkl-mnopqr\n ab-cdefgh-jklmn  -qr\na  -cd-ghijkl -   nop",
				sb.toString());
	}
}
