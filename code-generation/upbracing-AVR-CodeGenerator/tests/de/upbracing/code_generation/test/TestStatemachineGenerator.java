package de.upbracing.code_generation.test;

import static de.upbracing.code_generation.test.TestHelpers.loadRessource;
import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.junit.Test;

import de.upbracing.code_generation.IGenerator;
import de.upbracing.code_generation.StatemachinesHeaderTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.generators.StatemachineGenerator;
import de.upbracing.code_generation.generators.StatemachinesCFileTemplate;

public class TestStatemachineGenerator {
	@Test
	public void testGenerate() {
		MCUConfiguration config = new MCUConfiguration();
		
		config.getStatemachines().load("counter",
				URI.createURI(TestHelpers.getRessourceURL("files/counter.statecharts").toString()));
		
		IGenerator gen = new StatemachineGenerator();
		assertEquals(true, gen.validate(config, false));
		gen.updateConfig(config);
		assertEquals(true, gen.validate(config, true));
		

		String expected, result;
		expected = loadRessource("expected_results/statemachines/statemachines.h");
		result = new StatemachinesHeaderTemplate().generate(config);
		assertEquals(expected, result);

		expected = loadRessource("expected_results/statemachines/statemachines.c");
		result = new StatemachinesCFileTemplate().generate(config);
		assertEquals(expected, result);
	}
}
