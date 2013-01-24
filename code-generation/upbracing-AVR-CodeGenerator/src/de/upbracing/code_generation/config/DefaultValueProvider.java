package de.upbracing.code_generation.config;

public interface DefaultValueProvider<T> {
	T getDefaultValue(CodeGeneratorConfigurations config, ReadableConfigState<T> state);
}
