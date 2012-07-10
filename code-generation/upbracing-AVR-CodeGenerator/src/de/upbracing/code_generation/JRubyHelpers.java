package de.upbracing.code_generation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class JRubyHelpers {
	public static String readResource(String name) throws IOException {
		InputStream stream = JRubyHelpers.class.getClassLoader().getResourceAsStream(name);
		if (stream == null)
			return null;
		Reader reader = new InputStreamReader(stream, Charset.forName("utf-8"));
		
		StringBuffer sb = new StringBuffer();
		char buf[] = new char[256];
		int len;
		while ((len = reader.read(buf)) > 0)
			sb.append(buf, 0, len);
		
		return sb.toString();
	}
}
