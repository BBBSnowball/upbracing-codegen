package de.upbracing.configurationeditor.timer.converters;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Converts a int to a String.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class IntegerModelToDisplayConverter implements IConverter {

	/**
	 * Converts {@code arg0} from int to String.
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object arg0) {
		// Remove Decimal Points
		if (arg0.getClass() == Integer.class) {
			return arg0.toString().replace(".", "").replace(",", "");
		}
		return null;
	}

	/**
	 * FromType is int.
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		// FromType could be Integer in this case. 
		// Returning null here makes this accepting any target type.
		return int.class;
	}

	/**
	 * ToType is String.
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		// ToType could be String in this case. 
		// Returning null here makes this accepting any source type.
		return String.class;
	}

}
