package de.upbracing.code_generation.config;

import de.upbracing.code_generation.Messages;

public class EEPROMConfigProvider implements IConfigProvider {
	private static final ConfigState<EEPROMConfig> STATE = new ConfigState<EEPROMConfig>("eeprom");

	@Override
	public void extendConfiguration(RichConfigurationExtender ext) {
		ext.addState(STATE, EEPROMConfig.class);
		ext.addReadonlyProperty("eeprom", STATE);
	}

	@Override
	public void initConfiguration(CodeGeneratorConfigurations config) {
		config.setState(STATE, new EEPROMConfig());
	}

	@Override
	public void addFormatters(Messages messages) {
	}
	
	public static EEPROMConfig get(CodeGeneratorConfigurations config) {
		return config.getState(STATE);
	}
}
