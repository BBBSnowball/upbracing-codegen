package de.upbracing.shared.timer.model.enums;

public enum CTCTopValues {
	ICR,
	OCRnA;
	
	public String toString() {
		if (this.equals(CTCTopValues.ICR))
			return "Input Capture Register";
		if (this.equals(CTCTopValues.OCRnA))
			return "Output Compare Register";
		return "Undetermined";
	}
}
