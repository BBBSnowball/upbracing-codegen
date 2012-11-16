package de.upbracing.code_generation.tests.streams;

import java.io.IOException;
import java.io.InputStream;

public class MonitoredInputStream extends InputStream implements MonitoredStream {
	private InputStream inner;
	private MonitoredStreamHelper listeners = new MonitoredStreamHelper();

	public MonitoredInputStream(InputStream inner) {
		this.inner = inner;
	}
	
	public void addStreamListener(MonitoredStreamListener listener) {
		listeners.addStreamListener(listener);
	}

	public void removeStreamListener(MonitoredStreamListener listener) {
		listeners.removeStreamListener(listener);
	}

	public int available() throws IOException {
		try {
			return inner.available();
		} catch (IOException e) {
			listeners.fireException("available", e);
			throw new IOException(e);
		}
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

	public void mark(int readlimit) {
		inner.mark(readlimit);
		listeners.handleMark(readlimit);
	}

	public boolean markSupported() {
		return inner.markSupported();
	}

	public int read() throws IOException {
		try {
			int read = inner.read();
			if (read >= 0)
				listeners.fireRead(new byte[] { (byte)read }, 0, 1);
			return read;
		} catch (IOException e) {
			listeners.fireException("read(one)", e);
			throw new IOException(e);
		}
	}

	public int read(byte[] b, int off, int len) throws IOException {
		try {
			int len2 = inner.read(b, off, len);
			if (len2 >= 0)
				listeners.fireRead(b, off, len2);
			return len2;
		} catch (IOException e) {
			listeners.fireException("read(array)", e);
			throw new IOException(e);
		}
	}

	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	public void reset() throws IOException {
		inner.reset();
		listeners.handleReset();
	}

	public long skip(long n) throws IOException {
		try {
			if (listeners.anyListenerWantSkippedData()) {
				byte b[] = new byte[(int)n];
				int len = inner.read(b, 0, b.length);
				listeners.fireSkip(b, 0, len);
				return len;
			} else {
				long len = inner.skip(n);
				listeners.fireSkip(null, 0, len);
				return len;
			}
		} catch (IOException e) {
			listeners.fireException("skip", e);
			throw new IOException(e);
		}
	}
}
