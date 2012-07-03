package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

@Default
public class EEPROMConfig extends Variables<EEPROMVariable> {
	public EEPROMVariable add(String name, String type) {
		return add(name, type, null);
	}
	
	public EEPROMVariable add(String name, String type, Object defaultValue) {
		EEPROMVariable e = new EEPROMVariable(name, type, defaultValue);
		add(e);
		return e;
	}
}
