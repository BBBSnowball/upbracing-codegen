package de.upbracing.code_generation.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.tests.context.ExternalProgramContext;
import de.upbracing.code_generation.tests.context.ExternalProgramContext.MonitoredProcess;

/** like {@link Toolkit}, but it has a lot of convenience functions */
public class RichToolkit implements Toolkit {
	private Toolkit inner;
	private PwdProvider pwd_provider;
	
	/** see {@link RichToolkit#execProgram(String, String[], String[], File)} */
	public interface PwdProvider {
		String makeAbsolute(String path);
	}

	public RichToolkit(Toolkit inner) {
		super();
		this.inner = inner;
	}
	
	/** get real toolkit instance
	 * 
	 * @return the toolkit
	 */
	public Toolkit getInner() {
		return inner;
	}

	/** start an external program
	 * 
	 * The input and output are redirected to the
	 * console of the UI. The method waits for the
	 * program to exit. It returns the exit status.
	 * 
	 * see {@link Runtime#exec(String[], String[], File)}
	 * 
	 * @param name
	 * @param commandline
	 * @param environment
	 * @param dir
	 * @return exit code of the process
	 * @throws InterruptedException if the Thread is interrupted,
	 * 		while the process is running
	 */
	public int run(String name, String[] commandline, String[] environment,
			File dir) throws InterruptedException {
		ExternalProgramContext pc = inner.execProgram(name, commandline, environment, dir);
		
		return processIO(pc);
	}
	
	public int processIO(final ExternalProgramContext pc) throws InterruptedException {
		final MonitoredProcess p = pc.getProcess();
		
		final boolean finished[] = new boolean[] { false };
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				OutputStream stdin = p.getOutputStream();
				InputStream stdout = p.getInputStream();
				InputStream stderr = p.getErrorStream();
				try {
					while (!Thread.interrupted() && (stdout != null || stderr != null)) {
						boolean got_data = false;
						
						if (stdout != null && stdout.available() > 0) {
							if (stdout.read() < 0 || stdout.skip(stdout.available()) < 0) {
								stdout.close();
								stdout = null;
							}
							got_data = true;
						}
						
						if (stderr.available() > 0) {
							if (stderr.read() < 0 || stderr.skip(stderr.available()) < 0) {
								stderr.close();
								stderr = null;
							}
							got_data = true;
						}
						
						while (System.in.available() > 0) {
							int x = System.in.read();
							if (x < 0)
								break;
							stdin.write(x);
							got_data = true;
						}
						
						if (!got_data) {
							if (finished[0])
								return;
							
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
								break;
							}
						}
					}
				} catch (IOException e) {
					pc.reportError(e);
				} finally {
					try {
						if (stdout != null)
							stdout.close();
					} catch (IOException e) { }
		
					try {
						if (stderr != null)
							stderr.close();
					} catch (IOException e) { }
				}
			}
		});
		t.start();

		int exit_code = pc.getProcess().waitFor();
		
		finished[0] = true;
		//t.interrupt();
		t.join(500);
		
		return exit_code;
	}

	public int run(String name, String[] commandline) throws InterruptedException {
		return run(name, commandline, null, null);
	}
	
	public int run(String[] commandline) throws InterruptedException {
		return run(commandline[0], commandline, null, null);
	}

	/*public int run(String name, String commandline) throws InterruptedException {
		return run(name, splitCommandline(commandline), null, null);
	}

	public int run(String commandline) throws InterruptedException {
		return run(commandline, splitCommandline(commandline), null, null);
	}
	
	public String[] splitCommandline(String commandline) {
		//TODO This is the method used by Runtime.exec, but I think
		//     it won't work, if any argument contains a space.
		StringTokenizer tokens = new StringTokenizer(commandline);
		ArrayList<String> tokens2 = new ArrayList<String>();
		while (tokens.hasMoreTokens())
			tokens2.add(tokens.nextToken());
		return tokens2.toArray(new String[tokens2.size()]);
	}*/
	
	public ExternalProgramContext exec(String name, String[] commandline) throws InterruptedException {
		return execProgram(name, commandline, null, null);
	}
	
	public ExternalProgramContext exec(String[] commandline) throws InterruptedException {
		return execProgram(commandline[0], commandline, null, null);
	}

	public ExternalProgramContext execProgram(String name, String[] commandline,
			String[] environment, File dir) {
		// Ruby and Java disagree about the current directory because
		// Java cannot change it, so JRuby uses it's own copy of the
		// current dir. Unfortunately, the OS cannot know that and
		// start the process in the wrong directory. We fix that by
		// always putting in the path, which we obviously have to
		// get from JRuby.
		if ((dir == null || !dir.isAbsolute()) && pwd_provider != null)
			dir = new File(pwd_provider.makeAbsolute(dir != null ? dir.getPath() : null));
			
		return inner.execProgram(name, commandline, environment, dir);
	}
	
	public PwdProvider getPwdProvider() {
		return pwd_provider;
	}

	public void setPwdProvider(PwdProvider pwd_provider) {
		this.pwd_provider = pwd_provider;
	}

	public String ask() {
		return inner.ask(null, NoValidator.instance);
	}

	public String ask(String prompt) {
		return inner.ask(prompt, NoValidator.instance);
	}

	public String ask(String prompt, Validator validator) {
		if (validator == null)
			validator = NoValidator.instance;
		return inner.ask(prompt, validator);
	}

	public String ask(String prompt, String validator_regex) {
		if (validator_regex == null)
			return ask(prompt, (Validator)null);
		return inner.ask(prompt, new RegexValidator(validator_regex));
	}

	public void waitForUser() {
		inner.waitForUser(null);
	}
	
	private static class NoValidator implements Validator {
		public static final Validator instance = new NoValidator();
		
		@Override
		public Result validate(String text) {
			return Validator.Result.UNKNOWN;
		}
	}
	
	public boolean askYesNo(String prompt) {
		return "yes".equals(askOptions(prompt, new YesNoShaper(), "yes", "no"));
	}
	
	private class YesNoShaper implements OptionShaper {
		@Override
		public String process(String text) {
			text = text.trim().toLowerCase();
			
			if (text.equals("y") || text.equals("true") || text.equals("1"))
				return "yes";
			else if (text.equals("n") || text.equals("false") || text.equals("0"))
				return "no";
			else
				return text;
		}
	}

	public Messages getMessages() {
		return inner.getMessages();
	}

	public String askOptions(String prompt, OptionShaper shaper,
			String... options) {
		return inner.askOptions(prompt, shaper, options);
	}

	public void waitForUser(String prompt) {
		inner.waitForUser(prompt);
	}

	public void showInstructions(String instructions) {
		inner.showInstructions(instructions);
	}
	
	@Override
	public void allTestsFinished() {
		inner.allTestsFinished();
	}
}
