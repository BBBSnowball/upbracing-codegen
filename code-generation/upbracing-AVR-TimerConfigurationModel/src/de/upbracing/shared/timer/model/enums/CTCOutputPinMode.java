package de.upbracing.shared.timer.model.enums;

/**
 * This enum defines the output pin modes {@link CTCOutputPinMode#NORMAL NORMAL}, 
 * {@link CTCOutputPinMode#TOGGLE TOGGLE}, {@link CTCOutputPinMode#CLEAR CLEAR} and 
 * {@link CTCOutputPinMode#SET SET} for CTC mode.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public enum CTCOutputPinMode {
	/**
	 * In this mode, the output pin is disconnected.
	 */
	NORMAL,
	/**
	 * In this mode, the output pin will be toggled on compare match.
	 */
	TOGGLE,
	/**
	 * In this mode, the output pin will be cleared on compare match.
	 */
	CLEAR,
	/**
	 * In this mode, the output pin will be set on compare match. 
	 */
	SET;
	
	/** 
	 * This method returns a user-friendly <code>String</code> representation
	 * of the current output pin mode.
	 * @return a user-friendly <code>String</code> representation.
	 */
	@Override
	public String toString() {
		if (this.equals(CTCOutputPinMode.TOGGLE)) {
			return "Toggle Output on Compare Match";
		}
		if (this.equals(CTCOutputPinMode.CLEAR)) {
			return "Clear Output on Compare Match";
		}
		if (this.equals(CTCOutputPinMode.SET)) {
			return "Set Output on Compare Match";
		}
		return "Disconnected";
	}
}
