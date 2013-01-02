package de.upbracing.code_generation.tests.context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.upbracing.code_generation.tests.TestFailedException;
import de.upbracing.code_generation.tests.serial.SerialHelper;

//TODO This class has a lot of code in common with StringMatcher. We should
//      refactor the timeout stuff into a common base class.
public class RegexMatcher extends TestContext implements Runnable {

	private SerialHelper serial;
	private Pattern expected;
	private int max_len;
	private Charset charset;
	
	private int matched_chars;
	private Matcher matcher;

	public RegexMatcher(SerialHelper serial, String expected) {
		this(serial, Pattern.compile(expected), 4096, Charset.forName("iso-8859-1"));
	}

	public RegexMatcher(SerialHelper serial, Pattern expected, int max_len, Charset charset) {
		super("match regex: " + expected.pattern());
		
		this.serial = serial;
		this.expected = expected;
		this.max_len = max_len;
		this.charset = charset;
	}

	public Matcher run(long timeout_millis) throws TestFailedException {
		// initialize variables in case we kill
		// the thread before it can do that
		initRun();
		
		// start real check in a new thread
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
		
		// wait for thread to finish, but only until timeout
		try {
			t.join(timeout_millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// If we finished in time, that's fine. The thread will
		// have set the result. Otherwise, we report a timeout error.
		if (t.isAlive()) {
			// stop the thread
			t.interrupt();
			
			// signal error
			this.setResult(new Result.Error("timeout after " + timeout_millis + " ms"));
			
			// wait for the thread to end
			try {
				t.join(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (t.isAlive())
				//TODO use Messages class
				System.err.println("WARN: Reader thread is still alive.");
		}
		
		Result result = getResult();
		if (!result.isSuccessful())
			throw new TestFailedException(result);
		
		return matcher;
	}
	
	private void initRun() {
		matched_chars = 0;
	}
	
	@Override
	public void run() {
		try {
			// report status: in progress
			setResult(Result.Running.instance);
			
			// initialize variables, although
			// run(timeout) might have done that
			// already
			initRun();
			
			// get input stream and reserve up to 256 bytes of buffer memory
			//TODO We should really use a CharsetDecoder because this should be
			//     faster. Unfortunately, that class seems to be completely
			//     unusable in my implementation of Java. The implementation
			//     with CharsetDecoder is in commit 179782a6b32b5d.
			InputStream in = serial.getInputStream();
			byte[] buf = new byte[max_len];
			
			while (!Thread.interrupted()) {
				// break, if space in buffer is not enough
				if (matched_chars >= buf.length) {
					setResult(new Result.Error("match needs more than " + buf.length + "bytes"));
					break;
				}
				
				// read one character
				int x;
				try {
					x = in.read();
				} catch (IOException e) {
					setResult(new Result.Error(e));
					break;
				}
				
				// check for error
				if (x < 0) {
					// end of stream
					setResult(new Result.Error("end of stream"));
					break;
				}
				
				// add character to buffer and increase limit accordingly
				buf[matched_chars] = (byte)x;
				++matched_chars;
				
				// decode it
				//TODO Could that fail, if the buffer ends with a partial character?
				String received_chars = new String(buf, 0, matched_chars, charset);
				
				// try to match it
				Matcher m = expected.matcher(received_chars);
				if (m.lookingAt()) {
					// we found a match -> return
					this.matcher = m;
					setResult(Result.Success.instance);
					break;
				} else if (m.hitEnd()) {
					// no match, but it could match with more input
					// -> read another byte in next iteration
				} else {
					// it doesn't match and wouldn't match with more input
					setResult(new Result.Error("regex doesn't match"));
					break;
				}
			}
		} catch (Throwable t) {
			setResult(new Result.Error(t));
		}
	}
}
