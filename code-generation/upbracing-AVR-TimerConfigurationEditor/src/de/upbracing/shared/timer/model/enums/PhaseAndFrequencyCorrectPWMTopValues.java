package de.upbracing.shared.timer.model.enums;

public enum PhaseAndFrequencyCorrectPWMTopValues {
	ICR,
	OCRnA;
	
	public String toString() {
		if (this.equals(PhaseAndFrequencyCorrectPWMTopValues.ICR))
			return "Input Capture Register";
		if (this.equals(PhaseAndFrequencyCorrectPWMTopValues.OCRnA))
			return "Output Compare Register";
		return "Undetermined";
	}
}
