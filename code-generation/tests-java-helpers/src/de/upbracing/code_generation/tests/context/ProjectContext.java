package de.upbracing.code_generation.tests.context;

public class ProjectContext extends TestContext {
	private String path;

	public ProjectContext(String name, String path) {
		super(name);
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
}
