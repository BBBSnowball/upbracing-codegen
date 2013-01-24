package de.upbracing.code_generation.test;

import org.junit.Test;

import de.upbracing.code_generation.config.GlobalVariable;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.generators.GlobalVariableGenerator;

public class TestGlobalVariableGenerator {

	@Test
	public void testGenerate() {
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		config.getGlobalVariables().add(new GlobalVariable("h", "uint8_t", 17));
		config.getGlobalVariables().add(new GlobalVariable("foo", "s16", "0x4242"));
		config.getGlobalVariables().add("bar", "unsigned char");
		config.getGlobalVariables().add("foobar", "signed long", -2, null);
		config.getGlobalVariables().add(1, new GlobalVariable("xyz", "float", 1.75));
		config.getGlobalVariables().add("abc", "struct PointD", 16, null);
		config.getGlobalVariables().add("def", "struct PointD", 16, "{1,2}");


		GeneratorTester gen = new GeneratorTester(new GlobalVariableGenerator(), config);
		gen.testTemplates("expected_results/global_vars");
	}

}
