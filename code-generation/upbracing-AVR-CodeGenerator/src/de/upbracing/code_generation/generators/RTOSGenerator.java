package de.upbracing.code_generation.generators;

import java.util.Arrays;
import java.util.regex.Pattern;

import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.RTOSApplicationCFileTemplate;
import de.upbracing.code_generation.RTOSApplicationHeaderTemplate;
import de.upbracing.code_generation.RTOSFeaturesTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.config.RTOSAlarm;
import de.upbracing.code_generation.config.RTOSConfig;
import de.upbracing.code_generation.config.RTOSTask;

public class RTOSGenerator extends AbstractGenerator {
	public RTOSGenerator() {
		super("Os_cfg_features.h", new RTOSFeaturesTemplate(),
				"Os_cfg_application.h", new RTOSApplicationHeaderTemplate(),
				"Os_cfg_application.c", new RTOSApplicationCFileTemplate());
	}
	
	@Override
	public boolean isTemplateActive(String filename, ITemplate template,
			MCUConfiguration config) {
		return config.getRtos().isUsed();
	}
	
	@Override
	public boolean validate(MCUConfiguration config, boolean after_update_config, Object generator_data) {
		boolean valid = true;
		RTOSConfig rtos = config.getRtos();
		
		if (!rtos.isUsed())
			return true;
		
		if (!rtos.isTickFrequencyValid()) {
			System.err.println("ERROR: RTOS doesn't have a valid tick frequency. Use sensible values for clock and tick_frequency and make sure the processor is supported.");
			valid = false;
		} else if (Math.abs(rtos.getRealTickFrequency() - rtos.getTickFrequency()) > 0.1*rtos.getTickFrequency()) {
			System.err.println("WARN: System timer tick has an error of more than 10%: " +
					String.format("%0.0f instead of %0.0f, %0.2f%% off",
							rtos.getRealTickFrequency(),
							rtos.getTickFrequency(),
							(rtos.getRealTickFrequency() - rtos.getTickFrequency())/rtos.getTickFrequency()*100));
		}
		
		if (!Arrays.asList("BCC1", "BCC2", "ECC1", "ECC2").contains(rtos.getConformanceClass())) {
			System.err.println("ERROR: Invalid OSEK conformance class '" + rtos.getConformanceClass() + "'");
			valid = false;
		}
		
		if (rtos.getTasks().isEmpty()) {
			System.err.println("ERROR: There must be at least one task");
			valid = false;
		} else {
			RTOSTask idle_task = rtos.getTasks().get(0);
			
			for (RTOSTask task : rtos.getTasks()) {
				RTOSAlarm alarm = task.getAlarm();
				if (alarm == null) {
					for (RTOSAlarm alarm2 : rtos.getAlarms()) {
						if (alarm2.getTask() == task) {
							alarm = alarm2;
							break;
						}
					}
				} else if (!rtos.getAlarms().contains(alarm)) {
					System.err.println("ERROR: Task " + task.getName() + " references an alarm which is not in the list of alarms");
					valid = false;
				}
	
				if (task == idle_task) {
					if (alarm != null)
						System.err.println("WARN: The first task " + task.getName() + " is the idle task, so it shouldn't have an alarm");
				} else {
					if (alarm == null)
						System.err.println("WARN: Task " + task.getName() + " doesn't have an alarm");
				}
				
				if (!Pattern.matches("^[a-zA-Z_][a-zA-Z_0-9]*$", task.getName())) {
					System.err.println("ERROR: Task '" + task.getName() + "' has a name that is not a valid identifier");
					valid = false;
				}
				
				if (task.getStackSize() < 35) {
					System.err.println("ERROR: Task " + task.getName() + " has a too small stack size (smaller than 35 bytes)");
					valid = false;
				}
			}
		}

		for (RTOSAlarm alarm : rtos.getAlarms()) {
			RTOSTask task = alarm.getTask();
			if (task == null) {
				System.err.println("ERROR: An alarm (comment: " + alarm.getComment() + ") needs a task.");
				valid = false;
			} else 	if (!rtos.getTasks().contains(task)) {
				System.err.println("ERROR: An alarm (comment: " + alarm.getComment() + ") references the task " + task.getName() + " which is not in the list of tasks");
				valid = false;
			}
			
			if (alarm.getTicksPerBase() <= 0) {
				System.err.println("ERROR: An alarm (comment: " + alarm.getComment() + ") has a non-positive ticks_per_base value");
				valid = false;
			}
		}
		
		return valid;
	}
	
	@Override
	public Object updateConfig(MCUConfiguration config) {
		config.getRtos().updateTaskIDs();
		
		return super.updateConfig(config);
	}
}
