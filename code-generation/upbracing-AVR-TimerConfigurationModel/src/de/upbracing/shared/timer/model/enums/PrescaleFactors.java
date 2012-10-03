package de.upbracing.shared.timer.model.enums;

/**
 * This enum defines the valid prescale factors 
 * {@link PrescaleFactors#ONE 1}, {@link PrescaleFactors#EIGHT 8},
 * {@link PrescaleFactors#SIXTYFOUR 64}, 
 * {@link PrescaleFactors#TWOHUNDREDANDSIXTYFIVE 256}
 * and {@link PrescaleFactors#ONETHOUSANDANDTWENTYFOUR 1024}
 * for the AT90CAN128 processor.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public enum PrescaleFactors {
	/**
	 * Timer runs at full processor speed. 
	 */
	ONE,
	/**
	 * Timer runs at 1/8th of processor speed.
	 */
	EIGHT,
	/**
	 * Timer runs at 1/64th of processor speed.
	 */
	SIXTYFOUR,
	/**
	 * Timer runs at 1/256th of processor speed.
	 */
	TWOHUNDREDANDSIXTYFIVE,
	/**
	 * Timer runs at 1/1024th of processor speed.
	 */
	ONETHOUSANDANDTWENTYFOUR;
	
	/** 
	 * This method returns a user-friendly <code>String</code> representation
	 * of the current prescale factor selection.
	 * @return a user-friendly <code>String</code> representation.
	 */
	@Override
	public String toString() {
		if (this.equals(PrescaleFactors.ONE))
			return "1";
		if (this.equals(PrescaleFactors.EIGHT))
			return "8";
		if (this.equals(PrescaleFactors.SIXTYFOUR))
			return "64";
		if (this.equals(PrescaleFactors.TWOHUNDREDANDSIXTYFIVE))
			return "256";
		if (this.equals(PrescaleFactors.ONETHOUSANDANDTWENTYFOUR))
			return "1024";
		// Fallback
		return this.name();
	}
	
	/**
	 * Gets the prescale divisor as integer.
	 * @return the prescale divisor.
	 */
	public int getNumeric() {
		if (this.equals(PrescaleFactors.ONE))
			return 1;
		if (this.equals(PrescaleFactors.EIGHT))
			return 8;
		if (this.equals(PrescaleFactors.SIXTYFOUR))
			return 64;
		if (this.equals(PrescaleFactors.TWOHUNDREDANDSIXTYFIVE))
			return 256;
		if (this.equals(PrescaleFactors.ONETHOUSANDANDTWENTYFOUR))
			return 1024;
		// Fallback
		return 1;
	}
}
