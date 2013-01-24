package de.upbracing.code_generation.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.Set;

import org.simpleframework.xml.Default;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.Severity;

/**
 * main configuration class
 * 
 * You can reach all configuration objects from this class.
 * 
 * @author benny
 */
@Default(required=false)
public class CodeGeneratorConfigurations {
	/** all methods that extend the configuration synchronize on this object */
	private static Object extension_sync_root = new Object();
	
	/** information about a state and/or property */
	private static class ExtState<T> {
		public boolean gettable = false, settable = false;
		public String name = null;
		public ConfigState<T> config_state;
		public Class<T> write_type;
		public DefaultValueProvider<T> default_value = null;
		
		public ExtState(ConfigState<T> config_state, Class<T> cls) {
			this.config_state = config_state;
			this.write_type = cls;
		}

		public void reportToListener(ConfigurationExtender ext) {
			ext.addState(config_state, write_type);
			
			if (settable)
				ext.addProperty(name, config_state);
			else if (gettable)
				ext.addReadonlyProperty(name, config_state);
		}
	}
	
	private static Map<ConfigState<?>, ExtState<?>> states = new HashMap<ConfigState<?>, ExtState<?>>();
	private static Map<String, ExtState<?>> properties = new HashMap<String, ExtState<?>>();
	private static Map<String, Method> methods = new HashMap<String, Method>();
	private static List<ConfigurationExtender> config_extension_listeners = new LinkedList<ConfigurationExtender>();
	
	private Map<ConfigState<?>, Object> state_values = new HashMap<ConfigState<?>, Object>();

	private Messages messages;
	
	public CodeGeneratorConfigurations() {
		// load config providers (in case the classpath has been extended)
		//TODO Is that possible in Java? (in JRuby it is...)
		loadConfigProviders();
		
		// create an instance of messages
		messages = new Messages().withOutputTo(System.err, Severity.INFO);

		// initialize this object
		for (IConfigProvider provider : ServiceLoader.load(IConfigProvider.class)) {
			provider.initConfiguration(this);
			provider.addFormatters(messages);
		}
	}

	/**
	 * Get object that is used to report messages in validate and updateConfig
	 * @return the messages object
	 */
	public Messages getMessages() {
		return messages;
	}
	
	/**
	 * Set object that is used to report messages in validate and updateConfig
	 * @param messages the new value
	 */
	public void setMessages(Messages messages) {
		this.messages = messages;

		for (IConfigProvider provider : ServiceLoader.load(IConfigProvider.class)) {
			provider.addFormatters(messages);
		}
	}
	
	private static ExtState<?> getExtState(String name) {
		ExtState<?> x = properties.get(name);
		
		if (x == null)
			throw new IllegalArgumentException("no such property: " + name);
		
		return x;
	}

	@SuppressWarnings("unchecked")
	private static <T> ExtState<T> getExtState(ReadableConfigState<T> state) {
		ExtState<T> x = (ExtState<T>)states.get(state);
		
		if (x == null)
			throw new IllegalArgumentException("state not registered: " + state);
		
		return x;
	}

	@SuppressWarnings("unchecked")
	private static <T> ExtState<T> getExtState(WritableConfigState<?> state) {
		ExtState<T> x = (ExtState<T>)states.get(state);
		
		if (x == null)
			throw new IllegalArgumentException("state not registered: " + state);
		
		return x;
	}

	private static <T> ExtState<T> getExtState(ConfigState<T> state) {
		return getExtState((WritableConfigState<T>)state);
	}

	@SuppressWarnings("unchecked")
	private <T> T getState(ExtState<T> x) {
		if (state_values.containsKey(x.config_state))
			return (T)state_values.get(x.config_state);
		else if (x.default_value != null)
			return x.default_value.getDefaultValue(this, (ConfigState<T>)x.config_state);
		else
			return null;
	}
	
	private <T> void setState(ExtState<T> x, Object new_value) {
		if (x.write_type == null)
			throw new RuntimeException("not writable");
		
		if (! x.write_type.isAssignableFrom(new_value.getClass()))
			throw new IllegalArgumentException("invalid type");
		
		state_values.put(x.config_state, new_value);
	}
	
	
	/** get current state of a state variable */
	public <T> T getState(ReadableConfigState<T> state) {
		// make sure it is registered
		ExtState<T> x = getExtState(state);
		
		return getState(x);
	}

	/** set current state of a state variable */
	public <T> void setState(WritableConfigState<T> state, T new_value) {
		// make sure it is registered
		ExtState<T> x = getExtState(state);
		
		setState(x, new_value);
	}

	/** get current value of a property */
	public Object getProperty(String name) {
		ExtState<?> x = getExtState(name);
		
		if (!x.gettable)
			throw new IllegalAccessError("property isn't readable");
		
		return getState(x);
	}

	/** set current value of a property */
	public void setProperty(String name, Object value) {
		ExtState<?> x = getExtState(name);
		
		if (!x.settable)
			throw new IllegalAccessError("property isn't writable");
		
		setState(x, value);
	}
	
	/** type of the Object you should pass as 'value' argument to {@link #setProperty} */
	public Class<?> getPropertyWriteType(String name) {
		ExtState<?> x = getExtState(name);
		
		if (!x.settable)
			throw new IllegalAccessError("property isn't writable");
		
		return x.write_type;
	}
	
	/** call an extension method 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException if the method throws an Exception
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException */
	public Object call(String name, Object... args) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method method = methods.get(name);
		
		if (method == null)
			throw new NoSuchMethodException(name);
		
		// We have to add this object as the first argument.
		Object args2[] = new Object[args.length+1];
		args2[0] = this;
		for (int i=0;i<args.length;i++)
			args2[i+1] = args[i];
		
		return method.invoke(null, args2);
	}
	
	/** listen for config extensions
	 * 
	 * The listener will be called for all existing extensions.
	 * 
	 * @param ext the listener
	 */
	public static void addExtensionListener(ConfigurationExtender ext) {
		synchronized (extension_sync_root) {
			for (ExtState<?> state : states.values())
				state.reportToListener(ext);
			
			for (Entry<String, Method> x : methods.entrySet())
				ext.addMethod(x.getKey(), x.getValue());
			
			config_extension_listeners.add(ext);
		}
	}
	
	public static void removeExtensionListener(ConfigurationExtender ext) {
		synchronized (extension_sync_root) {
			config_extension_listeners.remove(ext);
		}
	}
	
	private static class Extender implements RichConfigurationExtender {
		public static final Extender instance = new Extender();

		private void addProperty(String name, ConfigState<?> state, boolean writable) {
			synchronized (extension_sync_root) {
				if (properties.containsKey(name))
					throw new IllegalArgumentException("There already is a property with this name: " + name);
				
				ExtState<?> x = getExtState(state);
				
				// We may have more than one name, if we have aliases. We
				// ignore all put the first one.
				//NOTE We may extend the access of the other aliases, but
				//     they could be accessed by the alias, so we don't care.
				if (x.name == null)
					x.name = name;
				
				x.gettable = true;
				x.settable = writable;
				
				properties.put(name, x);
			}
		}

		@Override
		public <T> void addProperty(String name, ConfigState<T> state) {
			boolean writable = true;
			addProperty(name, state, writable);
			
			for (ConfigurationExtender listener : config_extension_listeners)
				listener.addProperty(name, state);
		}

		@Override
		public void addReadonlyProperty(String name, ConfigState<?> state) {
			boolean writable = false;
			addProperty(name, state, writable);
			
			for (ConfigurationExtender listener : config_extension_listeners)
				listener.addReadonlyProperty(name, state);
		}

		@Override
		public <T> void addState(ConfigState<T> state, Class<T> cls) {
			synchronized (extension_sync_root) {
				if (states.containsKey(state))
					throw new IllegalStateException("You cannot register a state more than once.");
				
				states.put(state, new ExtState<T>(state, cls));
			}
			
			for (ConfigurationExtender listener : config_extension_listeners)
				listener.addState(state, cls);
		}

		@Override
		public void addMethod(String name, Method method) {
			synchronized (extension_sync_root) {
				if (methods.containsKey(name))
					throw new IllegalArgumentException("There already is a method with this name: " + name);
				
				if ((method.getModifiers() & Modifier.STATIC) == 0)
					throw new IllegalArgumentException("Only static methods are allowed");
				
				methods.put(name, method);
			}
			
			for (ConfigurationExtender listener : config_extension_listeners)
				listener.addMethod(name, method);
		}

		@Override
		public <T> void initDefaultValue(ConfigState<T> state,
				DefaultValueProvider<T> default_value) {
			synchronized (extension_sync_root) {
				ExtState<T> x = getExtState(state);
				
				if (default_value == null)
					throw new IllegalArgumentException("default_value cannot be null");
				
				if (x.default_value != null)
					throw new IllegalStateException("You cannot change the default value (only set it once)");
				
				x.default_value = default_value;
			}
		}

		@Override
		public void addMethod(Method method) {
			addMethod(method.getName(), method);
		}

		@Override
		public void addMethods(Class<?> cls) {
			for (Method method : cls.getMethods()) {
				ConfigurationMethod annot = method.getAnnotation(ConfigurationMethod.class);
				
				if (annot != null) {
					String name = annot.name();
					if (name == null || name.isEmpty())
						name = method.getName();
					
					//NOTE If the user puts the annotation on a non-static method,
					//     addMethod will throw an Exception. This is intended.
					addMethod(name, method);
				}
			}
		}
	}
	
	private static Set<Class<?>> done_config_providers = new HashSet<Class<?>>();
	
	public static void loadConfigProviders() {
		for (IConfigProvider provider : ServiceLoader.load(IConfigProvider.class)) {
			if (done_config_providers.contains(provider.getClass()))
				continue;
			done_config_providers.add(provider.getClass());
			
			provider.extendConfiguration(Extender.instance);
		}
	}
	
	static {
		loadConfigProviders();
	}
}
