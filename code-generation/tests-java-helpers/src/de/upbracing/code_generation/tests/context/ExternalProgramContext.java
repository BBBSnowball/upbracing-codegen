package de.upbracing.code_generation.tests.context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.ContextItem;
import de.upbracing.code_generation.tests.streams.MonitoredInputStream;
import de.upbracing.code_generation.tests.streams.MonitoredOutputStream;

public class ExternalProgramContext extends TestContext {
	private String commandline[];
	private String environment[];
	private File dir;
	private MonitoredProcess process;
	private ProgramIO programIO;
	private ContextItem context_item;
	
	/** same arguments as {@link Runtime#exec(String[], String[], File)} */
	public ExternalProgramContext(String name, Messages messages,
			String[] commandline, String[] environment, File dir) {
		super(name);
		
		this.commandline = commandline;
		this.environment = environment;
		this.dir = dir;
		setResult(Result.NotStarted.instance);
		
		if (messages != null)
			this.context_item = messages.pushContext(this);
	}
	
	public void popFromContext() {
		context_item.pop();
		
		context_item = null;
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
	
	public MonitoredProcess run() {
		try {
			Process p = Runtime.getRuntime().exec(commandline, environment, dir);

			setResult(Result.Running.instance);
			
			this.process = new MonitoredProcess(p);
			
			return this.process;
		} catch (IOException e) {
			setResult(new Result.Error(e));
			throw new RuntimeException(e);
		}
	}
	
	public MonitoredProcess getProcess() {
		return process;
	}
	
	public ProgramIO getProgramIO() {
		if (programIO == null)
			programIO = new ProgramIORecorder(
					process.getMonitoredOutputStream(),
					process.getMonitoredInputStream(),
					process.getMonitoredErrorStream());
		return programIO;
	}
	
	public void recordIO() {
		getProgramIO().setRecording(true);
	}
	
	public class MonitoredProcess extends Process {
		private Process inner;
		private MonitoredInputStream stdout, stderr;
		private MonitoredOutputStream stdin;

		public MonitoredProcess(Process inner) {
			this.inner = inner;
		}

		public void destroy() {
			inner.destroy();
		}

		public boolean equals(Object obj) {
			return inner.equals(obj);
		}

		public int exitValue() {
			return inner.exitValue();
		}

		public InputStream getErrorStream() {
			return getMonitoredErrorStream();
		}

		public InputStream getInputStream() {
			return getMonitoredInputStream();
		}

		public OutputStream getOutputStream() {
			return getMonitoredOutputStream();
		}

		public MonitoredInputStream getMonitoredErrorStream() {
			if (stderr == null)
				stderr = new MonitoredInputStream(inner.getErrorStream());
			return stderr;
		}

		public MonitoredInputStream getMonitoredInputStream() {
			if (stdout == null)
				stdout = new MonitoredInputStream(inner.getInputStream());
			return stdout;
		}

		public MonitoredOutputStream getMonitoredOutputStream() {
			if (stdin == null)
				stdin = new MonitoredOutputStream(inner.getOutputStream());
			return stdin;
		}

		public int hashCode() {
			return inner.hashCode();
		}

		public String toString() {
			return inner.toString();
		}

		public int waitFor() throws InterruptedException {
			int exit_value = inner.waitFor();
			
			if (getResult() instanceof Result.Running) {
				if (exit_value == 0)
					setResult(Result.Success.instance);
				else
					setResult(new Result.Error("exit value is " + exit_value));
			}
			
			if (context_item != null)
				popFromContext();
			
			return exit_value;
		}
	}

	public void reportError(IOException e) {
		setResult(new Result.Error(e));
	}

	public void reportError(String message) {
		setResult(new Result.Error(message));
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("external program ");
		sb.append(getName());
		sb.append(":");
		for (String arg : commandline) {
			sb.append(" ");
			if (Pattern.matches("\\A[-a-zA-Z0-9_/\\:]+\\z", arg))
				sb.append(arg);
			else
				sb.append("'" + arg.replace("'", "\\'") + "'");
		}
		return sb.toString();
	}
}
