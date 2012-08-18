package de.upbracing.shared.timer.model.validation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
		return ValidationResult.OK;
	}
	public ValidationResult getOcrAPeriodError() {
		if (!validateMaxPeriod(model.getOcrAPeriod()))
			return ValidationResult.ERROR;
		return ValidationResult.OK;
	}
	public ValidationResult getOcrBPeriodError() {
		if (!validateMaxPeriod(model.getOcrBPeriod()))
			return ValidationResult.ERROR;
		return ValidationResult.OK;
	}
	public ValidationResult getOcrCPeriodError() {
		if (!validateMaxPeriod(model.getOcrCPeriod()))
			return ValidationResult.ERROR;
		return ValidationResult.OK;
	}
	
	public String getIcrPeriodErrorText() {
		if (getIcrPeriodError() == ValidationResult.ERROR)
			return "The Top value is too high for this timer frequency.";
		if (getIcrPeriodError() == ValidationResult.WARNING)
			return "The Top value cannot be reached exactly. It will be quantized to ...";
		return "";
	}
	public String getOcrAPeriodErrorText() {
		if (getOcrAPeriodError() == ValidationResult.ERROR)
			return "The desired period would result in a value too high for this register.";
		if (getOcrAPeriodError() == ValidationResult.WARNING)
			return "The desired period cannot be reached exactly. It will be quantized to ...";
		return "";
	}
	public String getOcrBPeriodErrorText() {
		if (getOcrBPeriodError() == ValidationResult.ERROR)
			return "The desired period would result in a value too high for this register.";
		if (getOcrBPeriodError() == ValidationResult.WARNING)
			return "The desired period cannot be reached exactly. It will be quantized to ...";
		return "";
	}
	public String getOcrCPeriodErrorText() {
		if (getOcrCPeriodError() == ValidationResult.ERROR)
			return "The desired period would result in a value too high for this register.";
		if (getOcrCPeriodError() == ValidationResult.WARNING)
			return "The desired period cannot be reached exactly. It will be quantized to ...";
		return "";
	}
	
	public void updateValidation() {
		
		// Very primitive, but will make the databinding work...
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
		int frequency = parent.getFrequency();
		int maxValue = getMaximumValue();
		// Get Timer Tick Rate
		int prescale = model.getPrescale().getNumeric();
		double timerFreq = ((double) frequency / (double) prescale);
		double timerPeriod = 1.0 / timerFreq;
		// Calculate Register Value From Desired Period
		double registerValue = period / timerPeriod;
		if (registerValue > maxValue)
			return false;
		return true;
	}
	
	private int getMaximumValue() {
		int maxValue = 255;
		if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3))
			maxValue = 65535;
		return maxValue;
	}
}
