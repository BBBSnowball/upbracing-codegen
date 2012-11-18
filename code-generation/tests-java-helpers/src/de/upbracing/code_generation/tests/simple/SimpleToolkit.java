package de.upbracing.code_generation.tests.simple;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.Context;
import de.upbracing.code_generation.Messages.ContextListener;
import de.upbracing.code_generation.Messages.Message;
import de.upbracing.code_generation.Messages.MessageListener;
import de.upbracing.code_generation.tests.OptionShaper;
import de.upbracing.code_generation.tests.Toolkit;
import de.upbracing.code_generation.tests.Validator;
import de.upbracing.code_generation.tests.context.Result;
import de.upbracing.code_generation.tests.context.ExternalProgramContext;
import de.upbracing.code_generation.tests.context.ProgramIO;
import de.upbracing.code_generation.tests.context.TestContext;
import de.upbracing.code_generation.tests.context.ProgramIO.ProgramIOListener;
import de.upbracing.code_generation.tests.context.ProgramIO.Type;
import de.upbracing.code_generation.tests.context.Result.ErrorOrFailure;

public class SimpleToolkit implements Toolkit {
	private Messages messages;
	private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
	
	private LinkedList<Context> failed_contexts = new LinkedList<Context>();
	
	public SimpleToolkit() {
		messages = new Messages();
		
		messages.addContextListener(new ContextListener() {
			@Override
			public void contextPushed(Object context_item) {
			}
			
			@Override
			public void contextPopped(Object context_item) {
				if (context_item instanceof TestContext) {
					// remember failed tests
					if (!((TestContext)context_item).getResult().isSuccessful())
						failed_contexts.add(messages.getContext());
				}
			}
		});
		
		messages.addMessageListener(new MessageListener() {
			@Override
			public void message(Message msg) {
				showMessage(msg);
			}
		});
	}

	@Override
	public Messages getMessages() {
		return messages;
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
		if (prompt != null) {
			if (!prompt.endsWith("\n"))
				prompt += "\n";
		} else
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
		printInstructions(instructions);
		waitForUser(null);
	}

	@Override
	public ExternalProgramContext execProgram(String name, String[] commandline,
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
	
	@Override
	public void allTestsFinished() {
		for (Context ctx : failed_contexts) {
			StringBuffer sb = new StringBuffer();
			
			TestContext test = (TestContext) ctx.getTopmostItem();
			Result result = test.getResult();
			sb.append(result.getStatus());
			sb.append(": ");
			sb.append(result.getMessage());
			
			ctx.toLongString("  ", sb);

			System.out.println(sb.toString());
		}
	}
	
	public interface OutputFormatter {
		void printProgramIO(String data, Type type);
		void reportProgramResult(ExternalProgramContext context, Result result);
		void showMessage(Message msg);
		void printPrompt(String prompt);
		void printInstructions(String instructions);
	}
	
	public static class DefaultOutputFormatter implements OutputFormatter {
		public void printProgramIO(String data, Type type) {
			System.out.print(data);
		}
	
		public void reportProgramResult(ExternalProgramContext context,
				Result result) {
			String name = context.getName();
			
			if (result.isSuccessful())
				System.out.println(name + " finished successfully");
			else
				System.out.println(name + ": " + result.getMessage());
		}
	
		public void showMessage(Message msg) {
			StringBuffer sb = new StringBuffer();
			msg.format(sb, "  ");
			System.out.println(sb.toString());
		}
		
		public void printPrompt(String prompt) {
			System.out.print(prompt);
		}
	
		public void printInstructions(String instructions) {
			System.out.print(instructions);
			if (!instructions.endsWith("\n"))
				System.out.println();
		}
	}
	
	private OutputFormatter outputFormatter = new DefaultOutputFormatter();
	
	public OutputFormatter getOutputFormatter() {
		return outputFormatter;
	}
	
	public void setOutputFormatter(OutputFormatter outputFormatter) {
		this.outputFormatter = outputFormatter;
	}

	protected void printProgramIO(String data, Type type) {
		outputFormatter.printProgramIO(data, type);
	}

	protected void reportProgramResult(ExternalProgramContext context,
			de.upbracing.code_generation.tests.context.Result result) {
		outputFormatter.reportProgramResult(context, result);
	}

	protected void showMessage(Message msg) {
		outputFormatter.showMessage(msg);
	}

	protected void printPrompt(String prompt) {
		outputFormatter.printPrompt(prompt);
	}

	protected void printInstructions(String instructions) {
		outputFormatter.printInstructions(instructions);
	}

}
