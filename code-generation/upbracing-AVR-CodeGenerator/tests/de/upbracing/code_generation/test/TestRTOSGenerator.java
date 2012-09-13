package de.upbracing.code_generation.test;

import static de.upbracing.code_generation.test.TestHelpers.loadResource;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.upbracing.code_generation.IGenerator;
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
		
		
		IGenerator gen = new RTOSGenerator();
		assertEquals(true, gen.validate(config, false));
		gen.updateConfig(config);
		assertEquals(true, gen.validate(config, true));
		

		String expected, result;
		expected = loadResource("expected_results/rtos/Os_cfg_application.c");
		result = new RTOSApplicationCFileTemplate().generate(config);
		assertEquals(expected, result);

		expected = loadResource("expected_results/rtos/Os_cfg_application.h");
		result = new RTOSApplicationHeaderTemplate().generate(config);
		assertEquals(expected, result);

		expected = loadResource("expected_results/rtos/Os_cfg_features.h");
		result = new RTOSFeaturesTemplate().generate(config);
		assertEquals(expected, result);
	}
}
