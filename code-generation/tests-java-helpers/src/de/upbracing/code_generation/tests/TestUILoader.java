package de.upbracing.code_generation.tests;

import java.util.ServiceLoader;

public class TestUILoader {
	private TestUILoader() { }
	
	public static Toolkit createBareToolkit(String ui_name) {
		for (TestUI test_ui : ServiceLoader.load(TestUI.class)) {
			if (test_ui.getName().equals(ui_name))
				return test_ui.createToolkit();
		}
		
		throw new RuntimeException("no such UI: " + ui_name);
	}

	public static Toolkit createToolkit(String ui_name) {
		return new RichToolkit(createBareToolkit(ui_name));
	}
}
