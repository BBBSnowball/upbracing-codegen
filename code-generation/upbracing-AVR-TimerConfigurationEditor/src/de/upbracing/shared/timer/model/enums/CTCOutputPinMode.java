package de.upbracing.shared.timer.model.enums;

public enum CTCOutputPinMode {
	NORMAL,
	TOGGLE,
	CLEAR,
	SET;
	
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
