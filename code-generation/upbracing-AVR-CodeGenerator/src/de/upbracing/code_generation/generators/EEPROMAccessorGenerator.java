package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.EepromCFileTemplate;
import de.upbracing.code_generation.EepromHeaderTemplate;

/**
 * Generator for EEPROM accessors
 * 
 * @author benny
 */
public class EEPROMAccessorGenerator extends AbstractGenerator {
	public EEPROMAccessorGenerator() {
		super("eeprom_accessors.h", new EepromHeaderTemplate(),
				"eeprom_accessors.c", new EepromCFileTemplate());
	}
}
