package de.upbracing.configurationeditor.timer.converters;

import java.text.DecimalFormat;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Converts a double to a String.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class DoubleModelToDisplayConverter implements IConverter {

	/**
	 * Converts {@code arg0} from double to String.
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object arg0) {
		if (arg0.getClass() == Double.class) {
			DecimalFormat f = new DecimalFormat("###.##########");
			return f.format((Double)arg0).replace(',', '.');
		}
		return null;
	}

	/**
	 * FromType is double.
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		// FromType could be Double in this case. 
		return double.class;
	}

	/**
	 * ToType is String.
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		// ToType could be String in this case. 
		return String.class;
	}

}
