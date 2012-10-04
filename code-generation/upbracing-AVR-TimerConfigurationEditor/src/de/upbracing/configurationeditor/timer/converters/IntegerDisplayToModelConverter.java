package de.upbracing.configurationeditor.timer.converters;

import org.eclipse.core.databinding.conversion.IConverter;

public class IntegerDisplayToModelConverter implements IConverter {

	@Override
	public Object convert(Object arg0) {
		// Just return the parsed Integer
		return Integer.parseInt(arg0.toString());
	}

	@Override
	public Object getFromType() {
		// FromType could be String in this case. 
		// Returning null here makes this accepting any source type.
		return null;
	}

	@Override
	public Object getToType() {
		// ToType could be Integer in this case. 
		// Returning null here makes this accepting any source type.
		return null;
	}

}
