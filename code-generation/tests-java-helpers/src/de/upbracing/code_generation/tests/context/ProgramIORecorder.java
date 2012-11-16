package de.upbracing.code_generation.tests.context;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;

import de.upbracing.code_generation.tests.streams.MonitoredInputStream;
import de.upbracing.code_generation.tests.streams.MonitoredOutputStream;
import de.upbracing.code_generation.tests.streams.MonitoredStreamListener;

public class ProgramIORecorder implements ProgramIO {
	private LinkedList<ProgramIOListener> listeners = new LinkedList<ProgramIO.ProgramIOListener>();
	
	private LinkedList<ProgramIOPiece> recordedIO;
	private StringBuffer data_accumulator;
	private Type current_type;
	private boolean recording;

	private Charset charset;
	
	public ProgramIORecorder(MonitoredOutputStream stdin, MonitoredInputStream stdout,
			MonitoredInputStream stderr, Charset charset) {
		this.charset = charset;
		listenOn(stdin, stdout, stderr);
	}
	
	public ProgramIORecorder(MonitoredOutputStream stdin, MonitoredInputStream stdout,
			MonitoredInputStream stderr) {
		this(stdin, stdout, stderr, Charset.defaultCharset());
	}
	
	@Override
	public void addProgramIOListener(ProgramIOListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeProgramIOListener(ProgramIOListener listener) {
		listeners.remove(listener);
	}
	
	protected void fireProgramIO(String data, Type type) {
		for (ProgramIOListener l : listeners)
			l.programIO(data, type);
	}
	
	@Override
	public boolean isRecording() {
		return recording;
	}

	@Override
	public synchronized void setRecording(boolean recording) {
		if (recording == this.recording)
			return;
		
		this.recording = recording;
		
		if (recording) {
			// recording turned on -> initialize, if this is the first time
			if (recordedIO == null) {
				recordedIO = new LinkedList<ProgramIO.ProgramIOPiece>();
				data_accumulator = new StringBuffer();
			}
		} else {
			// recording turned off -> put last bits of data into the list
			flush();
		}
	}
	
	@Override
	public Collection<ProgramIOPiece> getRecordedIO() {
		return recordedIO;
	}
	
	public synchronized void flush() {
		if (data_accumulator != null && data_accumulator.length() > 0) {
			recordedIO.add(new ProgramIOPiece(data_accumulator.toString(), current_type));
			
			data_accumulator.setLength(0);
			current_type = null;
		}
	}

	private synchronized void handleIO(Type type, byte[] data, int offset, int length) {
		if (length <= 0)
			return;

		String text = new String(data, offset, length, charset);
		
		if (type != current_type)
			flush();
		
		if (recording) {
			data_accumulator.append(text);
			current_type = type;
		}
		
		for (ProgramIOListener l : listeners)
			l.programIO(text, type);
	}
	
	private class Listener implements MonitoredStreamListener {
		private Type type;
		
		public Listener(Type type) {
			this.type = type;
		}

		@Override
		public void read(byte[] data, int offset, int length) {
			handleIO(type, data, offset, length);
		}

		@Override
		public void unread(long unread_bytes) {
			throw new RuntimeException("not supported");
		}

		@Override
		public void skip(byte[] data, int offset, long length) {
			read(data, offset, (int)length);
		}

		@Override
		public boolean wantsSkippedData() {
			return true;
		}

		@Override
		public void write(byte[] data, int offset, int length) {
			handleIO(type, data, offset, length);
		}

		@Override
		public void flush() {
			ProgramIORecorder.this.flush();
		}

		@Override
		public void close() {
			ProgramIORecorder.this.flush();
		}

		@Override
		public void exception(String method, Throwable exception) {
		}
		
	}
	
	private void listenOn(MonitoredOutputStream stdin, MonitoredInputStream stdout,
			MonitoredInputStream stderr) {
		stdin.addStreamListener(new Listener(Type.IN));
		stdout.addStreamListener(new Listener(Type.OUT));
		stderr.addStreamListener(new Listener(Type.ERROR));
	}
}
