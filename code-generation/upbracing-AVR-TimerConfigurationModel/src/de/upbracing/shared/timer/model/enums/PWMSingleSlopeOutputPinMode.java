package de.upbracing.shared.timer.model.enums;

/**
 * This enum defines the output pin modes {@link PWMSingleSlopeOutputPinMode#NORMAL NORMAL}, 
 * {@link PWMSingleSlopeOutputPinMode#TOGGLE TOGGLE}, {@link PWMSingleSlopeOutputPinMode#CLEAR CLEAR} and 
 * {@link PWMSingleSlopeOutputPinMode#SET SET} for fast PWM mode.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public enum PWMSingleSlopeOutputPinMode {
	/**
	 * In this mode, the output pin is disconnected.
	 */
	NORMAL,
	/**
	 * In this mode, the output pin will be toggled on compare match.
	 */
	TOGGLE,
	/**
	 * In this mode, the output pin will be cleared on compare match and set at top.
	 */
	CLEAR,
	/**
	 * In this mode, the output pin will be set on compare match and cleared at top. 
	 */
	SET;
	
	/** 
	 * This method returns a user-friendly <code>String</code> representation
	 * of the current output pin mode.
	 * @return a user-friendly <code>String</code> representation.
	 */
	@Override
	public String toString() {
		if (this.equals(PWMSingleSlopeOutputPinMode.TOGGLE))
			return "Toggle Output on Compare Match";
		if (this.equals(PWMSingleSlopeOutputPinMode.CLEAR))
//			return "Clear Output on Compare Match, Set on Top";
			return "Output PWM signal";
		if (this.equals(PWMSingleSlopeOutputPinMode.SET))
//			return "Set Output on Compare Match, Clear on Top";
			return "Output inverted PWM signal";
		return "Disconnected";
	}
}
