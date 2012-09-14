package de.upbracing.code_generation;

/**
 * A code generation template. Those classes are generated from JET scripts.
 * 
 * @author benny
 */
public interface ITemplate {
	/**
	 * Execute the template
	 * @param config the configuration object
	 * @param data that has been returned by the corresponding IGenerator.updateConfig method
	 * @return the generated code
	 */
	String generate(de.upbracing.code_generation.config.MCUConfiguration config, Object generator_data);
}
