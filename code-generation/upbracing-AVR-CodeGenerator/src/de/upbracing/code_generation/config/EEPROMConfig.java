package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

@Default
public class EEPROMConfig extends Variables<EEPROMVariable> {
	public EEPROMConfig() {
		super(true);
	}

	public EEPROMVariable add(String name, String type) {
		return add(name, type, -1, null);
	}
	
	public EEPROMVariable add(String name, String type, Object defaultValue) {
		return add(name, type, -1, defaultValue);
	}

	public EEPROMVariable add(String name, String type, int size) {
		return add(name, type, size, null);
	}
	
	public EEPROMVariable add(String name, String type, int size, Object defaultValue) {
		EEPROMVariable e = new EEPROMVariable(name, type, size, defaultValue);
		add(e);
		return e;
	}
}
