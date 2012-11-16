package de.upbracing.code_generation.tests.simple;

import de.upbracing.code_generation.tests.TestUI;
import de.upbracing.code_generation.tests.Toolkit;

public class SimpleUI implements TestUI {

	@Override
	public String getName() {
		return "simple";
	}

	@Override
	public boolean available() {
		return true;
	}

	@Override
	public Toolkit createToolkit() {
		return new SimpleToolkit();
	}

}
