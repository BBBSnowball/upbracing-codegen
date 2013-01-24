package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.upbracing.code_generation.Messages;
import de.upbracing.eculist.ECUDefinition;
import de.upbracing.eculist.EEPROMValue;

public class ECUListProvider implements IConfigProvider {
	
	private static final ConfigState<List<ECUDefinition>> STATE_ECUS_W
			= new ConfigState<List<ECUDefinition>>("ecus");
	public static final ReadableConfigState<List<ECUDefinition>> STATE_ECUS
			= STATE_ECUS_W.readonly();

	private static final ConfigState<ECUDefinition> STATE_CURRENT_ECU_W
			= new ConfigState<ECUDefinition>("current_ecu");
	public static final ReadableConfigState<ECUDefinition> STATE_CURRENT_ECU
			= STATE_CURRENT_ECU_W.readonly();

	@Override
	@SuppressWarnings("unchecked")
	public void extendConfiguration(RichConfigurationExtender ext) {
		ext.<List<ECUDefinition>>addState(STATE_ECUS_W, (Class<List<ECUDefinition>>)(Object)List.class);
		ext.addReadonlyProperty("ecus", STATE_ECUS_W);
		
		ext.addState(STATE_CURRENT_ECU_W, ECUDefinition.class);
		ext.addReadonlyProperty("currentEcu", STATE_CURRENT_ECU_W);
		
		ext.addMethods(ECUListProvider.class);
	}

	@Override
	public void initConfiguration(CodeGeneratorConfigurations config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFormatters(Messages messages) {
		// TODO Auto-generated method stub

	}

	/**
	 * Set the list of electronic control units (ECUs)
	 * 
	 * Normally, this list is read from ecu-list.xml
	 * 
	 * @param ecus the list
	 */
	@ConfigurationMethod
	public static void setEcus(CodeGeneratorConfigurations config,
			Collection<ECUDefinition> ecus) {
		config.setState(STATE_ECUS_W, new ArrayList<ECUDefinition>(ecus));
	}

	/**
	 * Select the ECU (electronic control unit) to generate code for
	 * 
	 * The ECU must be in the list of ECUs.
	 * The EEPROM value definitions are automatically copied to the EEPROM configuration.
	 * 
	 * @param ecu the ECU definition
	 * @see CodeGeneratorConfigurations#getEcus()
	 * @see CodeGeneratorConfigurations#getEeprom()
	 */
	public static void selectEcu(CodeGeneratorConfigurations config, ECUDefinition ecu) {
		List<ECUDefinition> ecus = config.getState(STATE_ECUS);
		if (ecus == null || !ecus.contains(ecu))
			throw new IllegalArgumentException("The current ECU must be in the ECU list.");
		
		config.setState(STATE_CURRENT_ECU_W, ecu);
		
		for (EEPROMValue v : ecu.getEepromValues()) {
			//getEeprom().add(v.getName(), v.getType(), v.getDefault());
			throw new RuntimeException("TODO");
		}
	}

	/**
	 * Select the ECU (electronic control unit) to generate code for
	 * 
	 * The ECU must be in the list of ECUs.
	 * The EEPROM value definitions are automatically copied to the EEPROM configuration.
	 * 
	 * @param name the ECU name
	 * @see CodeGeneratorConfigurations#getEcus()
	 * @see CodeGeneratorConfigurations#getEeprom()
	 */
	public static void selectEcu(CodeGeneratorConfigurations config, String name) {
		List<ECUDefinition> ecus = config.getState(STATE_ECUS);
		for (ECUDefinition ecu : ecus) {
			if (ecu.getName().equals(name)) {
				selectEcu(config, ecu);
				return;
			}
		}
		throw new IllegalArgumentException("No such ECU could be found.");
	}
	
	// Calls one of the other selectEcu methods depending on the type of the second argument
	@ConfigurationMethod
	public static void selectEcu(CodeGeneratorConfigurations config, Object name_or_ecu) {
		if (name_or_ecu instanceof String)
			selectEcu(config, (String)name_or_ecu);
		else if (name_or_ecu instanceof ECUDefinition)
			selectEcu(config, (ECUDefinition)name_or_ecu);
		else
			throw new IllegalArgumentException("second argument must be String or ECUDefinition");
	}
}
