package de.upbracing.code_generation.test;

import org.junit.Test;

import de.upbracing.code_generation.CommonHeaderTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.generators.CommonHeaderGenerator;

public class TestCommonHeaderGenerator {
	@Test
	public void testGenerate() {
		MCUConfiguration config = new MCUConfiguration();

		GeneratorTester gen = new GeneratorTester(new CommonHeaderGenerator(), config);
		
		gen.testTemplate(new CommonHeaderTemplate(),
				"expected_results/common.h");
	}
}
