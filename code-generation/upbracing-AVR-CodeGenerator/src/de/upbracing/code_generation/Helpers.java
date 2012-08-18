package de.upbracing.code_generation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.upbracing.code_generation.config.MCUConfiguration;

public class Helpers {
	
	
	/**
	 * load a JRuby configuration file
	 * 
	 * @param stream InputStream for the configuration file. It should be encoded with utf-8.
	 * @return the configuration object
	 * @throws ScriptException if the config script contains errors or raises an exception
	 */
	public static MCUConfiguration loadConfig(InputStream stream, String directory) throws ScriptException {
		// use JSR 223 API to invoke JRuby
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("jruby");
		if (engine == null)
			throw new RuntimeException("Couldn't find the JRuby engine!");
		
		//TODO set class path for JRuby, so the config.rb script can 'require' other files
		// System.setProperty("org.jruby.embed.class.path", ...);

		// create a configuration object and put it into the script engine
		MCUConfiguration config = new MCUConfiguration();
		engine.put("config", config);
		
		engine.eval("require 'config-helpers.rb'");
		
		// go to directory of the script
		String old_pwd = null;
		if (directory != null) {
			old_pwd = engine.eval("Dir.pwd").toString();
			engine.put("directory", directory);
			engine.eval("Dir.chdir($directory)");
		}
		
		// execute the script
		Reader script_reader = new InputStreamReader(stream, Charset.forName("utf-8"));
		engine.eval(script_reader);
		try {
			script_reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// restore working directory
		if (old_pwd != null) {
			engine.put("directory", directory);
			engine.eval("Dir.chdir($directory)");
		}
		
		// return the configuration object
		config = (MCUConfiguration) engine.get("config");
		return config;
	}
	
	/**
	 * load a JRuby configuration file
	 * 
	 * @param file File name of the configuration file. It should be encoded with utf-8.
	 * @return the configuration object
	 * @throws ScriptException if the config script contains errors or raises an exception
	 * @throws FileNotFoundException if the config file cannot be opened
	 */
	public static MCUConfiguration loadConfig(String file) throws FileNotFoundException, ScriptException {
		return loadConfig(
				new FileInputStream(file),
				new File(file).getParentFile().getAbsolutePath());
	}
}
