package de.upbracing.code_generation.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class TestHelpers {
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
		return sb.toString();
	}
	
	public static String loadRessource(String name) {
		String path = TestHelpers.class.getPackage().getName().replace('.', '/') + "/" + name;
		InputStream stream = TestHelpers.class.getClassLoader().getResourceAsStream(path);
		if (stream == null)
			throw new IllegalArgumentException("invalid ressource name: " + name + " -> " + path);
		
		try {
			return loadStream(stream, UTF8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
