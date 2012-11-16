package de.upbracing.code_generation.tests.streams;

public interface MonitoredStreamListener {
	void read(byte[] data, int offset, int length);
	void unread(long unread_bytes);
	void skip(byte[] data, int offset, long length);
	boolean wantsSkippedData();
	
	void write(byte[] data, int offset, int length);
	void flush();
	
	void close();
	
	void exception(String method, Throwable exception);
}
