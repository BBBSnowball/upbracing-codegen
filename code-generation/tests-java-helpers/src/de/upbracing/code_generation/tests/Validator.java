package de.upbracing.code_generation.tests;

public interface Validator {
	public enum Result { VALID, INVALID, INCOMPLETE, UNKNOWN };
	
	Result validate(String text);
}
