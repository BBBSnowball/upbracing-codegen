package de.upbracing.code_generation.config;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.simpleframework.xml.Default;

/**
 * Configuration for pin names
 * 
 * It is a map with pairs of pin name and Pin objects, which represent the pin of the microcontroller
 * 
 * @author benny
 */
@Default
@SuppressWarnings("serial")
public class PinConfig extends TreeMap<String, Pin> {
	/** map of port aliases */
	private TreeMap<String, Character> ports = new TreeMap<String, Character>();
	
	/** add a pin name
	 * 
	 * @param name name of the pin; must be a valid C identifier
	 * @param pin pin name on the microcontroller, e.g. "PA3"
	 */
	public void add(String name, String pin) {
		add(name, new Pin(pin));
	}

	/** add a pin name
	 * 
	 * @param name name of the pin; must be a valid C identifier
	 * @param pin Pin object for the pin on the microcontroller, e.g. new Pin("PA3")
	 */
	public void add(String name, Pin pin) {
		this.put(name, pin);
	}
	
	/** add names for consecutive pins
	 * 
	 *   addRange("PA3", "A", "B", "C)
	 * is equivalent to
	 *   add("A", "PA3");
	 *   add("B", "PA4");
	 *   add("C", "PA5");
	 *   
	 * @param first_pin first pin on the microcontroller, e.g. "PA3"
	 * @param names names for the pins
	 */
	public void addRange(String first_pin, String... names) {
		Pin pin = new Pin(first_pin);
		
		boolean first = true;
		for (String name : names) {
			if (first)
				first = false;
			else {
				pin = pin.next();
				if (pin == null)
					throw new IllegalArgumentException("Cannot figure out the next pin -> too many names");
			}
			
			add(name, pin);
		}
	}
	
	/** add names for a whole port
	 * 
	 * Names will be generated for each pin of the port.
	 *   addPort("LEDS", 'C');
	 * defines the same names as
	 *   add("LEDS0", "PC0");
	 *   add("LEDS1", "PC1");
	 *   ...
	 *   
	 * In addition, the port is put into the map of port names. The
	 * code generator will create additional methods that can be used
	 * to efficiently manipulate the whole port.
	 * 
	 * @param basename base name; the numbers 0 to 7 will be appended for the pins
	 * @param port port name, e.g. 'A' for PA0 to PA7
	 * @see #getPorts()
	 */
	public void addPort(String basename, char port) {
		ports.put(basename, port);
		
		for (int bit=0;bit<8;bit++)
			add(basename + bit, new Pin(port, bit));
	}

	/** add names for a whole port
	 * 
	 * Names will be generated for each pin of the port.
	 *   addPort("LEDS", "PC");
	 * defines the same names as
	 *   add("LEDS0", "PC0");
	 *   add("LEDS1", "PC1");
	 *   ...
	 *   
	 * In addition, the port is put into the map of port names. The
	 * code generator will create additional methods that can be used
	 * to efficiently manipulate the whole port.
	 * 
	 * @param basename base name; the numbers 0 to 7 will be appended for the pins
	 * @param port port name, e.g. "PA" for PA0 to PA7
	 * @see #getPorts()
	 */
	public void addPort(String basename, String port) {
		if (!Pattern.matches("^P[A-Z]$", port))
			throw new IllegalArgumentException("invalid port");
		
		addPort(basename, port.charAt(1));
	}
	
	/** get the map of ports
	 * 
	 * The map contains pairs of port name (C identifier) and hardware port name (e.g. 'A' for PA0 to PA7)
	 * @return the map
	 * @see #addPort(String, char)
	 */
	public SortedMap<String, Character> getPorts() {
		return Collections.unmodifiableSortedMap(ports);
	}
	
	/** add an alias for an existing pin or port definition
	 * 
	 * @param alias new name
	 * @param existing_name old name
	 */
	public void addAlias(String alias, String existing_name) {
		Pin pin = get(existing_name);
		if (pin != null) {
			add(alias, pin);
			return;
		}
		
		Character port = getPorts().get(existing_name);
		if (port != null) {
			addPort(alias, port);
			return;
		}
		
		throw new IllegalStateException("Cannot create an alias for a pin or port that doesn't exist: " + existing_name);
	}
}
