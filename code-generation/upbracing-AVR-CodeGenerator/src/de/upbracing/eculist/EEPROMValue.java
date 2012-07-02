package de.upbracing.eculist;

public class EEPROMValue {
	private String name, type, defaultValue;
	
	public EEPROMValue(String name, String type, String defaultValue) {
		super();
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	public EEPROMValue() { }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefault() {
		return defaultValue;
	}

	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return "eeprom-value(" + name + ":" + type + "=" + defaultValue + ")";
	}
}
