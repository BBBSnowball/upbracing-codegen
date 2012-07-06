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
	 * @return the generated code
	 */
	String generate(de.upbracing.code_generation.config.MCUConfiguration config);
}
