package de.upbracing.code_generation.config;

public class VariableWithSize extends Variable {
	private int size;

	public VariableWithSize(String name, String type) {
		super(name, type);
		this.size = -1;
	}

	public VariableWithSize(String name, String type, int size) {
		super(name, type);
		this.size = size;
	}

	public int getSize() {
		return size;
	}
	
	public void setType(String type, int size) {
		setType(type);
		this.size = size;
	}
}
