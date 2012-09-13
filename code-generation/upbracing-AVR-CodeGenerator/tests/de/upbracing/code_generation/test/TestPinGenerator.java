package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upbracing.code_generation.PinTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.config.Pin;

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
		
		String expected = loadResource("TestPinGenerator.testGenerate.result1.txt");
		String result = new PinTemplate().generate(config);
		assertEquals(expected, result);
	}
}
