package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

/**
 * list of variables in the non-volatile memory (EEPROM)
 * 
 * @author benny
 */
@Default
public class EEPROMConfig extends Variables<EEPROMVariable> {
	/** constructor */
	public EEPROMConfig() {
		super(true);
	}

	/**
	 * add a variable to this list
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @return the variable object
	 */
	public EEPROMVariable add(String name, String type) {
		return add(name, type, -1, null);
	}

	/**
	 * add a variable to this list
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @param defaultValue default value for the variable
	 * @return the variable object
	 */
	public EEPROMVariable add(String name, String type, Object defaultValue) {
		return add(name, type, -1, defaultValue);
	}

	/**
	 * add a variable to this list
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @param size size of the type in bytes
	 * @param defaultValue default value for the variable
	 * @return the variable object
	 */
	public EEPROMVariable add(String name, String type, int size, Object defaultValue) {
		EEPROMVariable e = new EEPROMVariable(name, type, size, defaultValue);
		add(e);
		return e;
	}
}
