package de.upbracing.code_generation.fsm.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

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
		
		assertListEquals(
				FSMParsers.parseStateActions("ENTER/DDRB = 0xff \n ENTER/PORTB++ \n ALWAYS/wdt_reset()"),
				
				new Action(ActionType.ENTER, "DDRB = 0xff"),
				new Action(ActionType.ENTER, "PORTB++"),
				new Action(ActionType.ALWAYS, "wdt_reset()"));
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
		
		assertEquals(
				new TransitionInfo("event", "a[i\n]\n>\n0", "blub(\"a)\", b)"),
				FSMParsers.parseTransitionInfo("event [a[i\n]\n>\n0] / blub(\"a)\", b)"));
		

		assertEquals(
				new TransitionInfo(null, null, "blub(\"a)\", b)", "wait", 100e-3),
				FSMParsers.parseTransitionInfo("wait(100ms) / blub(\"a)\", b)"));
		
		assertEquals(
				new TransitionInfo(null, null, "blub(\"a)\", b)", "at", 3.2e-7),
				FSMParsers.parseTransitionInfo("at(3.2e2ns) / blub(\"a)\", b)"));
		
		assertEquals(
				new TransitionInfo(null, null, "wait(100ms)", "before", 1e2*1e-9),
				FSMParsers.parseTransitionInfo("before(1e2ns) / wait(100ms)"));
		
		assertEquals(
				new TransitionInfo(null, null, "blub", "after", 100),
				FSMParsers.parseTransitionInfo("after(1e2s) / blub"));
		
		assertEquals(
				new TransitionInfo(null, null, "blub", "wait", 100),
				FSMParsers.parseTransitionInfo("wait (1e2s) / blub"));
		
		assertEquals(
				new TransitionInfo(null, null, null, "wait", 3600*24),
				FSMParsers.parseTransitionInfo("wait ( 1 day )  "));
		
		assertEquals(
				new TransitionInfo(null, null, null, "wait", 3600*24*2),
				FSMParsers.parseTransitionInfo("wait 2 days"));
		
		assertEquals(
				new TransitionInfo(null, null, null, "wait", 3600*3.2),
				FSMParsers.parseTransitionInfo("wait 3.2h"));
		
		assertEquals(
				new TransitionInfo(null, null, null, "wait", 3600),
				FSMParsers.parseTransitionInfo("wait 1.0hour"));
		
		assertEquals(
				new TransitionInfo(null, null, null, "wait", 3600*.2),
				FSMParsers.parseTransitionInfo("wait .2hours"));
		
		assertEquals(
				new TransitionInfo(null, null, null, "wait", .3e-5),
				FSMParsers.parseTransitionInfo("wait .3e1us"));
		
		assertEquals(
				new TransitionInfo(null, null, null, "wait", 42e-6),
				FSMParsers.parseTransitionInfo("wait 42.e6 ps"));
		
		assertEquals(
				new TransitionInfo(null, null, null, "wait", 3600/4.0),
				FSMParsers.parseTransitionInfo("wait 1/4h"));
		
		assertEquals(
				new TransitionInfo("blub", null, null, "wait", 3600/4.0),
				FSMParsers.parseTransitionInfo("blub:wait 1/4h"));
		
		assertEquals(
				new TransitionInfo("foo", null, null, "wait", 3600*24/3.0),
				FSMParsers.parseTransitionInfo("foo:wait(1/3day)"));
		
		assertEquals(
				new TransitionInfo("bar", null, "x()", "at", 3600*24/3.0),
				FSMParsers.parseTransitionInfo("bar : at(1/3 day) / x()"));
		
		assertEquals(
				new TransitionInfo("bar", null, "x()", "at", 3600*24/3.0),
				FSMParsers.parseTransitionInfo(" bar : at(1/3 day) / x()"));
		
		assertEquals(
				new TransitionInfo(null, null, "x()", "at", 3600*24/3.0),
				FSMParsers.parseTransitionInfo(" : at(1/3 day) / x()"));
		
		assertEquals(
				new TransitionInfo(null, null, "x()", "at", 3600*24/3.0),
				FSMParsers.parseTransitionInfo(":at(1/3 day) / x()"));
		
		assertEquals(
				new TransitionInfo(null, null, "x()", "at", 60+13),
				FSMParsers.parseTransitionInfo("at 1:13 / x()"));
		
		assertEquals(
				new TransitionInfo(null, null, "x()", "at", 3600+2*60+13.7),
				FSMParsers.parseTransitionInfo("at 1:02:13.7 / x()"));
		//NOTE: 1:02:.7 would not work
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

	public static void main(String args[]) throws IOException {
		System.in.read();
		TestParsers test = new TestParsers();
		test.testParseStateActions();
		test.testParseTransitionInfo();
	}
}
