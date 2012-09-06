package de.upbracing.shared.timer.model.enums;

public enum TimerOperationModes {
	OVERFLOW,
	CTC,
	PWM_FAST,
	PWM_PHASE_CORRECT,
	PWM_PHASE_FREQUENCY_CORRECT;
	
	public String toString() {
		
		if (this.equals(TimerOperationModes.OVERFLOW))
			return "Overflow";
		
		if (this.equals(TimerOperationModes.CTC))
			return "Clear on Compare Match";
		
		if (this.equals(TimerOperationModes.PWM_FAST))
			return "Fast PWM";
		
		if (this.equals(TimerOperationModes.PWM_PHASE_CORRECT))
			return "Phase Correct PWM";
		
		if (this.equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT))
			return "Phase and Frequency Correct PWM";
		
		// Fallback
		return this.name();
	}
	
	public TimerOperationModes fromString(String str) {
		
		if (str.equals("Overflow"))
			return TimerOperationModes.OVERFLOW;
		
		if (str.equals("Clear on Compare Match"))
			return TimerOperationModes.CTC;
		
		if (str.equals("Fast PWM"))
			return TimerOperationModes.PWM_FAST;
		
		if (str.equals("Phase Correct PWM"))
			return TimerOperationModes.PWM_PHASE_CORRECT;
		
		if (str.equals("Phase and Frequency Correct PWM"))
			return TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT;
		
		// Fallback
		return TimerOperationModes.OVERFLOW;
	}
}
