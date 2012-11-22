package de.upbracing.code_generation.tests;

import java.io.File;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.tests.context.ExternalProgramContext;

public interface Toolkit {
	//NOTE A lot of arguments can be null, e.g. prompt and validator
	
	// we use a messages object to report messages
	// The UI uses listeners to get the data.
	Messages getMessages();
	
	// the UI presents a question to the user
	String ask(String prompt, Validator validator);
	String askOptions(String prompt, OptionShaper shaper, String... options);
	
	// let the user press a key
	void waitForUser(String prompt);
	
	// tell the user what he should do
	// The instructions should be visible until the user
	// dismisses them. If possible, this method should
	// return immediately (while the user still reads
	// the message).
	void showInstructions(String instructions);
	
	// start an external program
	// The output is redirected to streams and the method
	// doesn't wait for the program to exit.
	ExternalProgramContext execProgram(String name, String[] commandline,
			String[] environment, File dir);
	
	// tell the UI that all tests are done
	// It should notify the user and show a summary of the results.
	void allTestsFinished();
	
	// called before the first test is run
	void start();
	
	// called after the last test
	void tearDown();
}
