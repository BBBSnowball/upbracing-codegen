package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upbracing.code_generation.GlobalVariableTemplate;
import de.upbracing.code_generation.config.GlobalVariable;
import de.upbracing.code_generation.config.MCUConfiguration;

import static de.upbracing.code_generation.test.TestHelpers.*;

public class TestGlobalVariableGenerator {

	@Test
	public void testGenerate() {
		MCUConfiguration config = new MCUConfiguration();
		config.getGlobalVariables().add(new GlobalVariable("h", "uint8_t", 17));
		config.getGlobalVariables().add(new GlobalVariable("foo", "s16", "0x4242"));
		config.getGlobalVariables().add("bar", "unsigned char");
		config.getGlobalVariables().add("foobar", "signed long", -2);
		config.getGlobalVariables().add(1, new GlobalVariable("xyz", "float", 1.75));
		config.getGlobalVariables().add("abc", "struct PointD", 16);
		config.getGlobalVariables().add("def", "struct PointD", 16, "{1,2}");
		
		String expected = loadResource("TestGlobalVariableGenerator.testGenerate.result1.txt");
		String result = new GlobalVariableTemplate().generate(config);
		assertEquals(expected, result);
	}

}
