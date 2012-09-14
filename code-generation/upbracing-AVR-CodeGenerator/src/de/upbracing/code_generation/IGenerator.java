package de.upbracing.code_generation;

import java.util.Map;

import de.upbracing.code_generation.config.MCUConfiguration;

/**
 * Interface for a code generator
 * @author benny
 */
public interface IGenerator {
	/**
	 * Get the name of the generator
	 * @return the name
	 */
	String getName();
	
	/**
	 * Get information about the generated files
	 * @return a map with pairs of filename and template
	 */
	Map<String, ITemplate> getFiles();
	
	/**
	 * Should a file be generated?
	 * @return true, if the file should be generated
	 */
	boolean isTemplateActive(String filename, ITemplate template,
			MCUConfiguration config);
	
	/**
	 * Validate the configuration
	 * 
	 * @param config the configuration
	 * @param after_update_config true, if validate should assume that updateConfig has been run
	 * @param generator_data data returned by updateConfig or null
	 * @return whether the configuration is valid
	 */
	boolean validate(MCUConfiguration config, boolean after_update_config, Object generator_data);
	
	/**
	 * Get all generators that are used by this generator
	 * 
	 * The updateConfig method may change the configuration of those
	 * generators. Therefore, updateConfig for this generator must be
	 * run before updateConfig is run for the generators in the list.
	 * 
	 * @return list of used generators
	 */
	Iterable<Class<IGenerator>> getUsedGenerators();
	
	/**
	 * Prepare the configuration for code generation
	 * @param config the configuration
	 * @return data that will be passed into the template (generator_data)
	 */
	Object updateConfig(MCUConfiguration config);
}
