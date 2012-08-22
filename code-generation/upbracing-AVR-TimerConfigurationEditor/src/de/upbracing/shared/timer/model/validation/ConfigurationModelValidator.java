package de.upbracing.shared.timer.model.validation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.upbracing.shared.timer.model.ConfigurationModel;

public class ConfigurationModelValidator {
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changes.addPropertyChangeListener(propertyName, listener);
	}
	
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changes.removePropertyChangeListener(propertyName, listener);
	}
	
	private ConfigurationModel model;
	
	public ConfigurationModelValidator(ConfigurationModel model) {
		this.model = model;
	}
	
	public ValidationResult getFrequencyError() {
		return ValidationResult.OK;
	}
	
	public String getFrequencyErrorText() {
		return "";
	}
	
	public void updateValidation() {
		
		// Very primitive, but will make the Data Binding work...
		changes.firePropertyChange("frequencyError", null, null);
		changes.firePropertyChange("frequencyErrorText", null, null);
	}
}
