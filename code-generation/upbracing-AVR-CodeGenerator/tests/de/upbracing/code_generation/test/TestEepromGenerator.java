package de.upbracing.code_generation.test;

import org.junit.Test;

import de.upbracing.code_generation.config.EEPROMConfigProvider;
import de.upbracing.code_generation.config.EEPROMVariable;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.generators.EEPROMAccessorGenerator;

public class TestEepromGenerator {

	@Test
	public void testGenerate() {
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		EEPROMConfigProvider.get(config).add(new EEPROMVariable("wdt_reset_count", "uint8_t", 17));
		EEPROMConfigProvider.get(config).add(new EEPROMVariable("foo", "s16", "0x4242"));
		EEPROMConfigProvider.get(config).add("bar", "unsigned char");
		EEPROMConfigProvider.get(config).add("foobar", "signed long", -2, null);
		EEPROMConfigProvider.get(config).add(1, new EEPROMVariable("xyz", "float", 1.75));
		EEPROMConfigProvider.get(config).add("abc", "struct PointD", 16, null);
		EEPROMConfigProvider.get(config).add("def", "struct PointD", 16, "{1,2}");

		GeneratorTester gen = new GeneratorTester(new EEPROMAccessorGenerator(), config);
		gen.testTemplates("expected_results/eeprom");
	}

}
