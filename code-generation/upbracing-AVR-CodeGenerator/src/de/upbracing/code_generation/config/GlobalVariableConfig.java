package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

@Default
public class GlobalVariableConfig extends Variables<GlobalVariable> {
	/** constructor */
	public GlobalVariableConfig() {
		super(false);
	}

	/**
	 * add a variable
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 */
	public GlobalVariable add(String name, String type) {
		return add(name, type, -1, null);
	}

	/**
	 * add a variable
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @param initialValue initial value for the variable
	 */
	public GlobalVariable add(String name, String type, Object initialValue) {
		return add(name, type, -1, initialValue);
	}

	/**
	 * add a variable
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @param size size of the type in bytes
	 * @param initialValue initial value for the variable
	 */
	public GlobalVariable add(String name, String type, int size, Object initialValue) {
		GlobalVariable e = new GlobalVariable(name, type, size, initialValue);
		add(e);
		return e;
	}
}
