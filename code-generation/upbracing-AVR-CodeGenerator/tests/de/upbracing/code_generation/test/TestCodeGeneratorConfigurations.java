package de.upbracing.code_generation.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;

import de.upbracing.code_generation.Messages.Message;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.TestConfigExtProvider;

public class TestCodeGeneratorConfigurations {
	//NOTE This test assumes that TestConfigExtProvider is enabled
	//     (listed in META-INF/services/de.upbracing.code_generation.config.IConfigProvider)
	
	@Test
	public void testJava() throws IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		config.setProperty("test1", "blub");
		assertEquals("blub", config.getProperty("test1"));

		assertEquals(42, config.getProperty("test2"));
		
		assertEquals(42, config.call("doSomething", "x"));
		assertLastMessageEquals("blub: x", config);
		
		config.call("setSomethingElse", 7);	// sets test2
		assertEquals(7, config.getProperty("test2"));
	}

	private void assertLastMessageEquals(String expected, CodeGeneratorConfigurations config) {
		List<Message> messages = config.getMessages().getMessages();
		assertEquals(expected, messages.get(messages.size()-1).getMessage());
	}
	
	@Test
	public void testJRuby() throws ScriptException {
		// use JSR 223 API to invoke JRuby (like CodeGenerationMain#loadConfig)
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("jruby");
		if (engine == null)
			throw new RuntimeException("Couldn't find the JRuby engine!");

		// create a configuration object and put it into the script engine
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		engine.put("config", config);
		
		engine.eval("require 'config-helpers.rb'");
		
		engine.put(ScriptEngine.FILENAME, "TestCodeGeneratorConfigurations#testJRuby");
		
		
		engine.eval("$config.test1 = 'blub1'");
		assertEquals("blub1", config.getProperty("test1"));
		
		engine.eval("$config.setTest1('blub2')");
		assertEquals("blub2", config.getProperty("test1"));
		
		engine.eval("$config.setTest1('blub2')");
		assertEquals("blub2", config.getProperty("test1"));


		config.setProperty("test1", "blub3");
		assertEquals("blub3", engine.eval("$config.getTest1()"));
		//NOTE The return value should be an Integer, but somehow it
		//     gets converted to Long - weird, but not a problem.
		assertEquals(42L, engine.eval("$config.getTest2()"));
		assertEquals(42L, engine.eval("$config.test2"));
		

		engine.eval("$config.setTestTypeName('abc1')");
		assertEquals("abc1", config.getProperty("testTypeName"));

		engine.eval("$config.testTypeName = 'abc2'");
		assertEquals("abc2", config.getProperty("testTypeName"));

		engine.eval("$config.test_type_name = 'abc3'");
		assertEquals("abc3", config.getProperty("testTypeName"));
		
		config.setProperty("testTypeName", "abc4");
		assertEquals("abc4", engine.eval("$config.getTestTypeName()"));
		
		config.setProperty("testTypeName", "abc5");
		assertEquals("abc5", engine.eval("$config.testTypeName"));
		
		config.setProperty("testTypeName", "abc6");
		assertEquals("abc6", engine.eval("$config.test_type_name"));
		

		config.setProperty("test1", "abc");
		//NOTE The return value should be an Integer, but somehow it
		//     gets converted to Long - weird, but not a problem.
		assertEquals(42L, engine.eval("$config.doSomething('xyz1')"));
		assertLastMessageEquals("abc: xyz1", config);

		assertEquals(42L, engine.eval("$config.do_something 'xyz2'"));
		assertLastMessageEquals("abc: xyz2", config);
		

		engine.eval("$config.something_else = 23");	// sets test2
		assertEquals(23, config.getProperty("test2"));
		
		
		TestConfigExtProvider.setSomethingElse(config, 3);	// set test2
		engine.put("some_numbers", new int[] { 7, -1 });
		assertEquals("3, 7, -1", engine.eval("$config.doSomethingVariadic($some_numbers)"));
		assertEquals("3, 9, 22", engine.eval("$config.do_something_variadic([9,22])"));
		assertEquals("3, 0, 34", engine.eval("$config.doSomethingVariadic(0, 34)"));
		assertEquals("3, 42", engine.eval("$config.doSomethingVariadic(42)"));

		TestConfigExtProvider.setSomethingElse(config, 3);	// set test2
		engine.put("some_numbers", new int[] { 7, -1 });
		assertEquals("abc6 - 3, 7, -1", engine.eval("$config.doSomeMoreVariadicStuff('abc6', $some_numbers)"));
		assertEquals("abc7 - 3, 9, 22", engine.eval("$config.doSomeMoreVariadicStuff('abc7', [9,22])"));
		assertEquals("abc8 - 3, 0, 34", engine.eval("$config.doSomeMoreVariadicStuff('abc8', 0, 34)"));
		assertEquals("abc9 - 3, 42", engine.eval("$config.do_some_more_variadic_stuff('abc9', 42)"));
	}
}
