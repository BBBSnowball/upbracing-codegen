package de.upbracing.shared.timer.model.validation;

import java.text.DecimalFormat;

import de.upbracing.shared.timer.model.ConfigurationModel;
import de.upbracing.shared.timer.model.UseCaseModel;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;

public class UseCaseModelValidator extends AValidatorBase {

	private ConfigurationModel parent;
	private UseCaseModel model;
	
	public UseCaseModelValidator(ConfigurationModel parent, UseCaseModel model) {
		this.model = model;
		this.parent = parent;
	}
	
	public ValidationResult getNameError() {
		
		// Check for name collisions
		if (isNameColliding())
			return ValidationResult.ERROR;
		
		// Check for valid C identifier
		if (!isNameValidCIdentifier())
			return ValidationResult.ERROR;
		
		return ValidationResult.OK;
	}
	public ValidationResult getIcrPeriodError() {
		if (!validateMaxPeriod(model.getIcrPeriod()))
			return ValidationResult.ERROR;
		double quantizedPeriod = calculateQuantizedPeriod(model.getIcrPeriod());
		if (quantizedPeriod != model.getIcrPeriod())
			return ValidationResult.WARNING;
		return ValidationResult.OK;
	}
	public ValidationResult getOcrAPeriodError() {
		if (!validateMaxPeriod(model.getOcrAPeriod()))
			return ValidationResult.ERROR;
		double quantizedPeriod = calculateQuantizedPeriod(model.getOcrAPeriod());
		if (quantizedPeriod != model.getOcrAPeriod())
			return ValidationResult.WARNING;
		return ValidationResult.OK;
	}
	public ValidationResult getOcrBPeriodError() {
		if (!validateMaxPeriod(model.getOcrBPeriod()))
			return ValidationResult.ERROR;
		double quantizedPeriod = calculateQuantizedPeriod(model.getOcrBPeriod());
		if (quantizedPeriod != model.getOcrBPeriod())
			return ValidationResult.WARNING;
		return ValidationResult.OK;
	}
	public ValidationResult getOcrCPeriodError() {
		if (!validateMaxPeriod(model.getOcrCPeriod()))
			return ValidationResult.ERROR;
		double quantizedPeriod = calculateQuantizedPeriod(model.getOcrCPeriod());
		if (quantizedPeriod != model.getOcrCPeriod())
			return ValidationResult.WARNING;
		return ValidationResult.OK;
	}
	
	public ValidationResult getCtcTopError() {
		return ValidationResult.OK;
	}
	public ValidationResult getFastPWMTopError() {
		return ValidationResult.OK;
	}
	public ValidationResult getPhaseCorrectPWMTopError() {
		return ValidationResult.OK;
	}
	public ValidationResult getPhaseAndFrequencyCorrectPWMTopError() {
		return ValidationResult.OK;
	}
	public ValidationResult getModeError() {
		
		// Check Mode and Timer collisions
		if (!isModeValidForTimer())
			return ValidationResult.ERROR;
		
		return ValidationResult.OK;
	}
	public ValidationResult getTimerError() {
		
		// Check Mode and Timer collisions
		if (!isModeValidForTimer())
			return ValidationResult.ERROR;
		
		return ValidationResult.OK;
	}
	
	public String getNameErrorText() {
		// Check for empty name
		if (model.getName().equals(""))
			return "Please choose a name for this configuration.";
		
		// Check for name collisions
		if (isNameColliding())
			return "Each configuration must have a unique name.\nPlease choose another name.";
		
		// Check for valid C identifier
		if (!isNameValidCIdentifier())
			return "The name of this configuration is not a valid C identifier prefix. Only letters, underscores and digits are allowed.\nName may not start with a digit.";
		
		return "";
	}
	public String getIcrPeriodErrorText() {
		if (getIcrPeriodError() == ValidationResult.ERROR)
			return "The Top value is too high for this timer frequency (" + 
				calculateRegisterValue(model.getIcrPeriod()) + " > " + getMaximumValue() +
				", max. period is " + formatPeriod(calculatePeriodForRegisterValue(getMaximumValue())) + ").";
		if (getIcrPeriodError() == ValidationResult.WARNING)
			return "The Top value " + formatPeriod(model.getIcrPeriod()) + " cannot be reached exactly. It will be quantized to " + formatPeriod(calculateQuantizedPeriod(model.getIcrPeriod())) + ".";
		return "";
	}
	public String getOcrAPeriodErrorText() {
		if (getOcrAPeriodError() == ValidationResult.ERROR)
			return "The desired period would result in a value too high for this register (" + 
				calculateRegisterValue(model.getOcrAPeriod()) + " > " + getMaximumValue() +
				", max. period is " + formatPeriod(calculatePeriodForRegisterValue(getMaximumValue())) + ").";
		if (getOcrAPeriodError() == ValidationResult.WARNING)
			return "The desired period " + formatPeriod(model.getOcrAPeriod()) + " cannot be reached exactly. It will be quantized to " + formatPeriod(calculateQuantizedPeriod(model.getOcrAPeriod())) + ".";
		return "";
	}
	public String getOcrBPeriodErrorText() {
		if (getOcrBPeriodError() == ValidationResult.ERROR)
			return "The desired period would result in a value too high for this register (" + 
				calculateRegisterValue(model.getOcrBPeriod()) + " > " + getMaximumValue() +
				", max. period is " + formatPeriod(calculatePeriodForRegisterValue(getMaximumValue())) + ").";
		if (getOcrBPeriodError() == ValidationResult.WARNING)
			return "The desired period " + formatPeriod(model.getOcrBPeriod()) + " cannot be reached exactly. It will be quantized to " + formatPeriod(calculateQuantizedPeriod(model.getOcrBPeriod())) + ".";
		return "";
	}
	public String getOcrCPeriodErrorText() {
		if (getOcrCPeriodError() == ValidationResult.ERROR)
			return "The desired period would result in a value too high for this register (" + 
				calculateRegisterValue(model.getOcrCPeriod()) + " > " + getMaximumValue() +
				", max. period is " + formatPeriod(calculatePeriodForRegisterValue(getMaximumValue())) + ").";
		if (getOcrCPeriodError() == ValidationResult.WARNING)
			return "The desired period " + formatPeriod(model.getOcrCPeriod()) + " cannot be reached exactly. It will be quantized to " + formatPeriod(calculateQuantizedPeriod(model.getOcrCPeriod())) + ".";
		return "";
	}
	
	public String getCtcTopErrorText() {
		return "All ok!";
	}
	public String getFastPWMTopErrorText() {
		return "All ok!";
	}
	public String getPhaseCorrectPWMTopErrorText() {
		return "All ok!";
	}
	public String getPhaseAndFrequencyCorrectPWMTopErrorText() {
		return "All ok!";
	}
	public String getModeErrorText() {
		// Check Mode and Timer collisions
		if (!isModeValidForTimer())
			return "This mode is not valid for 8Bit timers. Please choose another mode and/or timer.";
		
		return "";
	}
	public String getTimerErrorText() {
		// Check Mode and Timer collisions
		if (!isModeValidForTimer())
			return "This mode is not valid for 8Bit timers. Please choose another mode and/or timer.";
		
		return "";
	}
	
	public void updateValidation() {
		
		// Very primitive, but will make the Data Binding work...
		changes.firePropertyChange("nameError", null, null);
		changes.firePropertyChange("nameErrorText", null, null);
		changes.firePropertyChange("icrPeriodError", null, null);
		changes.firePropertyChange("icrPeriodErrorText", null, null);
		changes.firePropertyChange("ocrAPeriodError", null, null);
		changes.firePropertyChange("ocrAPeriodErrorText", null, null);
		changes.firePropertyChange("ocrBPeriodError", null, null);
		changes.firePropertyChange("ocrBPeriodErrorText", null, null);
		changes.firePropertyChange("ocrCPeriodError", null, null);
		changes.firePropertyChange("ocrCPeriodErrorText", null, null);
		changes.firePropertyChange("ctcTopError", null, null);
		changes.firePropertyChange("ctcTopErrorText", null, null);
		changes.firePropertyChange("fastPWMTopError", null, null);
		changes.firePropertyChange("fastPWMTopErrorText", null, null);
		changes.firePropertyChange("phaseCorrectPWMTopError", null, null);
		changes.firePropertyChange("phaseCorrectPWMTopErrorText", null, null);
		changes.firePropertyChange("phaseAndFrequencyCorrectPWMTopError", null, null);
		changes.firePropertyChange("phaseAndFrequencyCorrectPWMTopErrorText", null, null);
		changes.firePropertyChange("modeError", null, null);
		changes.firePropertyChange("modeErrorText", null, null);
		changes.firePropertyChange("timerError", null, null);
		changes.firePropertyChange("timerErrorText", null, null);
	}
	
	private boolean validateMaxPeriod(double period) {
		// Get Maximum Register Value
		int maxValue = getMaximumValue();
		double registerValue = calculateRegisterValue(period);
		if (Math.round(registerValue) > maxValue)
			return false;
		return true;
	}
	
	private int calculateRegisterValue(double period) {
		int frequency = parent.getFrequency();
		// Get Timer Tick Rate
		int prescale = model.getPrescale().getNumeric();
		double timerFreq = ((double) frequency / (double) prescale);
		double timerPeriod = 1.0 / timerFreq;
		// Calculate Register Value From Desired Period
		int registerValue = (int) Math.round(period / timerPeriod) - 1;
		return registerValue;
	}
	
	private double calculatePeriodForRegisterValue(int registerValue) {
		// registerValue = period / timerPeriod
		//               = period * timerFreq
		//               = period * (frequency / prescale)
		// <=> period = registerValue / (frequency / prescale)
		
		int frequency = parent.getFrequency();
		int prescale = model.getPrescale().getNumeric();
		double timerFreq = ((double) frequency / (double) prescale);
		
		return (registerValue + 1) / timerFreq;
	}
	
	private double calculateQuantizedPeriod(double period) {
		int regValue = calculateRegisterValue(period);
		return calculatePeriodForRegisterValue(regValue);
	}
	
	private int getMaximumValue() {
		int maxValue = 255;
		if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3))
			maxValue = 65535;
		return maxValue;
	}
	
	private static String formatPeriod(double period) {
		DecimalFormat f = new DecimalFormat("###.##########");
		if (period > 1)
			return f.format(period) + "s";
		else if (period > 1e-3)
			return f.format(period * 1e3) + " ms";
		else if (period > 1e-6)
			return f.format(period * 1e6) + " µs";
		else
			return f.format(period * 1e9) + " ns";
	}
	
	private boolean isNameColliding() {
		// Check for name collisions
		for (UseCaseModel m: parent.getConfigurations()) {
			if (m != model && m.getName().equals(model.getName()))
				return true;
		}
		return false;
	}
	
	private boolean isNameValidCIdentifier() {
		// Check for valid C identifier
		// -> Only digits and letters + underscore
		// -> May not start with digit
		String regex = "^([_A-Za-z][_A-Za-z0-9]*)";
		if (model.getName().matches(regex))
			return true;
		return false;
	}
	
	private boolean isModeValidForTimer() {
		
		boolean isSixteenBit = false;
		if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3))
			isSixteenBit = true;
		
		// Invalid, if Phase and Frequency Correct Mode on 8Bit Timer!
		if (!isSixteenBit && model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT))
			return false;
		
		return true;
	}
}
