package de.upbracing.code_generation.tests.simple;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.tests.OptionShaper;
import de.upbracing.code_generation.tests.Toolkit;
import de.upbracing.code_generation.tests.Validator;
import de.upbracing.code_generation.tests.context.Result;
import de.upbracing.code_generation.tests.context.ExternalProgramContext;
import de.upbracing.code_generation.tests.context.ProgramIO;
import de.upbracing.code_generation.tests.context.ProgramIO.ProgramIOListener;
import de.upbracing.code_generation.tests.context.ProgramIO.Type;
import de.upbracing.code_generation.tests.context.Result.ErrorOrFailure;

public class SimpleToolkit implements Toolkit {
	private Messages messages = new Messages();
	private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

	@Override
	public Messages getMessages() {
		return messages;
	}
	
	public void printPrompt(String prompt) {
		System.out.print(prompt);
	}

	@Override
	public String ask(String prompt, Validator validator) {
		if (prompt != null) {
			if (prompt.endsWith(":"))
				prompt = prompt + " ";
			else
				prompt = prompt + "\n>  ";
		} else
			prompt = ">  ";
		
		try {
			String prev = null;
			while (true) {
				if (prev == null)
					printPrompt(prompt);
				else
					printPrompt(">> ");
				String answer = stdin.readLine();
				if (answer.endsWith("\n"))
					answer = answer.substring(0, answer.length()-1);
				
				if (validator == null)
					return answer;
				
				if (prev != null) {
					String both = prev + answer;
					Validator.Result result = validator.validate(both);
					if (result == Validator.Result.VALID || result == Validator.Result.UNKNOWN)
						return both;
					else if (answer.isEmpty()) {
						// abort accumulating
						prev = null;
						continue;
					} else if (result == Validator.Result.INCOMPLETE) {
						// do we need another line?
						both += "\n";
						result = validator.validate(both);
						if (result != Validator.Result.INVALID) {
							prev = both;
							continue;
						} else {
							// another line wouldn't help
							prev = null;
						}
					}
				}

				Validator.Result result = validator.validate(answer);
				if (result == Validator.Result.VALID || result == Validator.Result.UNKNOWN)
					return answer;
				else if (result == Validator.Result.INCOMPLETE) {
					// do we need another line?
					result = validator.validate(answer + "\n");
					if (result != Validator.Result.INVALID)
						prev = answer + "\n";
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String askOptions(String prompt, OptionShaper shaper, String... options) {
		if (prompt != null)
			printPrompt(prompt + "\n");
		
		StringBuilder sb = new StringBuilder();
		sb.append("options: ");
		boolean first = true;
		for (String option : options) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(option);
		}
		
		String answer = ask(sb.toString(), new OptionValidator(shaper, options));
		
		if (shaper != null)
			return shaper.process(answer);
		else
			return answer;
	}
	
	private class OptionValidator implements Validator {
		private OptionShaper shaper;
		private String[] options;

		public OptionValidator(OptionShaper shaper, String[] options) {
			this.shaper = shaper;
			this.options = options;
		}

		@Override
		public Result validate(String text) {
			if (text.equals("\n"))
				// we don't want a second line
				return Result.INVALID;
			
			String shaped_option = (shaper != null ? shaper.process(text) : text);
			
			boolean incomplete = false;
			for (String valid_option : options) {
				if (valid_option.equals(shaped_option))
					return Result.VALID;
				else if (valid_option.startsWith(shaped_option))
					incomplete = true;
			}
			
			if (incomplete)
				return Result.INCOMPLETE;
			else
				return Result.INVALID;
		}
		
	}

	@Override
	public void waitForUser(String prompt) {
		if (prompt != null)
			prompt += "\n";
		else
			prompt = "";
		
		//TODO better implementation, if we can put the terminal into raw mode
		
		prompt += "Press enter to continue";
		
		printPrompt(prompt);
		
		try {
			stdin.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showInstructions(String instructions) {
		System.out.println(instructions);
		waitForUser(null);
	}

	@Override
	public ExternalProgramContext exec(String name, String[] commandline,
			String[] environment, File dir) {
		ExternalProgramContext context = new ExternalProgramContext(
				name, messages, commandline, environment, dir);
		
		context.run();
		
		printToConsole(context.getProgramIO());
		
		monitorStatus(context);
		
		return context;
	}

	private void printToConsole(ProgramIO programIO) {
		programIO.addProgramIOListener(new ProgramIOListener() {
			@Override
			public void programIO(String data, Type type) {
				printProgramIO(data, type);
			}
		});
	}

	private void monitorStatus(final ExternalProgramContext context) {
		context.addPropertyChangeListener("result", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent ev) {
				Result result = context.getResult();
				if (result instanceof ErrorOrFailure || result.isSuccessful())
					reportProgramResult(context, result);
			}
		});
	}

	private void printProgramIO(String data, Type type) {
		//TODO
		System.out.print(data);
	}

	private void reportProgramResult(ExternalProgramContext context,
			Result result) {
		String name = context.getName();
		
		if (result.isSuccessful())
			System.out.println(name + " finished successfully");
		else
			System.out.println(name + ": " + result.getMessage());
	}
}
