package de.upbracing.code_generation.config.rtos;

import de.upbracing.code_generation.Messages;

public class RTOSConfigValue {
	public enum ConfigFile {
		FEATURES,
		APPLICATION
	}
	
	private RTOSConfigValueType type;
	private ConfigFile file;
	private String category;
	private String name;
	private String defaultValue;
	private String comment;
	
	private String value;

	public RTOSConfigValue(RTOSConfigValueType type, ConfigFile file,
			String category, String name,
			String defaultValue, String comment) {
		this.type = type;
		this.file = file;
		this.category = category;
		this.name = name;
		this.defaultValue = defaultValue;
		this.comment = comment;
		this.value = defaultValue;
	}
	
	public boolean validate(String value, Messages messages) {
		return type.validate(value, messages) && true;
	}

	public RTOSConfigValueType getType() {
		return type;
	}

	public ConfigFile getFile() {
		return file;
	}

	public String getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getComment() {
		return comment;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
