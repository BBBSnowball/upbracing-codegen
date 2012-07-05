package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

@Default
public abstract class Variable {
	private String name;
	private String type;
	
	public Variable(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
