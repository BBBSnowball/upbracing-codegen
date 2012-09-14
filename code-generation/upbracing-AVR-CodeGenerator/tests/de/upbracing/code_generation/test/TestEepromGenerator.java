package de.upbracing.code_generation.test;

import org.junit.Test;

import de.upbracing.code_generation.config.EEPROMVariable;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.generators.EEPROMAccessorGenerator;

public class TestEepromGenerator {

	@Test
	public void testGenerate() {
		MCUConfiguration config = new MCUConfiguration();
		config.getEeprom().add(new EEPROMVariable("wdt_reset_count", "uint8_t", 17));
		config.getEeprom().add(new EEPROMVariable("foo", "s16", "0x4242"));
		config.getEeprom().add("bar", "unsigned char");
		config.getEeprom().add("foobar", "signed long", -2, null);
		config.getEeprom().add(1, new EEPROMVariable("xyz", "float", 1.75));
		config.getEeprom().add("abc", "struct PointD", 16, null);
		config.getEeprom().add("def", "struct PointD", 16, "{1,2}");

		GeneratorTester gen = new GeneratorTester(new EEPROMAccessorGenerator(), config);
		gen.testTemplates("expected_results/eeprom");
	}

}
