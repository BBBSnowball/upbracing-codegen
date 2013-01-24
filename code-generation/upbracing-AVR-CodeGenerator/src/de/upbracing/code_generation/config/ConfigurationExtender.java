package de.upbracing.code_generation.config;

import java.lang.reflect.Method;

/** Used by IConfigProvider to extend the configuration object */
public interface ConfigurationExtender {
	/** add a property that you can get and set by name (you must first add it as state) */
	<T> void addProperty(String name, ConfigState<T> state);
	
	/** add a read-only property that you can get by name (you must first add it as state) */
	void addReadonlyProperty(String name, ConfigState<?> state);
	
	/** add invisible state */
	<T> void addState(ConfigState<T> state, Class<T> cls);
	
	/** is this state registered with the configuration?
	 * 
	 * You can use this method to test whether your initialization has already
	 * been done. This will be useful, if dependent config providers call your
	 * initialization to make sure they may use your stuff.
	 * @return 
	 */
	boolean hasState(ReadableConfigState<?> state);
	
	/** add a method
	 * 
	 * NOTE: Method names must be unique - you cannot overload them.
	 * 
	 * @param method static method which can take an CodeGeneratorConfigurations as its first argument
	 */
	void addMethod(String name, Method method);
}
