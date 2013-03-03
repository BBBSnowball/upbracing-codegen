package de.upbracing.code_generation.test;

import java.util.Collections;

import javax.script.ScriptException;

import org.eclipse.emf.common.util.URI;
import org.junit.Test;

import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.StatemachinesConfigProvider;
import de.upbracing.code_generation.generators.CanGenerator;
import de.upbracing.code_generation.generators.StatemachineGenerator;

public class TestStatemachineGenerator {
	// a very simple statemachine; can also be run on an AVR
	@Test
	public void testCounterStatemachine() {
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		
		StatemachinesConfigProvider.get(config).load("counter",
				URI.createURI(TestHelpers.getResourceURL("files/counter.statemachine").toString()));

		
		GeneratorTester gen = new GeneratorTester(new StatemachineGenerator(), config);
		gen.testTemplates("expected_results/statemachines/testCounterStatemachine");
	}
	
	// a statemachine with superstates
	// Not meant to be executable. It is only for testing.
	@Test
	public void testSimplePCStatemachine() {
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		
		StatemachinesConfigProvider.get(config).load("simple_pc",
				URI.createURI(TestHelpers.getResourceURL("files/simple-pc.statemachine").toString()));

		
		GeneratorTester gen = new GeneratorTester(new StatemachineGenerator(), config);
		gen.testTemplates("expected_results/statemachines/testSimplePCStatemachine");
	}
	
	// two statemachines in one file (counter and simple-pc)
	@Test
	public void testTwoStatemachines() {
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		
		StatemachinesConfigProvider.get(config).load("counter",
				URI.createURI(TestHelpers.getResourceURL("files/counter.statemachine").toString()));

		StatemachinesConfigProvider.get(config).load("simple_pc",
				URI.createURI(TestHelpers.getResourceURL("files/simple-pc.statemachine").toString()));

		
		GeneratorTester gen = new GeneratorTester(new StatemachineGenerator(), config);
		gen.testTemplates("expected_results/statemachines/testTwoStatemachines");
	}
	
	@Test
	public void testStatemachineDSL() throws ScriptException {
		testStatemachineWithConfig("files/statemachine1.rb", "testCounterStatemachine");
		testStatemachineWithConfig("files/statemachine2.rb", "testCounterStatemachine");
	}

	private void testStatemachineWithConfig(String configfile, String resultname) throws ScriptException {
		CodeGeneratorConfigurations config = TestHelpers.loadConfigFromRessource(
				configfile,
				Collections.<String,Object>emptyMap());

		GeneratorTester gen = new GeneratorTester(new StatemachineGenerator(), config);
		gen.testTemplates("expected_results/statemachines/" + resultname);
	}
}
