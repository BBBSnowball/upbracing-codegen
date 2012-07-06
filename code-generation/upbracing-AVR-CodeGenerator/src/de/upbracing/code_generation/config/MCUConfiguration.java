package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.simpleframework.xml.Default;

import de.upbracing.dbc.DBC;
import de.upbracing.eculist.ECUDefinition;
import de.upbracing.eculist.EEPROMValue;

/**
 * main configuration class
 * 
 * You can reach all configuration objects from this class.
 * 
 * @author benny
 */
@Default(required=false)
public class MCUConfiguration {
	private List<ECUDefinition> ecus;
	private ECUDefinition currentEcu;
	//TODO we have to wrap the DBC model to allow configuration of the code generation
	private DBC can;
	private EEPROMConfig eeprom = new EEPROMConfig();
	private PinConfig pins = new PinConfig();
	private GlobalVariableConfig global_variables = new GlobalVariableConfig();

	/**
	 * Get the list of electronic control units (ECUs)
	 * 
	 * Normally, this list is read from ecu-list.xml
	 * 
	 * @return the list
	 */
	public List<ECUDefinition> getEcus() {
		return ecus;
	}

	/**
	 * Set the list of electronic control units (ECUs)
	 * 
	 * Normally, this list is read from ecu-list.xml
	 * 
	 * @param ecus the list
	 */
	public void setEcus(Collection<ECUDefinition> ecus) {
		this.ecus = new ArrayList<ECUDefinition>(ecus);
	}

	/**
	 * Get the current ECU (electronic control unit)
	 * 
	 * This is the ECU we are generating code for.
	 * 
	 * @return the ecu definition object
	 * @see MCUConfiguration#selectEcu(ECUDefinition)
	 */
	public ECUDefinition getCurrentEcu() {
		return currentEcu;
	}

	/**
	 * Get CAN configuration
	 * 
	 * @return the CAN configuration object
	 */
	public DBC getCan() {
		return can;
	}

	/**
	 * Set CAN configuration
	 * 
	 * This is usually read from a DBC file using parse-dbc.rb
	 * 
	 * @param can the CAN config
	 */
	public void setCan(DBC can) {
		this.can = can;
	}
	
	/**
	 * Get EEPROM config
	 * 
	 * @return the config object
	 */
	public EEPROMConfig getEeprom() {
		return eeprom;
	}

	/** Get pin name config
	 * 
	 * @return the config object
	 */
	public PinConfig getPins() {
		return pins;
	}

	/**
	 * Get global variable config
	 * 
	 * @return the config object
	 */
	public GlobalVariableConfig getGlobalVariables() {
		return global_variables;
	}

	/**
	 * Select the ECU (electronic control unit) to generate code for
	 * 
	 * The ECU must be in the list of ECUs.
	 * The EEPROM value definitions are automatically copied to the EEPROM configuration.
	 * 
	 * @param ecu the ECU definition
	 * @see MCUConfiguration#getEcus()
	 * @see MCUConfiguration#getEeprom()
	 */
	public void selectEcu(ECUDefinition ecu) {
		if (ecus == null || !ecus.contains(ecu))
			throw new IllegalArgumentException("The current ECU must be in the ECU list.");
		
		this.currentEcu = ecu;
		
		for (EEPROMValue v : ecu.getEepromValues()) {
			getEeprom().add(v.getName(), v.getType(), v.getDefault());
		}
	}

	/**
	 * Select the ECU (electronic control unit) to generate code for
	 * 
	 * The ECU must be in the list of ECUs.
	 * The EEPROM value definitions are automatically copied to the EEPROM configuration.
	 * 
	 * @param name the ECU name
	 * @see MCUConfiguration#getEcus()
	 * @see MCUConfiguration#getEeprom()
	 */
	public void selectEcu(String name) {
		for (ECUDefinition ecu : getEcus()) {
			if (ecu.getName().equals(name)) {
				selectEcu(ecu);
				return;
			}
		}
		throw new IllegalArgumentException("No such ECU could be found.");
	}
}
