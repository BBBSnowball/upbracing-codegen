package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.GlobalVariableTemplate;

public class GlobalVariableGenerator extends AbstractGenerator {
	public GlobalVariableGenerator() {
		super("global_variables.h", new GlobalVariableTemplate());
	}
}
