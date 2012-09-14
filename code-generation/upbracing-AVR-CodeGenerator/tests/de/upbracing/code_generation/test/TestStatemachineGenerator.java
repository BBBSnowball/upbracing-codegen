package de.upbracing.code_generation.test;

import org.eclipse.emf.common.util.URI;
import org.junit.Test;

import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.generators.StatemachineGenerator;

public class TestStatemachineGenerator {
	@Test
	public void testGenerate() {
		MCUConfiguration config = new MCUConfiguration();
		
		config.getStatemachines().load("counter",
				URI.createURI(TestHelpers.getResourceURL("files/counter.statecharts").toString()));

		
		GeneratorTester gen = new GeneratorTester(new StatemachineGenerator(), config);
		gen.testTemplates("expected_results/statemachines");
	}
}
