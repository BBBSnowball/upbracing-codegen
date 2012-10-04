package de.upbracing.shared.timer.model.enums;

/**
 * This enum defines the possible top value registers 
 * {@link CTCTopValues#ICR ICR} and {@link CTCTopValues#OCRnA ORCnA}
 * for CTC mode.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public enum CTCTopValues {
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
		if (this.equals(CTCTopValues.ICR))
			return "Input Capture Register";
		if (this.equals(CTCTopValues.OCRnA))
			return "Output Compare Register";
		return "Undetermined";
	}
}
