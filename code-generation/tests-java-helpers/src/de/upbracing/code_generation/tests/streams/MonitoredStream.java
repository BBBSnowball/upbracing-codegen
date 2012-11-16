package de.upbracing.code_generation.tests.streams;

public interface MonitoredStream {
	public void addStreamListener(MonitoredStreamListener listener);
	public void removeStreamListener(MonitoredStreamListener listener);
}
