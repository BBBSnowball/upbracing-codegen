package de.upbracing.code_generation.config;

import java.lang.reflect.Method;


public interface RichConfigurationExtender extends ConfigurationExtender {
	/** add all static methods of that class that have a @ConfigurationMethod annotation */
	void addMethods(Class<?> cls);
	
	/** set the default value provider (you can only do that once) */
	<T> void initDefaultValue(ConfigState<T> state, DefaultValueProvider<T> default_value);
	
	/** add a method
	 * 
	 * NOTE: Method names must be unique - you cannot overload them.
	 * 
	 * @param method static method which can take an CodeGeneratorConfigurations as its first argument
	 */
	void addMethod(Method method);
	

	/** add a state change listener */
	<T> void addStateChangeListener(ReadableConfigState<T> state, StateChangeListener<T> ext);

	/** remove a state change listener */
	<T> void removeStateChangeListener(ReadableConfigState<T> state, StateChangeListener<T> ext);
	
}
