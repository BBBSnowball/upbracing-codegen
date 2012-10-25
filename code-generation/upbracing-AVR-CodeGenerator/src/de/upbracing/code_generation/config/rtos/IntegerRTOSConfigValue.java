package de.upbracing.code_generation.config.rtos;

import de.upbracing.code_generation.Messages;

public class IntegerRTOSConfigValue extends RTOSConfigValue {
	private int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;
	
	public IntegerRTOSConfigValue(RTOSConfigValueType type, ConfigFile file,
			String category, String name, String defaultValue, String comment) {
		super(type, file, category, name, defaultValue, comment);
	}

	public IntegerRTOSConfigValue(RTOSConfigValueType type, ConfigFile file,
			String category, String name, String defaultValue, String comment,
			int min, int max) {
		super(type, file, category, name, defaultValue, comment);
		this.min = min;
		this.max = max;
	}

	public static int parseInt(String value) {
		value = value.toLowerCase();
		
		if (value.startsWith("0x"))
			return Integer.parseInt(value.substring(2), 16);
		else if (value.startsWith("0b"))
			return Integer.parseInt(value.substring(2), 2);
		else if (value.startsWith("0"))
			return Integer.parseInt(value.substring(1), 8);
		else
			return Integer.parseInt(value, 10);
	}
	
	@Override
	public boolean validate(String value, Messages messages) {
		if (!super.validate(value, messages))
			return false;
		
		int x;
		
		try {
			x = parseInt(value);
		} catch (NumberFormatException e) {
			messages.error("not a number: %s", value);
			return false;
		}
		
		if (x < min) {
			messages.error("value too low: %d; it must be in range %s",
					x, describeRange());
			return false;
		} else if (x > max) {
			messages.error("value too high: %d; it must be in range %s",
					x, describeRange());
			return false;
		}
		
		return true;
	}

	private Object describeRange() {
		if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE)
			return "any integer";
		else if (min == Integer.MIN_VALUE)
			return String.format("x >= %d", min);
		else if (max == Integer.MAX_VALUE)
			return String.format("x <= %d", max);
		else
			return String.format("%d <= x <= %d", min, max);
	}
}
