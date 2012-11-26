package de.upbracing.code_generation.tests;

import java.util.ServiceLoader;

public class TestUILoader {
	private TestUILoader() { }
	
	public static Toolkit createBareToolkit(String ui_name, boolean is_error) {
		for (TestUI test_ui : ServiceLoader.load(TestUI.class)) {
			if (test_ui.getName().equals(ui_name)) {
				if (!test_ui.available()) {
					if (is_error)
						throw new RuntimeException("UI '" + ui_name + "' is not available.");
					else
						System.out.println("UI '" + ui_name + "' is not available.");
				} else
					return test_ui.createToolkit();
			}
		}
		
		if (is_error)
			throw new RuntimeException("no such UI: " + ui_name);
		else {
			System.out.println("WARN: unknown UI: " + ui_name);
			return null;
		}
	}

	public static Toolkit createBareToolkit(String... ui_names) {
		for (String ui_name : ui_names) {
			Toolkit toolkit = createBareToolkit(ui_name, false);
			if (toolkit != null)
				return toolkit;
		}

		throw new RuntimeException("no available UI could be found");
	}

	public static Toolkit createToolkit(String[] ui_names) {
		return new RichToolkit(createBareToolkit(ui_names));
	}
}
