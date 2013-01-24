package de.upbracing.code_generation.config.rtos;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.ConfigState;
import de.upbracing.code_generation.config.IConfigProvider;
import de.upbracing.code_generation.config.RichConfigurationExtender;

public class RTOSConfigProvider implements IConfigProvider {
	private static final ConfigState<RTOSConfig> STATE
			= new ConfigState<RTOSConfig>("rtos");

	@Override
	public void extendConfiguration(RichConfigurationExtender ext) {
		ext.addState(STATE, RTOSConfig.class);
		ext.addReadonlyProperty("rtos", STATE);
	}

	@Override
	public void initConfiguration(CodeGeneratorConfigurations config) {
		config.setState(STATE, new RTOSConfig());
	}

	@Override
	public void addFormatters(Messages messages) {
	}

	public static RTOSConfig get(CodeGeneratorConfigurations config) {
		return config.getState(STATE);
	}
}
