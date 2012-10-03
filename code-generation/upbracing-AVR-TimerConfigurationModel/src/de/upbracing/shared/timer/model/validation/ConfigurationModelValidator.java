package de.upbracing.shared.timer.model.validation;

import de.upbracing.shared.timer.model.ConfigurationModel;

/**
 * This class validates a {@link ConfigurationModel}.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class ConfigurationModelValidator extends AValidatorBase {

	private ConfigurationModel model;
	
	/**
	 * Creates a new instance of the {@link ConfigurationModelValidator} class.
	 * @param model is the {@link ConfigurationModel} which is to be validated
	 */
	public ConfigurationModelValidator(ConfigurationModel model) {
		this.model = model;
	}
	
	/**
	 * Checks, whether the chosen frequency is valid for the AT90CAN128.
	 * @return {@link ValidationResult#ERROR}, if the frequency is below 1Hz or above 16MHz. 
	 * {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getFrequencyError() {
		if (model.getFrequency() < 1 || model.getFrequency() > 16000000)
			return ValidationResult.ERROR;
		return ValidationResult.OK;
	}
	
	/** 
	 * Gets the error message describing the frequency error.
	 * @return the created error message.
	 */
	public String getFrequencyErrorText() {
		if (getFrequencyError() == ValidationResult.ERROR)
			return "Freuqency must lie within the range of 1Hz and 16MHz.";
		return "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateValidation() {
		
		// Very primitive, but will make the Data Binding work...
		changes.firePropertyChange("frequencyError", null, null);
		changes.firePropertyChange("frequencyErrorText", null, null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValidationResult validate() {
		
		// Aggregate validation statuses (for now: only frequency)
		return getFrequencyError();
	}
}
