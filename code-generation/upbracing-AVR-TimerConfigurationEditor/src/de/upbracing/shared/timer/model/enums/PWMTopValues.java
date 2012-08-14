package de.upbracing.shared.timer.model.enums;

public enum PWMTopValues {
	BIT8,
	BIT9,
	BIT10,
	ICR,
	OCRnA;
	
	public String toString() {
		if (this.equals(BIT8)) 
			return "Top Value 255 (8 Bit)";
		if (this.equals(BIT9))
			return "Top Value 511 (9 Bit)";
		if (this.equals(BIT10))
			return "Top Value 1023 (10 Bit)";
		if (this.equals(PWMTopValues.ICR))
			return "Input Capture Register";
		if (this.equals(PWMTopValues.OCRnA))
			return "Output Compare Register";
		return "Undetermined";
	}
}
