package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.CommonHeaderTemplate;

public class CommonHeaderGenerator extends AbstractGenerator {
	public CommonHeaderGenerator() {
		super("common.h", new CommonHeaderTemplate());
	}
}
