package de.upbracing.shared.timer.model.enums;

/**
 * This enum defines the output pin modes {@link PWMDualSlopeOutputPinMode#NORMAL NORMAL}, 
 * {@link PWMDualSlopeOutputPinMode#TOGGLE TOGGLE}, {@link PWMDualSlopeOutputPinMode#CLEAR_SET CLEAR_SET} and 
 * {@link PWMDualSlopeOutputPinMode#SET_CLEAR SET_CLEAR} for phase correct as well as phase and frequency correct PWM mode.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public enum PWMDualSlopeOutputPinMode {
	/**
	 * In this mode, the output pin is disconnected.
	 */
	NORMAL,
	/**
	 * In this mode, the output pin will be toggled on compare match.
	 */
	TOGGLE,
	/**
	 * In this mode, the output pin will be cleared while upcounting
	 * and set while downcounting.
	 */
	CLEAR_SET,
	/**
	 * In this mode, the output pin will be set while upcounting
	 * and cleared while downcounting.
	 */
	SET_CLEAR;
	
	/** 
	 * This method returns a user-friendly <code>String</code> representation
	 * of the current output pin mode.
	 * @return a user-friendly <code>String</code> representation.
	 */
	@Override
	public String toString() {
		if (this.equals(PWMDualSlopeOutputPinMode.TOGGLE))
			return "Toggle Output on Compare Match";
		if (this.equals(PWMDualSlopeOutputPinMode.CLEAR_SET))
			return "Clear Output while upcounting, set while downcounting";
		if (this.equals(PWMDualSlopeOutputPinMode.SET_CLEAR))
			return "Set Output while upcounting, clear while downcounting";
		return "Disconnected";
	}
}
