package de.upbracing.code_generation.tests.context;

import java.io.InputStream;
import java.util.regex.Matcher;

import de.upbracing.code_generation.tests.TestFailedException;

public final class IntMatcher {
	private IntMatcher() {}

	
	/** receive a String and decode it as an integer (base 10)
	 * 
	 * The character after the number will be consumed and won't be
	 * available anymore.
	 * 
	 * @param stream stream to read characters from
	 * @param separator character after the number (regex pattern)
	 * @return the parsed number
	 * @throws TestFailedException if the received chars aren't a number
	 */
	public static int expectInt(InputStream stream, String separator) throws TestFailedException {
		RegexMatcher rm = new RegexMatcher(stream, "^([+-]?[0-9]+)(" + separator + ")");
		Matcher m = rm.run(4000);
		return Integer.parseInt(m.group(1));
	}
	
	/** receive a String and decode it as an integer (base 10)
	 * 
	 * The character after the number will be consumed and won't be
	 * available anymore.
	 * 
	 * @param stream stream to read characters from
	 * @return the parsed number
	 * @throws TestFailedException if the received chars aren't a number
	 */
	public static int expectInt(InputStream stream) throws TestFailedException {
		return expectInt(stream, "[^0-9]");
	}
}
