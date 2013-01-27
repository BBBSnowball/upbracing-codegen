package de.upbracing.code_generation.test;

import java.util.Collections;

import javax.script.ScriptException;

import org.junit.Test;

import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.generators.PinNameGenerator;

public class TestPinsFromEagle {
	@Test
	public void test() throws ScriptException {
		String schematic_file = TestHelpers.getResourceURLForJRuby("files/CockpitPX212-L5970D-V2.sch");
		
		CodeGeneratorConfigurations config = TestHelpers.loadConfigFromRessource(
				"files/eagle_pins_config.rb",
				Collections.<String,Object>singletonMap("schematic_file", schematic_file));
		
		GeneratorTester gen = new GeneratorTester(new PinNameGenerator(), config);
		gen.testTemplates("expected_results/pins_eagle");
	}
}
