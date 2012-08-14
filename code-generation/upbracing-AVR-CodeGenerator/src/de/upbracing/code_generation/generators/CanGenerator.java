package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.CanTemplate;

/**
 * Generator for the CAN Communication
 * 
 * @author sven
 */
public class CanGenerator extends AbstractGenerator {
	public CanGenerator() {
		super("can.h", new CanTemplate());
	}
}
