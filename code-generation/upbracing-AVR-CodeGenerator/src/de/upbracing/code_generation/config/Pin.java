package de.upbracing.code_generation.config;

import java.util.regex.Pattern;

/**
 * Represents a pin of the microcontroller
 * 
 * @author benny
 */
public class Pin {
	/** regex for valid pin names (a valid name doesn't always mean that such a pin really exists) */
	private final static Pattern pinPattern = Pattern.compile("^P[A-Z][0-7]$");
	
	private final char port;
	private final int bit;

	/**
	 * constructor
	 * @param pin pin name
	 */
	public Pin(String pin) {
		if (!pinPattern.matcher(pin).matches())
			throw new IllegalArgumentException("This is not a valid pin name: " + pin);
		
		this.port = pin.charAt(1);
		this.bit = pin.charAt(2) - '0';
	}
	
	/**
	 * constructor
	 * @param port the port name, e.g. 'A' for PA3
	 * @param bit the pin/bit of the port, e.g. 3 for PA3
	 */
	public Pin(char port, int bit) {
		if (port < 'A' || port > 'Z' || bit < 0 || bit > 7)
			throw new IllegalArgumentException("port or bit is wrong");
		this.port = port;
		this.bit = bit;
	}

	/**
	 * Get pin name, e.g. "PA3"
	 * 
	 * @return the name
	 */
	public String getPinName() {
		return "P" + port + bit;
	}
	
	@Override
	public String toString() { return getPinName(); }
	
	/** Get name of the data direction register, e.g. DDRA */
	public String getDDR() { return "DDR" + port; }
	/** Get name of the output register, e.g. PORTA */
	public String getPORT() { return "PORT" + port; }
	/** Get name of the input register, e.g. PINA */
	public String getPIN() { return "PIN" + port; }
	
	/** Get port name, e.g. 'A' for PA3 */
	public char getPortName() { return port; }
	/** Get pin/bit , e.g. 3 for PA3 */
	public int getBit() { return bit; }
	/** Get a C code expression to access the bit, e.g. "(1<<3)" for PA3 */
	public String getMaskExpr() { return "(1<<" + bit + ")"; }
	
	/**
	 * Get the next pin on the same port, e.g. PA4 for PA3
	 * @return the next Pin or null, if there is no such Pin
	 */
	public Pin next() {
		if (bit < 7)
			return new Pin(port, bit+1);
		else
			return null;
	}
}
