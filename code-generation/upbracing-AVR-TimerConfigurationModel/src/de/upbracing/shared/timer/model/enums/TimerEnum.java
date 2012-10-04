package de.upbracing.shared.timer.model.enums;

/**
 * This enum defines the four timers {@link TimerEnum#TIMER0 TIMER0}, 
 * {@link TimerEnum#TIMER1 TIMER1}, {@link TimerEnum#TIMER2 TIMER2} and 
 * {@link TimerEnum#TIMER3 TIMER3}.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public enum TimerEnum {
	/**
	 * Selects Timer 0 of the AT90CAN128.
	 */
	TIMER0,
	/**
	 * Selects Timer 1 of the AT90CAN128.
	 */
	TIMER1,
	/**
	 * Selects Timer 2 of the AT90CAN128.
	 */
	TIMER2,
	/**
	 * Selects Timer 3 of the AT90CAN128.
	 */
	TIMER3;
	
	/** 
	 * This method returns a user-friendly <code>String</code> representation
	 * of the current timer selection.
	 * @return a user-friendly <code>String</code> representation.
	 */
	@Override
	public String toString() {
		if (this.equals(TimerEnum.TIMER0))
			return "Timer 0 (8 Bit)";
		if (this.equals(TimerEnum.TIMER1))
			return "Timer 1 (16 Bit)";
		if (this.equals(TimerEnum.TIMER2))
			return "Timer 2 (8 Bit)";
		if (this.equals(TimerEnum.TIMER3))
			return "Timer 3 (16 Bit)";
		return "Cannot determine";
	}
}
