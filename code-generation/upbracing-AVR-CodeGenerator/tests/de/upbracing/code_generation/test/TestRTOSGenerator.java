package de.upbracing.code_generation.test;

import org.junit.Test;

import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.rtos.RTOSTask;
import de.upbracing.code_generation.config.rtos.RTOSTask.TaskState;
import de.upbracing.code_generation.generators.RTOSGenerator;

public class TestRTOSGenerator {
	@Test
	public void testGenerate() {
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		
		config.getRtos().setClock(8000000);
		config.getRtos().setTickFrequency(5);
//		config.getRtos().setConformanceClass("BCC1");

		@SuppressWarnings("unused")
		RTOSTask update_task = config.getRtos().addTask("Update", TaskState.SUSPENDED, 1);
		config.getRtos().addTask("Increment", TaskState.SUSPENDED, 5);
		config.getRtos().addTask("Shift", TaskState.SUSPENDED, 1);
		
		
		GeneratorTester gen = new GeneratorTester(new RTOSGenerator(), config);
		gen.testTemplates("expected_results/rtos");
	}
}
