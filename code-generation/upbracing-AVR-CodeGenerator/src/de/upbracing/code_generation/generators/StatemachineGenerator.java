package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.StatemachinesHeaderTemplate;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.generators.fsm.StatemachinesCFileTemplate;
import de.upbracing.code_generation.generators.fsm.Updater;
import de.upbracing.code_generation.generators.fsm.Validator;

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

	@Override
	public boolean validate(CodeGeneratorConfigurations config,
			boolean after_update_config, Object generator_data) {
		Validator v = new Validator(config, after_update_config, generator_data);
		return v.validate();
	}

	@Override
	public Object updateConfig(CodeGeneratorConfigurations config) {
		return new Updater(config).updateConfig(config);
	}
}
