package de.upbracing.code_generation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.script.ScriptException;

import de.upbracing.code_generation.CodeGenerationMain;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.utils.Util;

public final class TestHelpers {
	private TestHelpers() { }
	
	public static URL getResourceURL(String name) {
		return Util.getResourceURL(TestHelpers.class, name);
	}

	public static String getResourceURLForJRuby(String name) {
		return Util.getResourceURLForJRuby(TestHelpers.class, name);
	}
	
	public static String loadResource(String name) {
		return Util.loadResource(TestHelpers.class, name);
	}

	public static CodeGeneratorConfigurations loadConfigFromRessource(
			String name, Map<String, Object> global_vars) throws ScriptException {
		return CodeGenerationMain.loadConfig(
				Util.getResourceStream(TestHelpers.class, name),
				name, null, global_vars);
	}
	
	public static void assertListEquals(Collection<?> expected, Collection<?> actual) {
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
	
	public static void assertListEquals(Collection<?> actual, Object... expected) {
		assertListEquals(Arrays.asList(expected), actual);
	}
}
