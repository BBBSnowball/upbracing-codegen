package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upbracing.code_generation.GlobalVariableTemplate;
import de.upbracing.code_generation.PinTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.config.Pin;
import de.upbracing.code_generation.generators.GlobalVariableGenerator;
import de.upbracing.code_generation.generators.PinNameGenerator;

import static de.upbracing.code_generation.test.TestHelpers.*;

public class TestPinGenerator {
	@Test
	public void testGenerate() {
		MCUConfiguration config = new MCUConfiguration();
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
