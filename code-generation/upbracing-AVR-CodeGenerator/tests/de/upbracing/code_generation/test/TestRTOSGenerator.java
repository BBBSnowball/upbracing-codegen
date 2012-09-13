package de.upbracing.code_generation.test;

import static de.upbracing.code_generation.test.TestHelpers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import de.upbracing.code_generation.RTOSApplicationCFileTemplate;
import de.upbracing.code_generation.RTOSApplicationHeaderTemplate;
import de.upbracing.code_generation.RTOSFeaturesTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.config.RTOSTask;
import de.upbracing.code_generation.config.RTOSTask.TaskState;
import de.upbracing.code_generation.generators.RTOSGenerator;

public class TestRTOSGenerator {
	@Test
	public void testGenerate() {
		MCUConfiguration config = new MCUConfiguration();
		
		config.getRtos().setClock(8000000);
		config.getRtos().setTickFrequency(5);
		config.getRtos().setConformanceClass("BCC1");

		@SuppressWarnings("unused")
		RTOSTask idle_task = config.getRtos().addTask("Idle", TaskState.READY);
		config.getRtos().addTask("Update", TaskState.SUSPENDED, 1);
		config.getRtos().addTask("Increment", TaskState.SUSPENDED, 5);
		config.getRtos().addTask("Shift", TaskState.SUSPENDED, 1);
		
		
		GeneratorTester gen = new GeneratorTester(new RTOSGenerator(), config);
		

		gen.testTemplate(new RTOSApplicationCFileTemplate(),
				"expected_results/rtos/Os_cfg_application.c");

		gen.testTemplate(new RTOSApplicationHeaderTemplate(),
				"expected_results/rtos/Os_cfg_application.h");

		gen.testTemplate(new RTOSFeaturesTemplate(),
				"expected_results/rtos/Os_cfg_features.h");
	}
}
