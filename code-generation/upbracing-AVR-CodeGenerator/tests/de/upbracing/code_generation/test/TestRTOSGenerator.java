package de.upbracing.code_generation.test;

import org.junit.Test;

import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.rtos.RTOSTask;
import de.upbracing.code_generation.config.rtos.RTOSTask.TaskState;
import de.upbracing.code_generation.config.rtos.RTOSConfigProvider;
import de.upbracing.code_generation.generators.RTOSGenerator;

public class TestRTOSGenerator {
	@Test
	public void testGenerate() {
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		
		RTOSConfigProvider.get(config).setClock(8000000);
		RTOSConfigProvider.get(config).setTickFrequency(5);
//		RtosConfigProvider.get(config).setConformanceClass("BCC1");

		@SuppressWarnings("unused")
		RTOSTask update_task = RTOSConfigProvider.get(config).addTask("Update", TaskState.SUSPENDED, 1);
		RTOSConfigProvider.get(config).addTask("Increment", TaskState.SUSPENDED, 5);
		RTOSConfigProvider.get(config).addTask("Shift", TaskState.SUSPENDED, 1);
		
		
		GeneratorTester gen = new GeneratorTester(new RTOSGenerator(), config);
		gen.testTemplates("expected_results/rtos");
	}
}
