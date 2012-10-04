package de.upbracing.configurationeditor.timer.viewmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This is the abstract base class for {@link ConfigurationViewModel} and {@link UseCaseViewModel}.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public abstract class AViewModelBase {
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	/**
	 * This method routes the addPropertyChangeListener operation.
	 * See {@link java.beans.PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener) PropertyChangeSupport.addPropertyChangeListener(...)} for details.
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changes.addPropertyChangeListener(propertyName, listener);
	}
	
	/**
	 * This method routes the removePropertyChangeListener operation.
	 * See {@link java.beans.PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener) PropertyChangeSupport.removePropertyChangeListener(...)} for details.
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changes.removePropertyChangeListener(propertyName, listener);
	}
}
