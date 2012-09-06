package de.upbracing.shared.timer.model.validation;

import de.upbracing.shared.timer.model.ConfigurationModel;

public class ConfigurationModelValidator extends AValidatorBase {

	private ConfigurationModel model;
	
	public ConfigurationModelValidator(ConfigurationModel model) {
		this.model = model;
	}
	
	public ValidationResult getFrequencyError() {
		if (model.getFrequency() < 1000000 || model.getFrequency() > 16000000)
			return ValidationResult.ERROR;
		return ValidationResult.OK;
	}
	
	public String getFrequencyErrorText() {
		if (getFrequencyError() == ValidationResult.ERROR)
			return "Freuqency must lie within the range of 1MHz and 16MHz.";
		return "";
	}
	
	public void updateValidation() {
		
		// Very primitive, but will make the Data Binding work...
		changes.firePropertyChange("frequencyError", null, null);
		changes.firePropertyChange("frequencyErrorText", null, null);
	}
}
