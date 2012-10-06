package de.upbracing.configurationeditor.timer.converters;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Converts a String to a double.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class DoubleDisplayToModelConverter implements IConverter {

	/**
	 * Converts {@code arg0} from String to double.
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object arg0) {
		// Just return the parsed double
		return Double.parseDouble(arg0.toString());
	}

	/**
	 * FromType is String.
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		// FromType could be String in this case. 
		return String.class;
	}

	/**
	 * ToType is double.
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		// ToType could be Double in this case. 
		return double.class;
	}

}
