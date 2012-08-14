package de.upbracing.configurationeditor.timer.editors;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;

public class IntegerValidator implements IValidator {

	private final ControlDecoration decoration;
	private final int min;
	private final int max;
	
	public IntegerValidator(ControlDecoration d, int min, int max) {
		decoration = d;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public IStatus validate(Object arg0) {
		Integer value = -1;
		try {
			value = (Integer) arg0;
		}
		catch (Exception e) {
			String errorText = "Value may only contain digits.";
			decoration.setDescriptionText(errorText);
			decoration.show();
			return ValidationStatus.error(errorText);
		}
		if (value < min) {
			String errorText = "Value must be larger than " + min + ".";
			decoration.setDescriptionText(errorText);
			decoration.show();
			return ValidationStatus.error(errorText);
		}
		if (value > max) {
			String errorText = "Value must be smaller than " + max + ".";
			decoration.setDescriptionText(errorText);
			decoration.show();
			return ValidationStatus.error(errorText);
		}
		decoration.hide();
		decoration.setDescriptionText(null);
		return ValidationStatus.ok();
	}

}
