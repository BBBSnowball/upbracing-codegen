package de.upbracing.code_generation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class Util {
	public static final Charset UTF8 = Charset.forName("utf-8");
	
	public static String loadStream(InputStream stream, Charset charset) throws IOException {
		Reader reader = new InputStreamReader(stream, charset);
		return loadFromReader(reader);
	}
	
	public static String loadFromReader(Reader reader) throws IOException {
		StringBuffer sb = new StringBuffer();
		char buf[] = new char[256];
		int len;
		while ((len = reader.read(buf)) > 0)
			sb.append(buf, 0, len);
		reader.close();
		return sb.toString();
	}

	private static String getResourcePath(Class<?> clazz, String name) {
		return clazz.getPackage().getName().replace('.', '/') + "/" + name;
	}
	
	public static URL getResourceURL(Class<?> clazz, String name) {
		String path = getResourcePath(clazz, name);
		return clazz.getClassLoader().getResource(path);
	}
	
	public static String loadResourceRaw(Class<?> clazz, String name) {
		String path = getResourcePath(clazz, name);
		InputStream stream = clazz.getClassLoader().getResourceAsStream(path);
		if (stream == null)
			throw new IllegalArgumentException("invalid ressource name: " + name
					+ ", relative to class " + clazz.getCanonicalName()
					+ " -> " + path);
		
		try {
			return loadStream(stream, UTF8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String nl() {
		return System.getProperties().getProperty("line.separator");
	}
	
	public static String fixNL(String string) {
		return string.replaceAll("\r?\n|\r", nl());
	}

	public static String loadResource(Class<?> clazz, String name) {
		return fixNL(loadResourceRaw(clazz, name));
	}
}
