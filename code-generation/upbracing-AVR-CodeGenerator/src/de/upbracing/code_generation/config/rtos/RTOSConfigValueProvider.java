package de.upbracing.code_generation.config.rtos;

import java.util.Collection;

public interface RTOSConfigValueProvider {
	/** return a collection of RTOS configuration values
	 * 
	 * Must return fresh value objects each time it is called.
	 * @return the list
	 */
	Collection<RTOSConfigValue> getRTOSConfigValues();
}
