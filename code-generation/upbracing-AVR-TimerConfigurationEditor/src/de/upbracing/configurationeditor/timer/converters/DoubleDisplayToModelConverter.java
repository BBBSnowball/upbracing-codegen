package de.upbracing.configurationeditor.timer.converters;

import org.eclipse.core.databinding.conversion.IConverter;

public class DoubleDisplayToModelConverter implements IConverter {

	@Override
	public Object convert(Object arg0) {
		// Just return the parsed double
		return Double.parseDouble(arg0.toString());
	}

	@Override
	public Object getFromType() {
		// FromType could be String in this case. 
		// Returning null here makes this accepting any source type.
		return null;
	}

	@Override
	public Object getToType() {
		// ToType could be Double in this case. 
		// Returning null here makes this accepting any target type.
		return null;
	}

}
