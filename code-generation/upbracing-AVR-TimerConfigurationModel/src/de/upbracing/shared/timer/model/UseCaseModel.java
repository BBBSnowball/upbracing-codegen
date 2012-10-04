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

/**
 * This class stores the data of a single use case configuration.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
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
	/**
	 * Gets the name of this configuration.
	 * @return the name of this configuration.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Gets the timer operation mode.
	 * @return the selected mode of operation.
	 */
	public TimerOperationModes getMode() {
		return mode;
	}
	/**
	 * Gets the timer selected for this configuration.
	 * @return the selected timer.
	 */
	public TimerEnum getTimer() {
		return timer;
	}
	/**
	 * Gets the prescale factor selected for this configuration.
	 * @return the selected prescale factor.
	 */
	public PrescaleFactors getPrescale() {
		return prescale;
	}
	/**
	 * Gets the desired ICR period for this configuration.
	 * @return the desired ICR period.
	 */
	public double getIcrPeriod() {
		return icrPeriod;
	}
	/**
	 * Gets the desired OCRnA period for this configuration.
	 * @return the selected OCRnA period.
	 */
	public double getOcrAPeriod() {
		return ocrAPeriod;
	}
	/**
	 * Gets the desired OCRnB period for this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the selected OCRnB period.
	 */
	public double getOcrBPeriod() {
		return ocrBPeriod;
	}
	/**
	 * Gets the desired OCRnC period for this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the selected OCRnC period.
	 */
	public double getOcrCPeriod() {
		return ocrCPeriod;
	}
	
	// Private Getters (Overflow):
	/**
	 * Gets the overflow interrupt setting for this configuration.
	 * @return the overflow interrupt setting.
	 */
	public boolean getOverflowInterrupt() {
		return overflowInterrupt;
	}
	
	// Private Getters (CTC):
	/**
	 * Gets the selected CTC top value register selection for this configuration.
	 * @return the selected CTC top value register.
	 */
	public CTCTopValues getCtcTop() {
		return ctcTop;
	}
	/**
	 * Gets the compare match interrupt setting for channel A in this configuration.
	 * @return the compare match interrupt setting for channel A.
	 */
	public boolean getCompareInterruptA() {
		return compareInterruptA;
	}
	/**
	 * Gets the compare match interrupt setting for channel B in this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the compare match interrupt setting for channel B.
	 */
	public boolean getCompareInterruptB() {
		return compareInterruptB;
	}
	/**
	 * Gets the compare match interrupt setting for channel C in this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the compare match interrupt setting for channel C.
	 */
	public boolean getCompareInterruptC() {
		return compareInterruptC;
	}
	/**
	 * Gets the selected CTC output pin mode for channel A in this configuration.
	 * @return the selected output pin mode for channel A in CTC mode.
	 */
	public CTCOutputPinMode getComparePinModeA() {
		return comparePinModeA;
	}
	/**
	 * Gets the selected CTC output pin mode for channel B in this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the selected output pin mode for channel B in CTC mode.
	 */
	public CTCOutputPinMode getComparePinModeB() {
		return comparePinModeB;
	}
	/**
	 * Gets the selected CTC output pin mode for channel C in this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the selected output pin mode for channel C in CTC mode.
	 */
	public CTCOutputPinMode getComparePinModeC() {
		return comparePinModeC;
	}
	
	// Private Getters (PWM):
	/**
	 * Gets the selected fast PWM top value selection for this configuration.
	 * @return the selected fast PWM top value.
	 */
	public PWMTopValues getFastPWMTop() {
		return fastPWMTop;
	}
	/**
	 * Gets the selected phase correct PWM top value register selection for this configuration.
	 * @return the selected phase correct PWM top value register.
	 */
	public PWMTopValues getPhaseCorrectPWMTop() {
		return phaseCorrectPWMTop;
	}
	/**
	 * Gets the selected phase and frequency correct PWM top value register selection for this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the selected phase and frequency correct PWM top value register.
	 */
	public PhaseAndFrequencyCorrectPWMTopValues getPhaseAndFrequencyCorrectPWMTop() {
		return phaseAndFrequencyCorrectPWMTop;
	}
	/**
	 * Gets the selected fast PWM output pin mode for channel A in this configuration.
	 * @return the selected output pin mode for channel A in fast PWM mode.
	 */
	public PWMSingleSlopeOutputPinMode getSingleSlopePWMPinModeA() {
		return singleSlopePWMPinModeA;
	}
	/**
	 * Gets the selected fast PWM output pin mode for channel B in this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the selected output pin mode for channel B in fast PWM mode.
	 */
	public PWMSingleSlopeOutputPinMode getSingleSlopePWMPinModeB() {
		return singleSlopePWMPinModeB;
	}
	/**
	 * Gets the selected fast PWM output pin mode for channel C in this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the selected output pin mode for channel C in fast PWM mode.
	 */
	public PWMSingleSlopeOutputPinMode getSingleSlopePWMPinModeC() {
		return singleSlopePWMPinModeC;
	}
	/**
	 * Gets the selected phase as well as phase and frequency correct PWM output pin mode for channel A in this configuration.
	 * @return the selected output pin mode for channel A in phase as well as phase and frequency correct PWM mode.
	 */
	public PWMDualSlopeOutputPinMode getDualSlopePWMPinModeA() {
		return dualSlopePWMPinModeA;
	}
	/**
	 * Gets the selected phase as well as phase and frequency correct PWM output pin mode for channel B in this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the selected output pin mode for channel B in phase as well as phase and frequency correct PWM mode.
	 */
	public PWMDualSlopeOutputPinMode getDualSlopePWMPinModeB() {
		return dualSlopePWMPinModeB;
	}
	/**
	 * Gets the selected phase as well as phase and frequency correct PWM output pin mode for channel C in this configuration.
	 * (Used for 16Bit timers only.)
	 * @return the selected output pin mode for channel C in phase as well as phase and frequency correct PWM mode.
	 */
	public PWMDualSlopeOutputPinMode getDualSlopePWMPinModeC() {
		return dualSlopePWMPinModeC;
	}
	
	// Public Setters (General):
	/**
	 * Sets the name of this configuration. The name must be a valid C language
	 * identifier. Additionally, it must not be equal to the name of any other {@link UseCaseModel}.
	 * (Note, that invalid names will be stored. But keep in mind, that validation will fail.)
	 * @param n the new name for this configuration.
	 */
	public void setName(String n) {
		this.name = n;
	}
	/**
	 * Sets the mode of this configuration. {@link TimerOperationModes#PWM_PHASE_FREQUENCY_CORRECT Phase and frequency correct PWM}
	 * mode must not be used in combination with 8Bit timers.
	 * (Note, that invalid mode and timer combinations will be stored. But keep in mind, that validation will fail.)
	 * @param m the new mode for this configuration.
	 */
	public void setMode(TimerOperationModes m) {
		this.mode = m;
	}
	/**
	 * Sets the timer used for this configuration. {@link TimerOperationModes#PWM_PHASE_FREQUENCY_CORRECT Phase and frequency correct PWM}
	 * mode must not be used in combination with 8Bit timers.
	 * (Note, that invalid mode and timer combinations will be stored. But keep in mind, that validation will fail.)
	 * @param t the new timer setting for this configuration.
	 */
	public void setTimer(TimerEnum t) {
		this.timer = t;
	}
	/**
	 * Sets the prescale factor for this configuration.
	 * @param p the new prescale factor for this configuration.
	 */
	public void setPrescale(PrescaleFactors p) {
		this.prescale = p;
	}
	/**
	 * Sets the desired ICR period for this configuration.
	 * (Note, that invalid ICR period settings will be stored. But keep in mind, that validation will fail.)
	 * @param f the desired ICR period.
	 */
	public void setIcrPeriod(double f) {
		this.icrPeriod = f;
	}
	/**
	 * Sets the desired ORCnA period for this configuration.
	 * (Note, that invalid ORCnA period settings will be stored. But keep in mind, that validation will fail.)
	 * @param f the desired ORCnA period.
	 */
	public void setOcrAPeriod(double f) {
		this.ocrAPeriod = f;
	}
	/**
	 * Sets the desired ORCnB period for this configuration. (Used for 16Bit timers only.)
	 * (Note, that invalid ORCnB period settings will be stored. But keep in mind, that validation will fail.)
	 * @param f the desired ORCnB period.
	 */
	public void setOcrBPeriod(double f) {
		this.ocrBPeriod = f;
	}
	/**
	 * Sets the desired ORCnC period for this configuration. (Used for 16Bit timers only.)
	 * (Note, that invalid ORCnC period settings will be stored. But keep in mind, that validation will fail.)
	 * @param f the desired ORCnC period.
	 */
	public void setOcrCPeriod(double f) {
		this.ocrCPeriod = f;
	}

	// Public Setters (Overflow):
	/**
	 * Sets the overflow interrupt enable flag for this configuration.
	 * @param i the new overflow interrupt enable flag.
	 */
	public void setOverflowInterrupt(boolean i) {
		this.overflowInterrupt = i;
	}

	// Public Setters (CTC):
	/**
	 * Sets the CTC top value register for this configuration.
	 * @param v the new CTC top value register selection.
	 */
	public void setCtcTop(CTCTopValues v) {
		ctcTop = v;
	}
	/**
	 * Sets the compare match interrupt enable flag for channel A in this configuration.
	 * @param i the new compare match interrupt enable flag for channel A.
	 */
	public void setCompareInterruptA(boolean i) {
		this.compareInterruptA = i;
	}
	/**
	 * Sets the compare match interrupt enable flag for channel B in this configuration.
	 * (Used for 16Bit timers only.)
	 * @param i the new compare match interrupt enable flag for channel B.
	 */
	public void setCompareInterruptB(boolean i) {
		this.compareInterruptB = i;
	}
	/**
	 * Sets the compare match interrupt enable flag for channel C in this configuration.
	 * (Used for 16Bit timers only.)
	 * @param i the new compare match interrupt enable flag for channel C.
	 */
	public void setCompareInterruptC(boolean i) {
		this.compareInterruptC = i;
	}
	/**
	 * Sets the CTC output pin mode for channel A in this configuration.
	 * @param m the new CTC output pin mode for channel A.
	 */
	public void setComparePinModeA(CTCOutputPinMode m) {
		this.comparePinModeA = m;
	}
	/**
	 * Sets the CTC output pin mode for channel B in this configuration.
	 * (Used for 16Bit timers only.)
	 * @param m the new CTC output pin mode for channel B.
	 */
	public void setComparePinModeB(CTCOutputPinMode m) {
		this.comparePinModeB = m;
	}
	/**
	 * Sets the CTC output pin mode for channel C in this configuration.
	 * (Used for 16Bit timers only.)
	 * @param m the new CTC output pin mode for channel C.
	 */
	public void setComparePinModeC(CTCOutputPinMode m) {
		this.comparePinModeC = m;
	}

	// Public Setters (PWM):
	/**
	 * Sets fast PWM top value for this configuration.
	 * @param p new fast PWM top value selection.
	 */
	public void setFastPWMTop(PWMTopValues p) {
		fastPWMTop = p;
	}
	/**
	 * Sets phase correct PWM top value register for this configuration.
	 * @param p new phase correct PWM top value register selection.
	 */
	public void setPhaseCorrectPWMTop(PWMTopValues p) {
		phaseCorrectPWMTop = p;
	}
	/**
	 * Sets phase and frequency correct PWM top value register for this configuration.
	 * (Used for 16Bit timers only.)
	 * @param p new phase and frequency correct PWM top value register selection.
	 */
	public void setPhaseAndFrequencyCorrectPWMTop(PhaseAndFrequencyCorrectPWMTopValues p) {
		phaseAndFrequencyCorrectPWMTop = p;
	}
	/**
	 * Sets the fast PWM output pin mode for channel A in this configuration.
	 * @param m the new fast PWM output pin mode for channel A.
	 */
	public void setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode m) {
		this.singleSlopePWMPinModeA = m;
	}
	/**
	 * Sets the fast PWM output pin mode for channel B in this configuration.
	 * (Used for 16Bit timers only.)
	 * @param m the new fast PWM output pin mode for channel B.
	 */
	public void setSingleSlopePWMPinModeB(PWMSingleSlopeOutputPinMode m) {
		this.singleSlopePWMPinModeB = m;
	}
	/**
	 * Sets the fast PWM output pin mode for channel C in this configuration.
	 * (Used for 16Bit timers only.)
	 * @param m the new fast PWM output pin mode for channel C.
	 */
	public void setSingleSlopePWMPinModeC(PWMSingleSlopeOutputPinMode m) {
		this.singleSlopePWMPinModeC = m;
	}
	/**
	 * Sets the phase as well as phase and frequency correct PWM output pin mode for channel A in this configuration.
	 * @param m the new phase as well as phase and frequency correct PWM output pin mode for channel A.
	 */
	public void setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode m) {
		this.dualSlopePWMPinModeA = m;
	}
	/**
	 * Sets the phase as well as phase and frequency correct PWM output pin mode for channel B in this configuration.
	 * (Used for 16Bit timers only.)
	 * @param m the new phase as well as phase and frequency correct PWM output pin mode for channel B.
	 */
	public void setDualSlopePWMPinModeB(PWMDualSlopeOutputPinMode m) {
		this.dualSlopePWMPinModeB = m;
	}
	/**
	 * Sets the phase as well as phase and frequency correct PWM output pin mode for channel C in this configuration.
	 * (Used for 16Bit timers only.)
	 * @param m the new phase as well as phase and frequency correct PWM output pin mode for channel C.
	 */
	public void setDualSlopePWMPinModeC(PWMDualSlopeOutputPinMode m) {
		this.dualSlopePWMPinModeC = m;
	}

}
