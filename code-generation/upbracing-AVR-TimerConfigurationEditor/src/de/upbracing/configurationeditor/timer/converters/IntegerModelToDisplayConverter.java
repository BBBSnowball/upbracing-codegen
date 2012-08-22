package de.upbracing.configurationeditor.timer.converters;

import org.eclipse.core.databinding.conversion.IConverter;

public class IntegerModelToDisplayConverter implements IConverter {

	@Override
	public Object convert(Object arg0) {
		if (arg0.getClass() == Integer.class) {
			return arg0.toString().replace(".", "");
		}
		return null;
	}

	@Override
	public Object getFromType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getToType() {
		// TODO Auto-generated method stub
		return null;
	}

}
