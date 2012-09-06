package de.upbracing.shared.timer.model.enums;

public enum PWMSingleSlopeOutputPinMode {
	NORMAL,
	TOGGLE,
	CLEAR,
	SET;
	
	public String toString() {
		if (this.equals(PWMSingleSlopeOutputPinMode.TOGGLE))
			return "Toggle Output on Compare Match";
		if (this.equals(PWMSingleSlopeOutputPinMode.CLEAR))
			return "Clear Output on Compare Match";
		if (this.equals(PWMSingleSlopeOutputPinMode.SET))
			return "Set Output on Compare Match";
		return "Disconnected";
	}
}
