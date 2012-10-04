package de.upbracing.shared.timer.model.enums;

/**
 * This enum defines the possible top value registers
 * {@link PWMTopValues#BIT8 BIT8}, {@link PWMTopValues#BIT9 BIT9},
 * {@link PWMTopValues#BIT10 BIT10}, {@link PWMTopValues#ICR ICR} and
 * {@link PWMTopValues#OCRnA OCRnA} for both fast and phase correct
 * PWM modes.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public enum PWMTopValues {
	/**
	 * Constant number 255 is used as top value.
	 */
	BIT8,
	/**
	 * Constant number 511 is used as top value.
	 */
	BIT9,
	/**
	 * Constant number 1023 is used as top value.
	 */
	BIT10,
	/**
	 * Register ICR is selected as top value register.
	 */
	ICR,
	/**
	 * Register OCRnA is selected as top value register.
	 */
	OCRnA;
	
	/** 
	 * This method returns a user-friendly <code>String</code> representation
	 * of the current top value selection.
	 * @return a user-friendly <code>String</code> representation.
	 */
	@Override
	public String toString() {
		if (this.equals(BIT8)) 
			return "Top Value 255 (8 Bit)";
		if (this.equals(BIT9))
			return "Top Value 511 (9 Bit)";
		if (this.equals(BIT10))
			return "Top Value 1023 (10 Bit)";
		if (this.equals(PWMTopValues.ICR))
			return "Input Capture Register";
		if (this.equals(PWMTopValues.OCRnA))
			return "Output Compare Register";
		return "Undetermined";
	}
}
