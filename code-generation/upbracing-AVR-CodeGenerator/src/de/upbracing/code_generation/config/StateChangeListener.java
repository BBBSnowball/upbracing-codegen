package de.upbracing.code_generation.config;

public interface StateChangeListener<T> {
	void stateChanged(CodeGeneratorConfigurations config,
			ConfigState<T> state, T old_value, T new_value);
}
