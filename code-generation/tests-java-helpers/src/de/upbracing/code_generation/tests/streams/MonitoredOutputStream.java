package de.upbracing.code_generation.tests.streams;

import java.io.IOException;
import java.io.OutputStream;

public class MonitoredOutputStream extends OutputStream implements MonitoredStream {
	private OutputStream inner;
	private MonitoredStreamHelper listeners = new MonitoredStreamHelper();

	public MonitoredOutputStream(OutputStream inner) {
		this.inner = inner;
	}
	
	public void addStreamListener(MonitoredStreamListener listener) {
		listeners.addStreamListener(listener);
	}

	public void removeStreamListener(MonitoredStreamListener listener) {
		listeners.removeStreamListener(listener);
	}

	public void close() throws IOException {
		try {
			inner.close();
			listeners.fireClose();
		} catch (IOException e) {
			listeners.fireException("close", e);
			throw new IOException(e);
		}
	}

	public void flush() throws IOException {
		try {
			inner.flush();
			listeners.fireFlush();
		} catch (IOException e) {
			listeners.fireException("flush", e);
			throw new IOException(e);
		}
	}

	public void write(byte[] b, int off, int len) throws IOException {
		try {
			inner.write(b, off, len);
			listeners.fireWrite(b, off, len);
		} catch (IOException e) {
			listeners.fireException("write(array)", e);
			throw new IOException(e);
		}
	}

	public void write(byte[] b) throws IOException {
		try {
			inner.write(b);
			listeners.fireWrite(b, 0, b.length);
		} catch (IOException e) {
			listeners.fireException("write(array)", e);
			throw new IOException(e);
		}
	}

	public void write(int b) throws IOException {
		try {
			inner.write(b);
			listeners.fireWrite(new byte[] { (byte)b }, 0, 1);
		} catch (IOException e) {
			listeners.fireException("write(one)", e);
			throw new IOException(e);
		}
	}
}
