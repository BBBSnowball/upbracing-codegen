package de.upbracing.shared.timer.model;

import de.upbracing.shared.timer.model.enums.CTCOutputPinMode;
import de.upbracing.shared.timer.model.enums.CTCTopValues;
import de.upbracing.shared.timer.model.enums.PWMDualSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMSingleSlopeOutputPinMode;
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
	private boolean compareInterruptA;
	private boolean compareInterruptB;
	private boolean compareInterruptC;
	private CTCOutputPinMode comparePinModeA;
	private CTCOutputPinMode comparePinModeB;
	private CTCOutputPinMode comparePinModeC;
	
	// Private Fields (PWM):
	private PWMTopValues fastPWMTop = PWMTopValues.BIT8;	
	private PWMTopValues phaseCorrectPWMTop = PWMTopValues.BIT8;
	private PhaseAndFrequencyCorrectPWMTopValues phaseAndFrequencyCorrectPWMTop = PhaseAndFrequencyCorrectPWMTopValues.ICR;
	private PWMSingleSlopeOutputPinMode singleSlopePWMPinModeA;
	private PWMSingleSlopeOutputPinMode singleSlopePWMPinModeB;
	private PWMSingleSlopeOutputPinMode singleSlopePWMPinModeC;
	private PWMDualSlopeOutputPinMode dualSlopePWMPinModeA;
	private PWMDualSlopeOutputPinMode dualSlopePWMPinModeB;
	private PWMDualSlopeOutputPinMode dualSlopePWMPinModeC;
	
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
	public boolean getCompareInterruptA() {
		return compareInterruptA;
	}
	public boolean getCompareInterruptB() {
		return compareInterruptB;
	}
	public boolean getCompareInterruptC() {
		return compareInterruptC;
	}
	public CTCOutputPinMode getComparePinModeA() {
		return comparePinModeA;
	}
	public CTCOutputPinMode getComparePinModeB() {
		return comparePinModeB;
	}
	public CTCOutputPinMode getComparePinModeC() {
		return comparePinModeC;
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
	public PWMSingleSlopeOutputPinMode getSingleSlopePWMPinModeA() {
		return singleSlopePWMPinModeA;
	}
	public PWMSingleSlopeOutputPinMode getSingleSlopePWMPinModeB() {
		return singleSlopePWMPinModeB;
	}
	public PWMSingleSlopeOutputPinMode getSingleSlopePWMPinModeC() {
		return singleSlopePWMPinModeC;
	}
	public PWMDualSlopeOutputPinMode getDualSlopePWMPinModeA() {
		return dualSlopePWMPinModeA;
	}
	public PWMDualSlopeOutputPinMode getDualSlopePWMPinModeB() {
		return dualSlopePWMPinModeB;
	}
	public PWMDualSlopeOutputPinMode getDualSlopePWMPinModeC() {
		return dualSlopePWMPinModeC;
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
	public void setCompareInterruptA(boolean i) {
		this.compareInterruptA = i;
	}
	public void setCompareInterruptB(boolean i) {
		this.compareInterruptB = i;
	}
	public void setCompareInterruptC(boolean i) {
		this.compareInterruptC = i;
	}
	public void setComparePinModeA(CTCOutputPinMode m) {
		this.comparePinModeA = m;
	}
	public void setComparePinModeB(CTCOutputPinMode m) {
		this.comparePinModeB = m;
	}
	public void setComparePinModeC(CTCOutputPinMode m) {
		this.comparePinModeC = m;
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
	public void setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode m) {
		this.singleSlopePWMPinModeA = m;
	}
	public void setSingleSlopePWMPinModeB(PWMSingleSlopeOutputPinMode m) {
		this.singleSlopePWMPinModeB = m;
	}
	public void setSingleSlopePWMPinModeC(PWMSingleSlopeOutputPinMode m) {
		this.singleSlopePWMPinModeC = m;
	}
	public void setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode m) {
		this.dualSlopePWMPinModeA = m;
	}
	public void setDualSlopePWMPinModeB(PWMDualSlopeOutputPinMode m) {
		this.dualSlopePWMPinModeB = m;
	}
	public void setDualSlopePWMPinModeC(PWMDualSlopeOutputPinMode m) {
		this.dualSlopePWMPinModeC = m;
	}

}
