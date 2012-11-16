package de.upbracing.code_generation.tests.context;

import java.util.Collection;

public interface ProgramIO {
	void addProgramIOListener(ProgramIOListener listener);
	void removeProgramIOListener(ProgramIOListener listener);
	
	boolean isRecording();
	void setRecording(boolean recording);
	
	Collection<ProgramIOPiece> getRecordedIO();
	
	
	public enum Type { IN, OUT, ERROR }
	
	public static class ProgramIOPiece {
		private String data;
		private Type type;
		
		public ProgramIOPiece(String data, Type type) {
			super();
			this.data = data;
			this.type = type;
		}

		public String getData() {
			return data;
		}

		public Type getType() {
			return type;
		}
	}
	
	public interface ProgramIOListener {
		void programIO(String data, Type type);
	}
}
