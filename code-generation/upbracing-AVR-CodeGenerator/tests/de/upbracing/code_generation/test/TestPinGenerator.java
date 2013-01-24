package de.upbracing.code_generation.test;

import org.junit.Test;

import de.upbracing.code_generation.PinTemplate;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.Pin;
import de.upbracing.code_generation.generators.PinNameGenerator;

public class TestPinGenerator {
	@Test
	public void testGenerate() {
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		config.getPins().add("FOO", "PC0");
		config.getPins().add("BAR", new Pin("PA3"));
		config.getPins().add("ABC", new Pin('A', 2));
		config.getPins().addRange("PB2", "X", "Y", "Z");
		config.getPins().addPort("RPM", 'D');

		GeneratorTester gen = new GeneratorTester(new PinNameGenerator(), config);
		
		gen.testTemplate(new PinTemplate(),
				"TestPinGenerator.testGenerate.result1.txt");
	}
}
