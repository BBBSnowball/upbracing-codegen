package de.upbracing.configurationeditor.timer.converters;

import org.eclipse.core.databinding.conversion.IConverter;

public class IntegerModelToDisplayConverter implements IConverter {

	@Override
	public Object convert(Object arg0) {
		// Remove Decimal Points
		if (arg0.getClass() == Integer.class) {
			return arg0.toString().replace(".", "").replace(",", "");
		}
		return null;
	}

	@Override
	public Object getFromType() {
		// FromType could be Integer in this case. 
		// Returning null here makes this accepting any target type.
		return null;
	}

	@Override
	public Object getToType() {
		// ToType could be String in this case. 
		// Returning null here makes this accepting any source type.
		return null;
	}

}
