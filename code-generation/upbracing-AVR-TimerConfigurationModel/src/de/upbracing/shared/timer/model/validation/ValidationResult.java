package de.upbracing.shared.timer.model.validation;

/**
 * This enum defines the three error levels 
 * {@link ValidationResult#OK OK}, 
 * {@link ValidationResult#WARNING WARNING} and 
 * {@link ValidationResult#ERROR ERROR}.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public enum ValidationResult {
	/**
	 * No errors. 
	 */
	OK,
	/**
	 * Code generation may continue. But the resulting behavior of the 
	 * processor might not be as expected. This goes especially for quantized
	 * register values.
	 */
	WARNING,
	/**
	 * The configuration contains severe errors, which need to be fixed before
	 * code generation can continue.
	 */
	ERROR
}
