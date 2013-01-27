package de.upbracing.code_generation.test;

import org.junit.Test;

import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.Pin;
import de.upbracing.code_generation.config.PinConfigProvider;
import de.upbracing.code_generation.generators.PinNameGenerator;

public class TestPinGenerator {
	@Test
	public void testGenerate() {
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		PinConfigProvider.get(config).add("FOO", "PC0");
		PinConfigProvider.get(config).add("BAR", new Pin("PA3"));
		PinConfigProvider.get(config).add("ABC", new Pin('A', 2));
		PinConfigProvider.get(config).addRange("PB2", "X", "Y", "Z");
		PinConfigProvider.get(config).addPort("RPM", 'D');

		GeneratorTester gen = new GeneratorTester(new PinNameGenerator(), config);
		gen.testTemplates("expected_results/pins");
	}
}
