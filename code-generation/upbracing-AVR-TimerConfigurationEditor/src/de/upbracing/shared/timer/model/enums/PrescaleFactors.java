package de.upbracing.shared.timer.model.enums;

public enum PrescaleFactors {
	ONE,
	EIGHT,
	SIXTYFOUR,
	TWOHUNDREDANDSIXTYFIVE,
	ONETHOUSANDANDTWENTYFOUR;
	
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
	
	public static PrescaleFactors fromString(String str) {
		if (str.equals("1"))
			return PrescaleFactors.ONE;
		if (str.equals("8"))
			return PrescaleFactors.EIGHT;
		if (str.equals("64"))
			return PrescaleFactors.SIXTYFOUR;
		if (str.equals("256"))
			return PrescaleFactors.TWOHUNDREDANDSIXTYFIVE;
		if (str.equals("1024"))
			return PrescaleFactors.ONETHOUSANDANDTWENTYFOUR;
		// Fallback
		return PrescaleFactors.ONE;
	}
	
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
