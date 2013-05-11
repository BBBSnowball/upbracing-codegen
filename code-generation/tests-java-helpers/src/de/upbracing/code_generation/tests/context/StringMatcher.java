package de.upbracing.code_generation.tests.context;

import java.io.InputStream;

import de.upbracing.code_generation.tests.TestFailedException;
import de.upbracing.code_generation.tests.serial.SerialHelper;

public class StringMatcher extends TestContext implements Runnable {

	private SerialHelper serial;
	private byte[] expected;
	
	private int matched_chars;

	public StringMatcher(SerialHelper serial, String expected) {
		this(serial, expected.getBytes());
	}

	public StringMatcher(SerialHelper serial, byte[] expected) {
		super("match string");
		
		this.serial = serial;
		this.expected = expected;
	}

	public void run(long timeout_millis) throws TestFailedException {
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
			InputStream in = serial.getInputStream();
			byte[] buf = new byte[Math.min(expected.length, 256)];
			
			while (!Thread.interrupted()) {
				// how many chars are missing?
				int left_chars = expected.length - matched_chars;
				if (left_chars <= 0) {
					// we're done and everything matched
					setResult(Result.Success.instance);
					break;
				}
				
				// try to read so many chars
				int chars_to_read = Math.min(left_chars, buf.length);
				int real_len;
				real_len = in.read(buf, 0, chars_to_read);

				// check for "end of stream"
				while (real_len < 0) {
					// Our stream shouldn't have any end at all, but
					// "end of stream" seems to be reported, if there
					// isn't any data at the moment.
					// (at least sometimes)
					
					// give it some time
					Thread.sleep(10);
					
					// try again
					real_len = in.read();
				}
				
				// compare
				for (int i=0;i<real_len;i++) {
					if (expected[matched_chars] != buf[i]) {
						setResult(new Result.Error(String.format(
								"mismatch at %d: 0x%02x %s (actual)  !=  0x%02x %s (expected)",
								i, buf[i], formatChar(buf[i]),
								expected[matched_chars], formatChar(expected[matched_chars]))));
						return;
					}
					
					++matched_chars;
				}
			}
		} catch (Throwable t) {
			setResult(new Result.Error(t));
		}
	}

	private String formatChar(byte b) {
		switch ((char)b) {
		case '\r':
			return "'\\r'";
		case '\n':
			return "'\\n'";
		case '\0':
			return "'\\0'";
		default:
			if (b >= 32 && b < 128)
				return "'" + ((char)b) + "'";
			else
				return "?";
		}
	}
}
