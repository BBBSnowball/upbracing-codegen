package de.upbracing.code_generation.config;

/** Key into the config store of an {@link CodeGeneratorConfigurations} object */
public class ConfigState<T> implements ReadableConfigState<T>, WritableConfigState<T> {
	private static int unique_counter = 0;
	
	private int id;
	private String name;
	
	public ConfigState(String name) {
		id = ++unique_counter;
	}
	
	public String getName() { return name; }
	
	public String toString() {
		return "ConfigState(" + name + ")@" + id;
	}
	
	/** returns a copy of this object that cannot be used to change the state */
	public ReadableConfigState<T> readonly() {
		return new ReadonlyConfigState();
	}
	
	private class ReadonlyConfigState implements ReadableConfigState<T> {
		public String toString() {
			return "ConfigState(readonly: " + name + ")@" + id;
		}

		@Override
		public int hashCode() {
			return ConfigState.this.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return ConfigState.this.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof ConfigState.ReadonlyConfigState)
			// This will end up in an equals call for the ConfigState objects.
			return obj.equals(this);
		if (getClass() != obj.getClass())
			return false;
		ConfigState<?> other = (ConfigState<?>) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
