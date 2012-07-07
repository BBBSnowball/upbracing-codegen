package de.upbracing.eculist;

import org.simpleframework.xml.Default;

/**
 * Definition of a EEPROM variable; read from ecu-list.xml
 * 
 * @author benny
 */
@Default
public class EEPROMValue {
	private String name, type, defaultValue;

	/**
	 * constructor
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @param defaultValue default value for the variable
	 */
	public EEPROMValue(String name, String type, String defaultValue) {
		super();
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	/** constructor */
	public EEPROMValue() { }

	/** get variable name */
	public String getName() {
		return name;
	}

	/** set variable name */
	public void setName(String name) {
		this.name = name;
	}

	/** get variable type in C code */
	public String getType() {
		return type;
	}

	/** set variable type in C code */
	public void setType(String type) {
		this.type = type;
	}

	/** get default value for the variable */
	public String getDefault() {
		return defaultValue;
	}

	/** set default value for the variable */
	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return "eeprom-value(" + name + ":" + type + "=" + defaultValue + ")";
	}
}
