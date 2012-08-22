package de.upbracing.configurationeditor.timer.converters;

import org.eclipse.core.databinding.conversion.IConverter;

public class DoubleDisplayToModelConverter implements IConverter {

	@Override
	public Object convert(Object arg0) {
		// TODO Auto-generated method stub
		return Double.parseDouble(arg0.toString());
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
