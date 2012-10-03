package de.upbracing.shared.timer.model.validation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This is the abstract base class for {@link ConfigurationModelValidator} and {@link UseCaseModelValidator}.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public abstract class AValidatorBase {
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
	
	/**
	 * Validates the whole model at once and returns the most severe <code>ValidationResult</code>.
	 * @return the most severe error during validation.
	 */
	public abstract ValidationResult validate();
	
	/**
	 * This method fires property change events for each validation property. This approach might not
	 * be the most efficient solution, but it guarantees, that no value is skipped during validation.
	 */
	public abstract void updateValidation();
}
