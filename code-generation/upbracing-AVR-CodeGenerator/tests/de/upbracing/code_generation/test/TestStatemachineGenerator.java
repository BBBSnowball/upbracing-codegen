package de.upbracing.code_generation.test;

import static de.upbracing.code_generation.test.TestHelpers.loadResource;
import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.junit.Test;

import de.upbracing.code_generation.IGenerator;
import de.upbracing.code_generation.RTOSApplicationCFileTemplate;
import de.upbracing.code_generation.StatemachinesHeaderTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.generators.RTOSGenerator;
import de.upbracing.code_generation.generators.StatemachineGenerator;
import de.upbracing.code_generation.generators.StatemachinesCFileTemplate;

public class TestStatemachineGenerator {
	@Test
	public void testGenerate() {
		MCUConfiguration config = new MCUConfiguration();
		
		config.getStatemachines().load("counter",
				URI.createURI(TestHelpers.getResourceURL("files/counter.statecharts").toString()));

		
		GeneratorTester gen = new GeneratorTester(new StatemachineGenerator(), config);
		
		gen.testTemplate(new StatemachinesHeaderTemplate(),
				"expected_results/statemachines/statemachines.h");
		
		gen.testTemplate(new StatemachinesCFileTemplate(),
				"expected_results/statemachines/statemachines.c");
	}
}
