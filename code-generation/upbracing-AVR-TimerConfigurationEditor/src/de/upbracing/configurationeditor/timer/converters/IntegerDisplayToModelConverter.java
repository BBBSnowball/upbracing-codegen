package de.upbracing.configurationeditor.timer.converters;

import org.eclipse.core.databinding.conversion.IConverter;

public class IntegerDisplayToModelConverter implements IConverter {

	@Override
	public Object convert(Object arg0) {
		// TODO Auto-generated method stub
		return Integer.parseInt(arg0.toString());
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
