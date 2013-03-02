package de.upbracing.code_generation.config;

import java.lang.reflect.Method;
import java.util.List;


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

	
	/** Due to some Java type quirks adding list state can be hard. This method
	 * is the same as addState, but without "unchecked" warnings in your code */
	<ITEM> void addListState(ConfigState<List<ITEM>> state);
	

	/** add a state change listener */
	<T> void addStateChangeListener(ReadableConfigState<T> state, StateChangeListener<T> ext);

	/** remove a state change listener */
	<T> void removeStateChangeListener(ReadableConfigState<T> state, StateChangeListener<T> ext);
}
