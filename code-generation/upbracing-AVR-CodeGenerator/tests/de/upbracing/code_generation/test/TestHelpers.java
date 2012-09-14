package de.upbracing.code_generation.test;

import java.net.URL;

import de.upbracing.code_generation.utils.Util;

public class TestHelpers {
	public static URL getResourceURL(String name) {
		return Util.getResourceURL(TestHelpers.class, name);
	}
	
	public static String loadResource(String name) {
		return Util.loadResource(TestHelpers.class, name);
	}
}
