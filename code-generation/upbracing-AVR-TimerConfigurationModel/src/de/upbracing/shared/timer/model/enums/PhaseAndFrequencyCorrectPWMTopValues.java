package de.upbracing.shared.timer.model.enums;

/**
 * This enum defines the possible top value registers
 * {@link PhaseAndFrequencyCorrectPWMTopValues#ICR ICR} and
 * {@link PhaseAndFrequencyCorrectPWMTopValues#OCRnA OCRnA}
 * for phase and frequency correct PWM mode.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public enum PhaseAndFrequencyCorrectPWMTopValues {
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
	 * of the current top value register selection.
	 * @return a user-friendly <code>String</code> representation.
	 */
	@Override
	public String toString() {
		if (this.equals(PhaseAndFrequencyCorrectPWMTopValues.ICR))
			return "Input Capture Register";
		if (this.equals(PhaseAndFrequencyCorrectPWMTopValues.OCRnA))
			return "Output Compare Register";
		return "Undetermined";
	}
}
