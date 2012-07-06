package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.GlobalVariableTemplate;

/**
 * Generator for global variable accessors
 * 
 * @author benny
 */
public class GlobalVariableGenerator extends AbstractGenerator {
	public GlobalVariableGenerator() {
		super("global_variables.h", new GlobalVariableTemplate());
	}
}
