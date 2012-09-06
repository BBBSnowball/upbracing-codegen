package de.upbracing.shared.timer.model.enums;

public enum PWMDualSlopeOutputPinMode {
	NORMAL,
	TOGGLE,
	CLEAR_SET,
	SET_CLEAR;
	
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
