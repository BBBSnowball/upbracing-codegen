package de.upbracing.code_generation.fsm.model;

public final class StateVariablePurposes {
	public static final StateVariablePurpose STATE
		= new Purpose("current state");
	
	public static final StateVariablePurpose WAIT
		= new Purpose("wait time counter");
	
	private StateVariablePurposes() { }
	
	private static class Purpose implements StateVariablePurpose {
		private String name;

		public Purpose(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		public String toString() {
			return "Purpose[" + name + "]";
		}
	}
}
