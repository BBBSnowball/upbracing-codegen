package de.upbracing.code_generation.config;

import de.upbracing.code_generation.Messages;

public class StatemachinesConfigProvider implements IConfigProvider {
	private static final ConfigState<StatemachinesConfig> STATE
			= new ConfigState<StatemachinesConfig>("statemachines");

	@Override
	public void extendConfiguration(RichConfigurationExtender ext) {
		ext.addState(STATE, StatemachinesConfig.class);
		ext.addReadonlyProperty("statemachines", STATE);
	}

	@Override
	public void initConfiguration(CodeGeneratorConfigurations config) {
		StatemachinesConfig statemachines = new StatemachinesConfig();
		statemachines.addFormatters(config.getMessages());
		
		config.setState(STATE, statemachines);
	}

	@Override
	public void addFormatters(Messages messages) {
	}
	
	public static StatemachinesConfig get(CodeGeneratorConfigurations config) {
		return config.getState(STATE);
	}
}
