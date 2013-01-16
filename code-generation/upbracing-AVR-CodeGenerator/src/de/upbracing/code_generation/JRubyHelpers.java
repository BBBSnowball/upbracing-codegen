package de.upbracing.code_generation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import statemachine.GlobalCode;
import statemachine.StatemachineFactory;


import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;

public final class JRubyHelpers {
	// we don't want any instances of this class
	private JRubyHelpers() { }
	
	public static String readResource(String name) throws IOException {
		InputStream stream = JRubyHelpers.class.getClassLoader().getResourceAsStream(name);
		if (stream == null)
			return null;
		Reader reader = new InputStreamReader(stream, Charset.forName("utf-8"));
		
		return readContent(reader);
	}

	public static String readContent(Reader reader) throws IOException {
		StringBuffer sb = new StringBuffer();
		char buf[] = new char[256];
		int len;
		while ((len = reader.read(buf)) > 0)
			sb.append(buf, 0, len);
		
		return sb.toString();
	}
	
	public static GlobalCode addGlobalCode(StateMachineForGeneration smg,
			boolean in_header, String code) {
		GlobalCode cb = getStatemachineFactory().createGlobalCode();
		cb.setInHeaderFile(in_header);
		cb.setCode(code);
		smg.getGlobalCodeBoxes().add(cb);
		return cb;
	}
	
	public static StatemachineFactory getStatemachineFactory() {
		return new statemachine.impl.StatemachineFactoryImpl();
	}
}
