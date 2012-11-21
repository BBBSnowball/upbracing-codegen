package de.upbracing.code_generation.tests.ncurses;

import de.upbracing.code_generation.tests.TestUI;
import de.upbracing.code_generation.tests.Toolkit;

public class NCursesUI implements TestUI {
	private static TestUI ruby_testui = null;
	
	public static void setRubyUI(TestUI testUI) {
		if (ruby_testui != null)
			throw new IllegalStateException("RubyUI has already been set");
		ruby_testui = testUI;
	}

	@Override
	public String getName() {
		return "ncurses";
	}

	@Override
	public boolean available() {
		return ruby_testui != null && ruby_testui.available();
	}

	@Override
	public Toolkit createToolkit() {
		return ruby_testui.createToolkit();
	}

}
