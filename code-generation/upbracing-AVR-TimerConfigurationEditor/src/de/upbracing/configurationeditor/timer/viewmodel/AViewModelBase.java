package de.upbracing.configurationeditor.timer.viewmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AViewModelBase {
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changes.addPropertyChangeListener(propertyName, listener);
	}
	
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changes.removePropertyChangeListener(propertyName, listener);
	}
}
