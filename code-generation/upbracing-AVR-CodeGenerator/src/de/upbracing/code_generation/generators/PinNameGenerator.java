package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.PinTemplate;

/**
 * Generator for pin name code
 * 
 * @author benny
 */
public class PinNameGenerator extends AbstractGenerator {
	public PinNameGenerator() {
		super("pins.h", new PinTemplate());
	}
}
