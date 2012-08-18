package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

import de.upbracing.code_generation.TemplateHelpers;

/**
 * Global variable in the C program
 * 
 * @author benny
 */
@Default
public class GlobalVariable extends VariableWithSize {
	private Object initialValue;

	/**
	 * constructor
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 */
	public GlobalVariable(String name, String type) {
		this(name, type, -1, null);
	}

	/**
	 * constructor
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @param initialValue initial value for the variable
	 */
	public GlobalVariable(String name, String type, Object initialValue) {
		this(name, type, -1, initialValue);
	}

	/**
	 * constructor
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @param size size of the type in bytes
	 * @param initialValue initial value for the variable
	 */
	public GlobalVariable(String name, String type, int size, Object initialValue) {
		super(name, type, size);
		this.initialValue = initialValue;
	}

	/**
	 * Get initial value
	 * @return the initial value
	 */
	public Object getInitialValue() {
		return initialValue;
	}

	/**
	 * Set initial value
	 * @param initialValue the initial value
	 */
	public void setInitialValue(Object initialValue) {
		this.initialValue = initialValue;
	}
	
	public String getGetterName() {
		return "get" + TemplateHelpers.capitalize(getName());
	}
	
	public String getSetterName() {
		return "set" + TemplateHelpers.capitalize(getName());
	}
}
