package de.upbracing.code_generation.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.simpleframework.xml.Default;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.Severity;
import de.upbracing.code_generation.config.rtos.RTOSConfig;
import de.upbracing.dbc.DBC;
import de.upbracing.eculist.ECUDefinition;
import de.upbracing.eculist.EEPROMValue;
import de.upbracing.shared.timer.model.ConfigurationModel;

/**
 * main configuration class
 * 
 * You can reach all configuration objects from this class.
 * 
 * @author benny
 */
@Default(required=false)
public class CodeGeneratorConfigurations_old {
	private List<ECUDefinition> ecus;
	private ECUDefinition currentEcu;
	private DBCConfig canConfig;
	private EEPROMConfig eeprom = new EEPROMConfig();
	private PinConfig pins = new PinConfig();
	private GlobalVariableConfig global_variables = new GlobalVariableConfig();
	private RTOSConfig rtos = new RTOSConfig();
	private ConfigurationModel timer;
	private StatemachinesConfig statemachines;

	private Messages messages;
	
	public CodeGeneratorConfigurations_old() {
		messages = new Messages().withOutputTo(System.err, Severity.INFO);
		statemachines = new StatemachinesConfig();
		statemachines.addFormatters(messages);
	}
	
	// Note (Peer): This was necessary in order to keep the TimerConfigurationEditor
	//              working with this package of code generators. Otherwise the editor
	//              would need to include the whole StateMachineEditor model.
	public CodeGeneratorConfigurations_old(boolean ignoreMe) {
		messages = new Messages().withOutputTo(System.err, Severity.INFO);
	}
	
	/** current working directory for loading config files */
	public static String currentDirectory = ".";
	
	/** get current working directory for loading config files */
	public static String getCurrentDirectory() { return currentDirectory; }

	/** set current working directory for loading config files */
	public static void setCurrentDirectory(String cwd) { currentDirectory = cwd; }

	/** make absolute path
	 * 
	 * If the path is not absolute, use {@link #getCurrentDirectory()} as the parent. 
	 * 
	 * @param path the path
	 * @return absolute path
	 */
	public static File makeAbsolute(File path) {
		if (path.isAbsolute())
			return path;
		else
			return new File(getCurrentDirectory(), path.toString());
	}
	
	/** make absolute path
	 * 
	 * If the path is not absolute, use {@link #getCurrentDirectory()} as the parent. 
	 * 
	 * @param path the path
	 * @return absolute path
	 */
	public static String makeAbsolute(String path) {
		return makeAbsolute(new File(path)).getAbsolutePath();
	}

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
	 * @see CodeGeneratorConfigurations#selectEcu(ECUDefinition)
	 */
	public ECUDefinition getCurrentEcu() {
		return currentEcu;
	}

	/**
	 * Get CAN configuration
	 * 
	 * @return the CAN configuration object
	 */
	public DBCConfig getCan() {
		return canConfig;
	}
	
	/**
	 * Set CAN configuration
	 * 
	 * This is usually read from a DBC file using parse-dbc.rb
	 * 
	 * @param can the CAN config
	 */
	public void setCan(DBC can) {
		//setCan(new DBCConfig(can, this));
		throw new RuntimeException("not implemented (anymore)");
	}
	
	/**
	 * Set CAN configuration
	 * 
	 * This is set by the setCan method
	 * 
	 * @param can the CAN config
	 */
	public void setCan(DBCConfig canConfig) {
		this.canConfig = canConfig;
	}
	
	/**
	 * @deprecated Use {@link #getCan()} instead
	 */
	@Deprecated
	public DBCConfig getCanConfig() {
		return canConfig;
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
	 * Get operating system config
	 * 
	 * @return the config object
	 */
	public RTOSConfig getRtos() {
		return rtos;
	}

	/**
	 * Get statemachines config
	 * 
	 * @return the config object
	 */
	public StatemachinesConfig getStatemachines() {
		return statemachines;
	}

	/**
	 * Get timer config
	 * 
	 * @return the config object
	 */
	public ConfigurationModel getTimerConfig() {
		return timer;
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
	 * @see CodeGeneratorConfigurations#getEcus()
	 * @see CodeGeneratorConfigurations#getEeprom()
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
	
	/**
	 * Loads the timer configuration for this MCU from disk
	 * 
	 * @param path to configuration file
	 * @throws FileNotFoundException
	 */
	public void loadTimerConfiguration(String path) throws FileNotFoundException {
		timer = ConfigurationModel.Load(makeAbsolute(path));
		if (timer == null) {
			timer = new ConfigurationModel();
			messages.addMessage(Severity.ERROR, "Cannot find configuration file \"" + path + "\". Empty timer configuration is used!");
		}
	}
	
	/** 
	 * Sets the timer configuration for this MCU
	 * 
	 * @param m new configuration model
	 */
	public void setTimerConfig(ConfigurationModel m) {
		timer = m;
	}

	/**
	 * Get object that is used to report messages in validate and updateConfig
	 * @return the messages object
	 */
	public Messages getMessages() {
		return messages;
	}
	
	/**
	 * Set object that is used to report messages in validate and updateConfig
	 * @param messages the new value
	 */
	public void setMessages(Messages messages) {
		this.messages = messages;
		
		getStatemachines().addFormatters(messages);
	}
}
