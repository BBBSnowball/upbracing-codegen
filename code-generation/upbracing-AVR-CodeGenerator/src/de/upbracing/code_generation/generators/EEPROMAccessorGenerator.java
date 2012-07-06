package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.EepromTemplate;

public class EEPROMAccessorGenerator extends AbstractGenerator {
	public EEPROMAccessorGenerator() {
		super("eeprom_accessors.h", new EepromTemplate());
	}
}
