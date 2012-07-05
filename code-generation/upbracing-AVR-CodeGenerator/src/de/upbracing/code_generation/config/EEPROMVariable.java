package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

@Default
public class EEPROMVariable extends Variable {
	private Object defaultValue;
	private int size;

	public EEPROMVariable(String name, String type, Object defaultValue) {
		super(name, type);
		this.defaultValue = defaultValue;
		this.size = -1;
	}

	public EEPROMVariable(String name, String type, int size, Object defaultValue) {
		super(name, type);
		this.defaultValue = defaultValue;
		this.size = size;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public int getSize() {
		return size;
	}
	
	public void setType(String type, int size) {
		setType(type);
		this.size = size;
	}
}
