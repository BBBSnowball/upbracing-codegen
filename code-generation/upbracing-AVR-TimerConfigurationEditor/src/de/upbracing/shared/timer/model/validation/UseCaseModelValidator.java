package de.upbracing.shared.timer.model.validation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;

import de.upbracing.shared.timer.model.ConfigurationModel;
import de.upbracing.shared.timer.model.UseCaseModel;
import de.upbracing.shared.timer.model.enums.TimerEnum;

public class UseCaseModelValidator {

	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changes.addPropertyChangeListener(propertyName, listener);
	}
	
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changes.removePropertyChangeListener(propertyName, listener);
	}
	
	private ConfigurationModel parent;
	private UseCaseModel model;
	
	public UseCaseModelValidator(ConfigurationModel parent, UseCaseModel model) {
		this.model = model;
		this.parent = parent;
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
	
	public void updateValidation() {
		
		// Very primitive, but will make the Data Binding work...
		changes.firePropertyChange("icrPeriodError", null, null);
		changes.firePropertyChange("icrPeriodErrorText", null, null);
		changes.firePropertyChange("ocrAPeriodError", null, null);
		changes.firePropertyChange("ocrAPeriodErrorText", null, null);
		changes.firePropertyChange("ocrBPeriodError", null, null);
		changes.firePropertyChange("ocrBPeriodErrorText", null, null);
		changes.firePropertyChange("ocrCPeriodError", null, null);
		changes.firePropertyChange("ocrCPeriodErrorText", null, null);
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
		int registerValue = (int) (period / timerPeriod) - 1;
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
			return String.format("%4.10f", period) + "s";
		else if (period > 1e-3)
			return f.format(period * 1e3) + " ms";
		else if (period > 1e-6)
			return f.format(period * 1e6) + " �s";
		else
			return f.format(period * 1e9) + " ns";
	}
}
