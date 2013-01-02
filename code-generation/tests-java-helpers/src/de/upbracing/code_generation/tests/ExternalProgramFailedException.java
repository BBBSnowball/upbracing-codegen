package de.upbracing.code_generation.tests;

import java.io.File;

import de.upbracing.code_generation.tests.context.Result;

@SuppressWarnings("serial")
public class ExternalProgramFailedException extends TestFailedException {
	private String name;
	private String[] commandline;
	private String[] environment;
	private File dir;
	private int exit_code;

	public ExternalProgramFailedException(int exit_code, String message) {
		super(message);
		this.exit_code = exit_code;
	}

	public ExternalProgramFailedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ExternalProgramFailedException(String name, int exit_code, String[] commandline, String[] environment, File dir) {
		super(new Result.Error("program '" + (name != null ? name : commandline[0]) + "' failed with exit code " + exit_code));
		
		this.name = name;
		this.exit_code = exit_code;
		this.commandline = commandline;
		this.environment = environment;
		this.dir = dir;
	}
	
	public int getExitCode() {
		return exit_code;
	}

	public String getName() {
		return name;
	}

	public String[] getCommandline() {
		return commandline;
	}

	public String[] getEnvironment() {
		return environment;
	}

	public File getDir() {
		return dir;
	}
}
