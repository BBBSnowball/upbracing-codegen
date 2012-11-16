package de.upbracing.code_generation.tests.streams;

import java.util.LinkedList;

public class MonitoredStreamHelper {
	private LinkedList<MonitoredStreamListener> listeners;
	long mark_counter = 0;

	public MonitoredStreamHelper() {
		this.listeners = new LinkedList<MonitoredStreamListener>();
	}

	public void addStreamListener(MonitoredStreamListener listener) {
		listeners.add(listener);
	}
	
	public void removeStreamListener(MonitoredStreamListener listener) {
		listeners.remove(listener);
	}
	
	public void fireException(String method, Throwable exception) {
		for (MonitoredStreamListener l : listeners)
			l.exception(method, exception);
	}

	public void fireWrite(byte[] data, int offset, int length) {
		for (MonitoredStreamListener l : listeners)
			l.write(data, offset, length);
	}

	public void fireClose() {
		for (MonitoredStreamListener l : listeners)
			l.close();
	}

	public void fireFlush() {
		for (MonitoredStreamListener l : listeners)
			l.flush();
	}

	public void fireRead(byte[] data, int offset, int length) {
		mark_counter += length;
		
		for (MonitoredStreamListener l : listeners)
			l.read(data, offset, length);
	}

	public boolean anyListenerWantSkippedData() {
		for (MonitoredStreamListener l : listeners)
			if (l.wantsSkippedData())
				return true;
		return false;
	}

	public void fireSkip(byte[] data, int offset, long length) {
		mark_counter += length;
		
		for (MonitoredStreamListener l : listeners)
			l.skip(data, offset, length);
	}

	public void fireUnread(long unread_bytes) {
		for (MonitoredStreamListener l : listeners)
			l.unread(unread_bytes);
	}
	
	public void handleMark(int readlimit) {
		mark_counter = 0;
	}
	
	public void handleReset() {
		fireUnread(mark_counter);
		mark_counter = 0;
	}
}
