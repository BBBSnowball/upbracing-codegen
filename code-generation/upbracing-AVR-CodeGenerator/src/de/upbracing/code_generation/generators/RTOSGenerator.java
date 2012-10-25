package de.upbracing.code_generation.generators;

import java.util.Arrays;
import java.util.regex.Pattern;

import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.ContextItem;
import de.upbracing.code_generation.Messages.Severity;
import de.upbracing.code_generation.RTOSApplicationCFileTemplate;
import de.upbracing.code_generation.RTOSApplicationHeaderTemplate;
import de.upbracing.code_generation.RTOSFeaturesTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.config.rtos.RTOSAlarm;
import de.upbracing.code_generation.config.rtos.RTOSConfig;
import de.upbracing.code_generation.config.rtos.RTOSTask;

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
		RTOSConfig rtos = config.getRtos();
		
		Messages messages = config.getMessages();
		ContextItem context = messages.pushContext("caRTOS validator");
		
		if (!rtos.isUsed())
			return true;
		
		if (!rtos.isTickFrequencyValid()) {
			messages.error("RTOS doesn't have a valid tick frequency. Use sensible "
					+ "values for clock and tick_frequency and make sure the processor is supported.");
		} else if (Math.abs(rtos.getRealTickFrequency() - rtos.getTickFrequency()) > 0.1*rtos.getTickFrequency()) {
			messages.warn("System timer tick has an error of more than 10%%: %0.0f instead of %0.0f, %0.2f%% off",
							rtos.getRealTickFrequency(),
							rtos.getTickFrequency(),
							(rtos.getRealTickFrequency() - rtos.getTickFrequency())/rtos.getTickFrequency()*100);
		}
		
		if (!Arrays.asList("BCC1", "BCC2", "ECC1", "ECC2").contains(rtos.getConformanceClass())) {
			messages.error("Invalid OSEK conformance class '%s'", rtos.getConformanceClass());
		}
		
		if (rtos.getTasks().isEmpty()) {
			messages.error("There must be at least one task");
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
					messages.error("Task '%s' references an alarm which is not in the list of alarms", task.getName());
				}
	
				if (task == idle_task) {
					if (alarm != null)
						messages.warn("The first task '%' is the idle task, so it shouldn't have an alarm", task.getName());
				} else {
					if (alarm == null)
						messages.warn("Task '%' doesn't have an alarm", task.getName());
				}
				
				if (!Pattern.matches("^[a-zA-Z_][a-zA-Z_0-9]*$", task.getName())) {
					messages.error("ERROR: Task '%s' has a name that is not a valid identifier", task.getName());
				}
				
				if (task.getStackSize() < 35) {
					messages.error("ERROR: Task '%' has a too small stack size (smaller than 35 bytes)", task.getName());
				}
			}
		}

		for (RTOSAlarm alarm : rtos.getAlarms()) {
			RTOSTask task = alarm.getTask();
			if (task == null) {
				messages.error("An alarm (comment: '%s') needs a task.", alarm.getComment());
			} else 	if (!rtos.getTasks().contains(task)) {
				messages.error("An alarm (comment: '%s') references the task '%s' which is not in the list of tasks",
						alarm.getComment(), task.getName());
			}
			
			if (alarm.getTicksPerBase() <= 0) {
				messages.error("An alarm (comment: '%s') has a non-positive ticks_per_base value", alarm.getComment());
			}
		}
		
		boolean valid = messages.getHighestSeverityInContext().ordinal() < Severity.ERROR
				.ordinal();
		
		context.pop();

		return valid;
	}
	
	@Override
	public Object updateConfig(MCUConfiguration config) {
		config.getRtos().updateTaskIDs();
		
		return super.updateConfig(config);
	}
}
