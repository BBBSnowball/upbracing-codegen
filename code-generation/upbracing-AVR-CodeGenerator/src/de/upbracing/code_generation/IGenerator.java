package de.upbracing.code_generation;

import java.util.Map;

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
}
