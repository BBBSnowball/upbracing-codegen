package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

@Default
public class EEPROMVariable extends Variable {
	private Object defaultValue;

	public EEPROMVariable(String name, String type, Object defaultValue) {
		super(name, type);
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
}
