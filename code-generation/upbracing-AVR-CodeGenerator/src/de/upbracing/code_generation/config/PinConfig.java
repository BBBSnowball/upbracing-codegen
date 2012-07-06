package de.upbracing.code_generation.config;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import org.simpleframework.xml.Default;

@Default
@SuppressWarnings("serial")
public class PinConfig extends TreeMap<String, Pin> {
	private TreeMap<String, Character> ports = new TreeMap<String, Character>();
	
	public void add(String name, String pin) {
		add(name, new Pin(pin));
	}
	
	public void add(String name, Pin pin) {
		this.put(name, pin);
	}
	
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
	
	public void addPort(String basename, char port) {
		ports.put(basename, port);
		
		for (int bit=0;bit<8;bit++)
			add(basename + bit, new Pin(port, bit));
	}
	
	public SortedMap<String, Character> getPorts() {
		return Collections.unmodifiableSortedMap(ports);
	}
}
