package de.upbracing.code_generation.fsm.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.codehaus.jparsec.error.ParserException;
import org.junit.Test;

public class TestParsers {

	@Test
	public void testParseStateActions() {
		assertListEquals(
				FSMParsers.parseStateActions("EXIT / blub(a,b/2)"),
				
				new Action(ActionType.EXIT, "blub(a,b/2)"));

		assertListEquals(
				FSMParsers.parseStateActions("EXIT / blub(a,b/2)\nENTER/abc"),
				
				new Action(ActionType.EXIT, "blub(a,b/2)"),
				new Action(ActionType.ENTER, "abc"));

		assertListEquals(
				FSMParsers.parseStateActions("\n\nEXIT / blub(a,b/2)\n\nENTER/abc\n\n\n"),
				
				new Action(ActionType.EXIT, "blub(a,b/2)"),
				new Action(ActionType.ENTER, "abc"));
		
		assertListEquals(
				FSMParsers.parseStateActions("EXIT / blub(a,\nb/2)"),
				
				new Action(ActionType.EXIT, "blub(a,\nb/2)"));

		assertListEquals(
				FSMParsers.parseStateActions("EXIT / blub(a,b/2)\nENTER/{\nabc;\ndef;\n}\n"),
				
				new Action(ActionType.EXIT, "blub(a,b/2)"),
				new Action(ActionType.ENTER, "{\nabc;\ndef;\n}"));
		
		try {
			FSMParsers.parseStateActions("EXIT / blub(a,\nb/2)\nabc");
			fail("expected ParserException");
		} catch (ParserException e) {
			// expected
		}

		assertListEquals(
				FSMParsers.parseStateActions("EXIT / blub(a,b/2); {\nabc(x);\n}"),
				
				new Action(ActionType.EXIT, "blub(a,b/2); {\nabc(x);\n}"));

		assertListEquals(
				FSMParsers.parseStateActions("EXIT / blub(a,b/2); \\\n blork(42); {\nabc(\\x);\n}\nENTER / b"),
				
				new Action(ActionType.EXIT, "blub(a,b/2); \\\n blork(42); {\nabc(\\x);\n}"),
				new Action(ActionType.ENTER, "b"));

		assertListEquals(
				FSMParsers.parseStateActions("EXIT / blub(\"a)\",b/2,\"[c\"); \\\n blork(42); {\nabc(\\x, \"}\");\n}\nENTER / b"),
				
				new Action(ActionType.EXIT, "blub(\"a)\",b/2,\"[c\"); \\\n blork(42); {\nabc(\\x, \"}\");\n}"),
				new Action(ActionType.ENTER, "b"));
	}

	@Test
	public void testParseTransitionInfo() {
		assertEquals(
				new TransitionInfo("event", "a>0", "blub(a,b/2)"),
				FSMParsers.parseTransitionInfo("event [a>0] / blub(a,b/2)"));

		assertEquals(
				new TransitionInfo("event", "a>0", null),
				FSMParsers.parseTransitionInfo("event [a>0]"));
		
		assertEquals(
				new TransitionInfo("event", null, "blub(a,b/2)"),
				FSMParsers.parseTransitionInfo("event / blub(a,b/2)"));

		assertEquals(
				new TransitionInfo("event", null, null),
				FSMParsers.parseTransitionInfo("event"));

		assertEquals(
				new TransitionInfo(null, "a>0", null),
				FSMParsers.parseTransitionInfo("[a>0]"));
		
		assertEquals(
				new TransitionInfo(null, null, "blub(a,b/2)"),
				FSMParsers.parseTransitionInfo("/ blub(a,b/2)"));

		assertEquals(
				new TransitionInfo(null, null, null),
				FSMParsers.parseTransitionInfo(""));
		

		assertEquals(
				new TransitionInfo("event", "a[i]>0", null),
				FSMParsers.parseTransitionInfo("event [a[i]>0]"));

		assertEquals(
				new TransitionInfo(null, null, "blub(a,\nb/2)"),
				FSMParsers.parseTransitionInfo("/ blub(a,\nb/2)"));
		
		assertEquals(
				new TransitionInfo(null, null, "blub(\"a)\", b)"),
				FSMParsers.parseTransitionInfo("/ blub(\"a)\", b)"));
	}
	
	private static void assertListEquals(Collection<?> expected, Collection<?> actual) {
		//assertEquals(expected.size(), actual.size());
		Iterator<?> it1 = expected.iterator(),
				it2 = actual.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			assertEquals(it1.next(), it2.next());
		}
		
		if (it1.hasNext())
			assertFalse("expected at least one more element: " + it1.next(), it1.hasNext());
		
		if (it2.hasNext())
			assertFalse("unexpected element: " + it2.next(), it2.hasNext());
	}
	
	private static void assertListEquals(Collection<?> actual, Object... expected) {
		assertListEquals(Arrays.asList(expected), actual);
	}

}
