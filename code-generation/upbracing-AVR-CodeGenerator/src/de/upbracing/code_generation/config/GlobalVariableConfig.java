package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Default;

@Default
public class GlobalVariableConfig extends Variables<GlobalVariable> {
	
	List<String> declerations = new ArrayList<String>();
	
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
	
	/**
	 * add a declaration that is included in the generated file to include 
	 * additional datatypes. (It is used by the CAN code generator) 
	 * 
	 * @param declaration String with declaration to be embedded into the generated file
	 */
	public void addDeclaration(String declaration) {
		this.declerations.add(declaration);
	}
	
	/**
	 * returns the list of declarations
	 * 
	 * @return the list of declarations
	 */
	public List<String> getDeclarations() {
		return declerations;
	}
}
