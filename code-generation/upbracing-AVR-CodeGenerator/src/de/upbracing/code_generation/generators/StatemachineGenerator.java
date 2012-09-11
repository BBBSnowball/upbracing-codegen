package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.StatemachinesCFileTemplate;
import de.upbracing.code_generation.StatemachinesHeaderTemplate;

/**
 * Generator for statemachine code
 * 
 * @author benny
 *
 */
public class StatemachineGenerator extends AbstractGenerator {
	public StatemachineGenerator() {
		super("statemachines.h", new StatemachinesHeaderTemplate(),
				"statemachines.c", new StatemachinesCFileTemplate());
	}
}
