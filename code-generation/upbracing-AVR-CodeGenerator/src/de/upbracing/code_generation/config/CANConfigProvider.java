package de.upbracing.code_generation.config;

import de.upbracing.code_generation.Messages;
import de.upbracing.dbc.DBC;

public class CANConfigProvider implements IConfigProvider {
	public static final ConfigState<DBCConfig> STATE_W = new ConfigState<DBCConfig>("can");
	public static final ReadableConfigState<DBCConfig> STATE = STATE_W.readonly();
	
	@Override
	public void extendConfiguration(RichConfigurationExtender ext) {
		ext.addState(STATE_W, DBCConfig.class);
		ext.addProperty("can", STATE_W);
		
		ext.addMethods(CANConfigProvider.class);
	}

	@Override
	public void initConfiguration(CodeGeneratorConfigurations config) {
	}

	@Override
	public void addFormatters(Messages messages) {
	}

	public static DBCConfig setCan(CodeGeneratorConfigurations config) {
		return config.getState(STATE);
	}

	@ConfigurationMethod
	/** set the value of the 'can' property; converting to a DBCConfig, if necessary */
	public static void setCan(CodeGeneratorConfigurations config, DBC dbc) {
		DBCConfig dbcconfig;
		if (dbc instanceof DBCConfig)
			dbcconfig = (DBCConfig) dbc;
		else
			dbcconfig = new DBCConfig(dbc, config);
		
		config.setState(STATE_W, dbcconfig);
	}
}
