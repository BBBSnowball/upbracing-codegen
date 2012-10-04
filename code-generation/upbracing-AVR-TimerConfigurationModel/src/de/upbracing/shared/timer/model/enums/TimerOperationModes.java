package de.upbracing.shared.timer.model.enums;

/**
 * This enum defines the valid operation modes
 * {@link TimerOperationModes#OVERFLOW OVERFLOW}, 
 * {@link TimerOperationModes#CTC CTC}, 
 * {@link TimerOperationModes#PWM_FAST PWM_FAST}, 
 * {@link TimerOperationModes#PWM_PHASE_CORRECT PWM_PHASE_CORRECT} and
 * {@link TimerOperationModes#PWM_PHASE_FREQUENCY_CORRECT PWM_PHASE_FREQUENCY_CORRECT}
 * for the timers of the AT90CAN128 processor.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public enum TimerOperationModes {
	/**
	 * Timer will run in overflow mode.
	 */
	OVERFLOW,
	/**
	 * Timer will run in CTC mode.
	 */
	CTC,
	/**
	 * Timer will run in fast PWM mode.
	 */
	PWM_FAST,
	/**
	 * Timer will run in phase correct PWM mode.
	 */
	PWM_PHASE_CORRECT,
	/**
	 * Timer will run in phase and frequency correct
	 * PWM mode (16Bit Timers only!).
	 */
	PWM_PHASE_FREQUENCY_CORRECT;
	
	/** 
	 * This method returns a user-friendly <code>String</code> representation
	 * of the current timer operation mode.
	 * @return a user-friendly <code>String</code> representation.
	 */
	@Override
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
}
