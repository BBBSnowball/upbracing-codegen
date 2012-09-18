package de.upbracing.code_generation.test;

import org.eclipse.emf.common.util.URI;
import org.junit.Test;

import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.generators.StatemachineGenerator;

public class TestStatemachineGenerator {
	// a very simple statemachine; can also be run on an AVR
	@Test
	public void testCounterStatemachine() {
		MCUConfiguration config = new MCUConfiguration();
		
		config.getStatemachines().load("counter",
				URI.createURI(TestHelpers.getResourceURL("files/counter.statecharts").toString()));

		
		GeneratorTester gen = new GeneratorTester(new StatemachineGenerator(), config);
		gen.testTemplates("expected_results/statemachines/testCounterStatemachine");
	}
	
	// a statemachine with superstates
	// Not meant to be executable. It is only for testing.
	@Test
	public void testSimplePCStatemachine() {
		MCUConfiguration config = new MCUConfiguration();
		
		config.getStatemachines().load("simple_pc",
				URI.createURI(TestHelpers.getResourceURL("files/simple-pc.statecharts").toString()));

		
		GeneratorTester gen = new GeneratorTester(new StatemachineGenerator(), config);
		gen.testTemplates("expected_results/statemachines/testSimplePCStatemachine");
	}
	
	// two statemachines in one file (counter and simple-pc)
	@Test
	public void testTwoStatemachines() {
		MCUConfiguration config = new MCUConfiguration();
		
		config.getStatemachines().load("counter",
				URI.createURI(TestHelpers.getResourceURL("files/counter.statecharts").toString()));

		config.getStatemachines().load("simple_pc",
				URI.createURI(TestHelpers.getResourceURL("files/simple-pc.statecharts").toString()));

		
		GeneratorTester gen = new GeneratorTester(new StatemachineGenerator(), config);
		gen.testTemplates("expected_results/statemachines/testTwoStatemachines");
	}
}
