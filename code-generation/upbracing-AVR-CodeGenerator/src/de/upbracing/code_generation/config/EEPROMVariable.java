package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

/**
 * Variable in the non-volatile memory (EEPROM)
 * 
 * @author benny
 */
@Default
public class EEPROMVariable extends VariableWithSize {
	private Object defaultValue;

	/**
	 * constructor
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @param defaultValue default value for the variable
	 */
	public EEPROMVariable(String name, String type, Object defaultValue) {
		super(name, type);
		this.defaultValue = defaultValue;
	}

	/**
	 * constructor
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @param size size of the type in bytes
	 * @param defaultValue default value for the variable
	 */
	public EEPROMVariable(String name, String type, int size, Object defaultValue) {
		super(name, type, size);
		this.defaultValue = defaultValue;
	}

	/**
	 * Get the default value
	 * @return the default value
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}
}
