package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

@Default
public class EEPROMVariable extends VariableWithSize {
	private Object defaultValue;

	public EEPROMVariable(String name, String type, Object defaultValue) {
		super(name, type);
		this.defaultValue = defaultValue;
	}

	public EEPROMVariable(String name, String type, int size, Object defaultValue) {
		super(name, type, size);
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
}
