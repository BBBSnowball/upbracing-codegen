package de.upbracing.code_generation.config;

import de.upbracing.code_generation.Messages;
import de.upbracing.eculist.ECUDefinition;
import de.upbracing.eculist.EEPROMValue;

public class EEPROMConfigProvider implements IConfigProvider, StateChangeListener<ECUDefinition> {
	private static final ConfigState<EEPROMConfig> STATE = new ConfigState<EEPROMConfig>("eeprom");

	@Override
	public void extendConfiguration(RichConfigurationExtender ext) {
		ext.addState(STATE, EEPROMConfig.class);
		ext.addReadonlyProperty("eeprom", STATE);
		
		// make sure the ECU list is initialized
		new ECUListProvider().extendConfiguration(ext);
		
		// react to selectEcu calls
		ext.addStateChangeListener(ECUListProvider.STATE_CURRENT_ECU, this);
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

	@Override
	public void stateChanged(CodeGeneratorConfigurations config,
			ConfigState<ECUDefinition> state, ECUDefinition old_value,
			ECUDefinition new_value) {
		ECUDefinition ecu = new_value;
		EEPROMConfig eeprom = get(config);
		
		for (EEPROMValue v : ecu.getEepromValues()) {
			eeprom.add(v.getName(), v.getType(), v.getDefault());
		}
	}
}
