package de.upbracing.code_generation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import de.upbracing.code_generation.config.MCUConfiguration;

public class SimpleTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// use JSR 223 API to invoke JRuby
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("jruby");

		MCUConfiguration config = new MCUConfiguration();
		try {
			//TODO set class path for JRuby, so the config.rb script can 'require' other files
			// System.setProperty("org.jruby.embed.class.path", ...);
			engine.eval("puts 'Hello'");
			engine.put("config", config);
			InputStream script_stream = SimpleTest.class.getClassLoader().getResourceAsStream("config.rb");
			Reader script_reader = new InputStreamReader(script_stream, Charset.forName("utf-8"));
			Bindings b = engine.createBindings();
			b.put("config", config);
			engine.eval(script_reader, b);
			//config = (MCUConfiguration) engine.get("config2");
		} catch (ScriptException exception) {
			exception.printStackTrace();
		}
		
		//Serializer serializer = new Persister(new CycleStrategy());
		//serializer.write(config, System.out);
		
		/*STGroupFile tgroup = new STGroupFile(
				SimpleTest.class.getClassLoader().getResource("de/upbracing/code_generation/eeprom.stg"),
				"utf-8", '<', '>');
		ST template = tgroup.getInstanceOf("main");
		template.add("config", config);
		System.out.println(template.render());*/
		
		System.out.println(new EepromTemplate().generate(config));
	}

}
