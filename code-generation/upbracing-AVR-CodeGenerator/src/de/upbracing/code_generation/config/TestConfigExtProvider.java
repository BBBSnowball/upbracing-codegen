package de.upbracing.code_generation.config;

import de.upbracing.code_generation.Messages;

public class TestConfigExtProvider implements IConfigProvider {
	public static final ConfigState<String> STATE_TEST1 = new ConfigState<String>("test1");
	public static final ConfigState<Integer> STATE_TEST2 = new ConfigState<Integer>("test2");
	public static final ConfigState<String> STATE_TEST_NAME = new ConfigState<String>("test_something");
	
	@ConfigurationMethod
	public static int doSomething(CodeGeneratorConfigurations config, String msg) {
		config.getMessages().info(config.getState(STATE_TEST1) + ": " + msg);
		return 42;
	}

	@Override
	public void extendConfiguration(RichConfigurationExtender ext) {
		ext.addState(STATE_TEST1, String.class);
		ext.addState(STATE_TEST2, Integer.class);
		ext.addState(STATE_TEST_NAME, String.class);
		
		ext.addProperty("test1", STATE_TEST1);
		ext.addReadonlyProperty("test2", STATE_TEST2);
		ext.addProperty("testTypeName", STATE_TEST_NAME);
		
		ext.addMethods(TestConfigExtProvider.class);
	}

	@Override
	public void initConfiguration(CodeGeneratorConfigurations config) {
		config.setState(STATE_TEST2, 42);
	}

	@Override
	public void addFormatters(Messages messages) {
	}

}
