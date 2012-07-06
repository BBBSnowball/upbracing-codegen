package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

@Default
public class GlobalVariableConfig extends Variables<GlobalVariable> {
	public GlobalVariableConfig() {
		super(false);
	}

	public GlobalVariable add(String name, String type) {
		return add(name, type, -1, null);
	}
	
	public GlobalVariable add(String name, String type, Object defaultValue) {
		return add(name, type, -1, defaultValue);
	}

	public GlobalVariable add(String name, String type, int size) {
		return add(name, type, size, null);
	}
	
	public GlobalVariable add(String name, String type, int size, Object defaultValue) {
		GlobalVariable e = new GlobalVariable(name, type, size, defaultValue);
		add(e);
		return e;
	}
}
