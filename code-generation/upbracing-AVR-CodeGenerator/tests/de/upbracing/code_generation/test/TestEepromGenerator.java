package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upbracing.code_generation.EepromTemplate;
import de.upbracing.code_generation.config.EEPROMVariable;
import de.upbracing.code_generation.config.MCUConfiguration;

import static de.upbracing.code_generation.test.TestHelpers.*;

public class TestEepromGenerator {

	@Test
	public void testGenerate() {
		MCUConfiguration config = new MCUConfiguration();
		config.getEeprom().add(new EEPROMVariable("wdt_reset_count", "uint8_t", 17));
		config.getEeprom().add(new EEPROMVariable("foo", "s16", "0x4242"));
		config.getEeprom().add("bar", "unsigned char");
		config.getEeprom().add("foobar", "signed long", -2);
		config.getEeprom().add(1, new EEPROMVariable("xyz", "float", 1.75));
		config.getEeprom().add("abc", "struct PointD", 16);
		config.getEeprom().add("def", "struct PointD", 16, "{1,2}");
		
		String expected = loadResource("TestEepromGenerator.testGenerate.result1.txt");
		String result = new EepromTemplate().generate(config);
		assertEquals(expected, result);
	}

}
