package de.upbracing.code_generation.config;

import java.util.regex.Pattern;

public class Pin {
	private final static Pattern pinPattern = Pattern.compile("^P[A-Z][0-9]$");
	
	private final char port;
	private final int bit;

	public Pin(String pin) {
		if (!pinPattern.matcher(pin).matches())
			throw new IllegalArgumentException("This is not a valid pin name: " + pin);
		
		this.port = pin.charAt(1);
		this.bit = pin.charAt(2) - '0';
	}
	
	public Pin(char port, int bit) {
		if (port < 'A' || port > 'Z' || bit < 0 || bit > 7)
			throw new IllegalArgumentException("port or bit is wrong");
		this.port = port;
		this.bit = bit;
	}

	public String getPinName() {
		return "P" + port + bit;
	}
	
	public String toString() { return getPinName(); }
	
	public String getDDR() { return "DDR" + port; }
	public String getPORT() { return "PORT" + port; }
	public String getPIN() { return "PIN" + port; }
	
	public char getPortName() { return port; }
	public int getBit() { return bit; }
	public String getMaskExpr() { return "(1<<" + bit + ")"; }
	
	public Pin next() {
		if (bit < 7)
			return new Pin(port, bit+1);
		else
			return null;
	}
}
