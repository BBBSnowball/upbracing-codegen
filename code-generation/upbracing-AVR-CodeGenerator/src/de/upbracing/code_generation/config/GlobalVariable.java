package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

@Default
public class GlobalVariable extends VariableWithSize {
	private Object initialValue;

	public GlobalVariable(String name, String type) {
		this(name, type, -1, null);
	}

	public GlobalVariable(String name, String type, Object initialValue) {
		this(name, type, -1, initialValue);
	}

	public GlobalVariable(String name, String type, int size, Object initialValue) {
		super(name, type, size);
		this.initialValue = initialValue;
	}

	public Object getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(Object initialValue) {
		this.initialValue = initialValue;
	}
}
