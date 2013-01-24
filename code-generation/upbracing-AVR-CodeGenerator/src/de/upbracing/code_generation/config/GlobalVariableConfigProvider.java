package de.upbracing.code_generation.config;

import de.upbracing.code_generation.Messages;

public class GlobalVariableConfigProvider implements IConfigProvider {
	private static final ConfigState<GlobalVariableConfig> STATE
			= new ConfigState<GlobalVariableConfig>("globalVariables");

	@Override
	public void extendConfiguration(RichConfigurationExtender ext) {
		ext.addState(STATE, GlobalVariableConfig.class);
		ext.addReadonlyProperty("globalVariables", STATE);
	}

	@Override
	public void initConfiguration(CodeGeneratorConfigurations config) {
		config.setState(STATE, new GlobalVariableConfig());
	}

	@Override
	public void addFormatters(Messages messages) {
	}
	
	public static GlobalVariableConfig get(CodeGeneratorConfigurations config) {
		return config.getState(STATE);
	}
}
