package de.upbracing.code_generation.config;

import de.upbracing.code_generation.Messages;


/** Enhances the MCU configuration object, e.g. to add
 * state and methods for a code generator.
 *
 * IConfigProviders are created more than once, so you
 * shouldn't do any expensive actions during object
 * creation (in the constructor).
 */
public interface IConfigProvider {
	/** add configuration extensions (using the ConfigurationExtender that is passed as its argument) */
	void extendConfiguration(RichConfigurationExtender ext);
	
	/** initialize a configuration object (e.g. set initial values) */
	void initConfiguration(CodeGeneratorConfigurations config);
	
	/** add formatters to the message object */
	void addFormatters(Messages messages);
}
