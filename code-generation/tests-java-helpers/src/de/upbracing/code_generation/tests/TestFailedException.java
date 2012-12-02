package de.upbracing.code_generation.tests;

import de.upbracing.code_generation.tests.context.Result;

@SuppressWarnings("serial")
public class TestFailedException extends Exception {
	private Result result;

	public TestFailedException() {
		super();
	}

	public TestFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public TestFailedException(String message) {
		super(message);
	}

	public TestFailedException(Throwable cause) {
		super(cause);
	}
	
	public TestFailedException(Result result) {
		super(result.getMessage());
		
		if (result.isSuccessful())
			throw new IllegalArgumentException("You cannot have a TestFailed with a successful result!");
		
		this.result = result;
	}

	public Result getResult() {
		return result;
	}
}
