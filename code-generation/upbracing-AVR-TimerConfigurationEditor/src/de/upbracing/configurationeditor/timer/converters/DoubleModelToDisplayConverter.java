package de.upbracing.configurationeditor.timer.converters;

import java.text.DecimalFormat;

import org.eclipse.core.databinding.conversion.IConverter;

public class DoubleModelToDisplayConverter implements IConverter {

	@Override
	public Object convert(Object arg0) {
		if (arg0.getClass() == Double.class) {
			DecimalFormat f = new DecimalFormat("###.##########");
			return f.format((Double)arg0).replace(',', '.');
		}
		return null;
	}

	@Override
	public Object getFromType() {
		// FromType could be Double in this case. 
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
