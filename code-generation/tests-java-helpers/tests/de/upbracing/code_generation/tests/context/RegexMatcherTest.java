package de.upbracing.code_generation.tests.context;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.IllegalCharsetNameException;
import java.util.regex.Matcher;

import org.junit.Test;

import de.upbracing.code_generation.tests.TestFailedException;
import de.upbracing.code_generation.tests.Toolkit;
import de.upbracing.code_generation.tests.serial.SerialHelper;
import de.upbracing.code_generation.tests.streams.MonitoredInputStream;

public class RegexMatcherTest {

	private Charset charset() {
		try {
			return Charset.forName("iso-8859-1");
		} catch (IllegalCharsetNameException e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream stringStream(String str) {
		return new ByteArrayInputStream(str.getBytes(charset()));
	}

	private SerialHelper wrapStream(final InputStream stream) {
		return new SerialHelper((Toolkit) null, 0) {
			@Override
			public MonitoredInputStream getInputStream() {
				return new MonitoredInputStream(stream);
			}
		};
	}

	private Matcher testRegex(String regex, String match, String remaining)
			throws TestFailedException, IOException {
		// create some dummy objects
		InputStream stream = stringStream(match + remaining);
		SerialHelper sh = wrapStream(stream);
		
		// run RegexMatcher
		Matcher matcher;
		try {
			RegexMatcher m = new RegexMatcher(sh.getInputStream(), regex);
			matcher = m.run(4 * 1000);
			
			// make sure we have matched what we expect to
			assertEquals(true, matcher.matches());
			assertEquals(0, matcher.start());
			assertEquals(match.length(), matcher.end());
			assertEquals(match, matcher.group());
		} finally {
			// get remaining bytes
			// (We also do that, if an error occurs.)
			int remaining_len = stream.available();
			byte buf[] = new byte[remaining_len];
			int len = stream.read(buf);
			//assertEquals(remaining_len, len);
			
			// check remaining bytes
			assertEquals(remaining, new String(buf, charset()));
		}
		
		// return the matcher, so it can be inspected further
		return matcher;
	}
	
	private void testRegexError(String regex, String match, String remaining, String message)
			throws IOException {
		try {
			testRegex(regex, match, remaining);
			fail("expected TestFailedException");
		} catch (TestFailedException e) {
			assertEquals(false, e.getResult().isSuccessful());
			assertEquals(true, e.getResult() instanceof Result.Error);
			assertEquals(message, e.getResult().getMessage());
		}
	}

	//@Test
	/** This tests the CharsetDecoder class which I would like to use for
	 * RegexMatcher. Unfortunately, it fails miserably in my implementation
	 * of Java :-(
	 * 
	 * java -version
	 * java version "1.6.0_24"
	 * OpenJDK Runtime Environment (IcedTea6 1.11.5) (6b24-1.11.5-0ubuntu1~12.04.1)
	 * OpenJDK 64-Bit Server VM (build 20.0-b12, mixed mode)
	 */
	public void testDecoder() {
		String test = "blub";
		Charset charset = Charset.forName("iso-8859-1");	// also tried utf-8
		boolean end_of_input = false;	// also tried true
		
		ByteBuffer buf2 = ByteBuffer.wrap(test.getBytes(charset));
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer cbuf = CharBuffer.allocate(test.length()*2 + 1);
		
		cbuf.position(0);		//DEBUG
		decoder.reset();		//DEBUG
		
		CoderResult result = decoder.decode(buf2, cbuf, end_of_input);
		decoder.flush(cbuf);	//DEBUG
		assertEquals(false, result.isError());
		assertEquals(true, result.isUnderflow());
		assertEquals(test.length(), cbuf.position());
		assertEquals(test, cbuf.subSequence(0, cbuf.position()).toString());
	}

	@Test
	public void test() throws TestFailedException, IOException {
		testRegex("blub", "blub", "abc");
		testRegex("^blub", "blub", "abc");
		testRegexError("^blub", "blu", "", "end of stream");
		testRegexError("^blub", "blua", "bc", "regex doesn't match");
	}

	private int testInt(String match, String remaining)
			throws TestFailedException, IOException {
		// create some dummy objects
		InputStream stream = stringStream(match + remaining);
		SerialHelper sh = wrapStream(stream);
		
		// run RegexMatcher
		int result;
		try {
			result = IntMatcher.expectInt(sh.getInputStream());

			// make sure we have matched what we expect to
			assertEquals(Integer.parseInt(match), result);
		} finally {
			// get remaining bytes
			// (We also do that, if an error occurs.)
			int remaining_len = stream.available();
			byte buf[] = new byte[remaining_len];
			int len = stream.read(buf);
			//assertEquals(remaining_len, len);
			
			// check remaining bytes
			// (Unfortunately, the first byte is lost.)
			assertEquals(remaining.substring(1), new String(buf, charset()));
		}
		
		// return the matcher, so it can be inspected further
		return result;
	}
	
	@Test
	public void testInt() throws TestFailedException, IOException {
		assertEquals(103, testInt("103", "abc"));
		testInt("-103", "abc");
		testInt("0", "abc");
		testInt("103", ".");
		testInt("103", "+");
		testInt("103", "-");
		testInt("3", "abc");
	}
	
}
