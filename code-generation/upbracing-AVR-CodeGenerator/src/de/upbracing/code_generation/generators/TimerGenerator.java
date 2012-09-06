package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.TimerHeaderTemplate;
import de.upbracing.code_generation.TimerCFileTemplate;

public class TimerGenerator extends AbstractGenerator {
	public TimerGenerator() {
		super("timer.h", new TimerHeaderTemplate(),
				"timer.c", new TimerCFileTemplate());
	}
}
