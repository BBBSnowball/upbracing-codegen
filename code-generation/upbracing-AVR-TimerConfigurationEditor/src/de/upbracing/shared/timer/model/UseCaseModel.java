package de.upbracing.shared.timer.model;

import de.upbracing.shared.timer.model.enums.CTCTopValues;
import de.upbracing.shared.timer.model.enums.PWMTopValues;
import de.upbracing.shared.timer.model.enums.PhaseAndFrequencyCorrectPWMTopValues;
import de.upbracing.shared.timer.model.enums.PrescaleFactors;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;

public class UseCaseModel {
	
	// Private Fields (General):
	private String name = "default";
	private TimerOperationModes mode = TimerOperationModes.OVERFLOW;
	private TimerEnum timer = TimerEnum.TIMER0;
	private PrescaleFactors prescale = PrescaleFactors.ONE;
	private double icrPeriod;
	private double ocrAPeriod;
	private double ocrBPeriod;
	private double ocrCPeriod;
	
	// Private Fields (Overflow):
	private boolean overflowInterrupt;
	
	// Private Fields (CTC):
	private CTCTopValues ctcTop = CTCTopValues.ICR;
	
	// Private Fields (PWM):
	private PWMTopValues fastPWMTop = PWMTopValues.BIT8;	
	private PWMTopValues phaseCorrectPWMTop = PWMTopValues.BIT8;
	private PhaseAndFrequencyCorrectPWMTopValues phaseAndFrequencyCorrectPWMTop = PhaseAndFrequencyCorrectPWMTopValues.ICR;
	
	// Public Getters (General):
	public String getName() {
		return name;
	}
	public TimerOperationModes getMode() {
		return mode;
	}
	public TimerEnum getTimer() {
		return timer;
	}
	public PrescaleFactors getPrescale() {
		return prescale;
	}
	public double getIcrPeriod() {
		return icrPeriod;
	}
	public double getOcrAPeriod() {
		return ocrAPeriod;
	}
	public double getOcrBPeriod() {
		return ocrBPeriod;
	}
	public double getOcrCPeriod() {
		return ocrCPeriod;
	}
	
	// Private Getters (Overflow):
	public boolean getOverflowInterrupt() {
		return overflowInterrupt;
	}
	
	// Private Getters (CTC):
	public CTCTopValues getCtcTop() {
		return ctcTop;
	}
	
	// Private Getters (PWM):
	public PWMTopValues getFastPWMTop() {
		return fastPWMTop;
	}
	public PWMTopValues getPhaseCorrectPWMTop() {
		return phaseCorrectPWMTop;
	}
	public PhaseAndFrequencyCorrectPWMTopValues getPhaseAndFrequencyCorrectPWMTop() {
		return phaseAndFrequencyCorrectPWMTop;
	}
	
	// Public Setters (General):
	public void setName(String n) {
		this.name = n;
	}
	public void setMode(TimerOperationModes m) {
		this.mode = m;
	}
	public void setTimer(TimerEnum t) {
		this.timer = t;
	}
	public void setPrescale(PrescaleFactors p) {
		this.prescale = p;
	}
	public void setIcrPeriod(double f) {
		this.icrPeriod = f;
	}
	public void setOcrAPeriod(double f) {
		this.ocrAPeriod = f;
	}
	public void setOcrBPeriod(double f) {
		this.ocrBPeriod = f;
	}
	public void setOcrCPeriod(double f) {
		this.ocrCPeriod = f;
	}

	// Public Setters (Overflow):
	public void setOverflowInterrupt(boolean i) {
		this.overflowInterrupt = i;
	}

	// Public Setters (CTC):
	public void setCtcTop(CTCTopValues v) {
		ctcTop = v;
	}

	// Public Setters (PWM):
	public void setFastPWMTop(PWMTopValues p) {
		fastPWMTop = p;
	}
	public void setPhaseCorrectPWMTop(PWMTopValues p) {
		phaseCorrectPWMTop = p;
	}
	public void setPhaseAndFrequencyCorrectPWMTop(PhaseAndFrequencyCorrectPWMTopValues p) {
		phaseAndFrequencyCorrectPWMTop = p;
	}
}
