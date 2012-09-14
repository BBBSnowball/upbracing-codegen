package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upbracing.code_generation.EepromTemplate;
import de.upbracing.code_generation.GlobalVariableCFileTemplate;
import de.upbracing.code_generation.GlobalVariableHeaderTemplate;
import de.upbracing.code_generation.config.GlobalVariable;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.generators.EEPROMAccessorGenerator;
import de.upbracing.code_generation.generators.GlobalVariableGenerator;

import static de.upbracing.code_generation.test.TestHelpers.*;

public class TestGlobalVariableGenerator {

	@Test
	public void testGenerate() {
		MCUConfiguration config = new MCUConfiguration();
		config.getGlobalVariables().add(new GlobalVariable("h", "uint8_t", 17));
		config.getGlobalVariables().add(new GlobalVariable("foo", "s16", "0x4242"));
		config.getGlobalVariables().add("bar", "unsigned char");
		config.getGlobalVariables().add("foobar", "signed long", -2, null);
		config.getGlobalVariables().add(1, new GlobalVariable("xyz", "float", 1.75));
		config.getGlobalVariables().add("abc", "struct PointD", 16, null);
		config.getGlobalVariables().add("def", "struct PointD", 16, "{1,2}");


		GeneratorTester gen = new GeneratorTester(new GlobalVariableGenerator(), config);
		
		gen.testTemplate(new GlobalVariableHeaderTemplate(),
				"expected_results/global_vars/global_variables.h");
		
		gen.testTemplate(new GlobalVariableCFileTemplate(),
				"expected_results/global_vars/global_variables.c");
	}

}
