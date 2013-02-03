package de.upbracing.code_generation.config;

import de.upbracing.code_generation.Messages;

public class PinConfigProvider implements IConfigProvider {
	private static final ConfigState<PinConfig> STATE
			= new ConfigState<PinConfig>("pins");

	@Override
	public void extendConfiguration(RichConfigurationExtender ext) {
		ext.addState(STATE, PinConfig.class);
		ext.addReadonlyProperty("pins", STATE);
	}

	@Override
	public void initConfiguration(CodeGeneratorConfigurations config) {
		config.setState(STATE, new PinConfig());
	}

	@Override
	public void addFormatters(Messages messages) {
	}
	
	public static PinConfig get(CodeGeneratorConfigurations config) {
		return config.getState(STATE);
	}
}
