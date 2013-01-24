package de.upbracing.code_generation.config;

import java.io.FileNotFoundException;

import de.upbracing.code_generation.Messages;
import de.upbracing.shared.timer.model.ConfigurationModel;

public class TimerConfigProvider implements IConfigProvider {
	private static final ConfigState<ConfigurationModel> STATE
			= new ConfigState<ConfigurationModel>("timerConfig");

	@Override
	public void extendConfiguration(RichConfigurationExtender ext) {
		ext.addState(STATE, ConfigurationModel.class);
		ext.addProperty("timerConfig", STATE);
		
		ext.addMethods(TimerConfigProvider.class);
	}

	@Override
	public void initConfiguration(CodeGeneratorConfigurations config) {
	}

	@Override
	public void addFormatters(Messages messages) {
	}

	public static ConfigurationModel get(CodeGeneratorConfigurations config) {
		return config.getState(STATE);
	}

	/**
	 * Loads the timer configuration for this MCU from disk
	 * 
	 * @param path to configuration file
	 * @throws FileNotFoundException
	 */
	@ConfigurationMethod
	public static void loadTimerConfiguration(CodeGeneratorConfigurations config, String path)
			throws FileNotFoundException {
		ConfigurationModel timer = ConfigurationModel.Load(CWDProvider.makeAbsolute(path));
		if (timer == null) {
			timer = new ConfigurationModel();
			config.getMessages().error("Cannot find configuration file \"%s\". Empty timer configuration is used!", path);
		}
		
		config.setState(STATE, timer);
	}
}
