package de.upbracing.shared.timer.model.enums;

public enum TimerEnum {
	TIMER0,
	TIMER1,
	TIMER2,
	TIMER3;
	
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
