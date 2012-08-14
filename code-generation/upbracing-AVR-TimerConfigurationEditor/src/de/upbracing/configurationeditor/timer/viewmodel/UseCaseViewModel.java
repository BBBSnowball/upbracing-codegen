package de.upbracing.configurationeditor.timer.viewmodel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import de.upbracing.shared.timer.model.UseCaseModel;
import de.upbracing.shared.timer.model.enums.CTCOutputPinMode;
import de.upbracing.shared.timer.model.enums.CTCTopValues;
import de.upbracing.shared.timer.model.enums.PWMTopValues;
import de.upbracing.shared.timer.model.enums.PhaseAndFrequencyCorrectPWMTopValues;
import de.upbracing.shared.timer.model.enums.PrescaleFactors;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;

public class UseCaseViewModel extends AViewModelBase {
	
	private UseCaseModel model;
	private ConfigurationViewModel parent;
		
	// Constructor:
	public UseCaseViewModel(UseCaseModel m) {
		this.model = m;
	}

	// Getter for Parent
	public UseCaseModel getModel() {
		return model;
	}
	
	// Routed Model Getters:
	public String getName() {
		return this.model.getName();
	}
	public TimerOperationModes getMode() {
		return model.getMode();
	}
	public TimerEnum getTimer() {
		return model.getTimer();
	}
	public PrescaleFactors getPrescale() {
		return model.getPrescale();
	}
	public double getIcrPeriod() {
		return model.getIcrPeriod();
	}
	public double getOcrAPeriod() {
		return model.getOcrAPeriod();
	}
	public double getOcrBPeriod() {
		return model.getOcrBPeriod();
	}
	public double getOcrCPeriod() {
		return model.getOcrCPeriod();
	}
	
	// Routed Model Setters:
	public void setName(String n) {
		model.setName(n);
		changes.firePropertyChange("name", null, null);
	}
	public void setMode(TimerOperationModes m) {
		this.model.setMode(m);
		changes.firePropertyChange("mode", null, null);
		triggerUpdateView();
	}
	public void setTimer(TimerEnum t) {
		this.model.setTimer(t);
		changes.firePropertyChange("timer", null, null);
		triggerUpdateView();
	}
	public void setPrescale(PrescaleFactors p) {
		this.model.setPrescale(p);
		changes.firePropertyChange("prescale", null, null);
		triggerUpdateView();
	}
	public void setIcrPeriod(double f) {
		this.model.setIcrPeriod(f);
		changes.firePropertyChange("icrPeriod", null, null);
	}
	public void setOcrAPeriod(double f) {
		this.model.setOcrAPeriod(f);
		changes.firePropertyChange("ocrAPeriod", null, null);
	}
	public void setOcrBPeriod(double f) {
		this.model.setOcrBPeriod(f);
		changes.firePropertyChange("ocrBPeriod", null, null);
	}
	public void setOcrCPeriod(double f) {
		this.model.setOcrCPeriod(f);
		changes.firePropertyChange("ocrCPeriod", null, null);
	}
	
	// Dynamic data-bound View Data:
	public boolean getIcrVisibility() {
		if (getMode() != null) {
			if (getMode().equals(TimerOperationModes.CTC))
				if (getCtcTop() != null && getCtcTop().equals(CTCTopValues.ICR))
					return true;
			if (getMode().equals(TimerOperationModes.PWM_FAST)) 
				if (getFastPWMTop() != null && getFastPWMTop().equals(PWMTopValues.ICR))
					return true;
			if (getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT))
				if (getPhaseCorrectPWMTop() != null && getPhaseCorrectPWMTop().equals(PWMTopValues.ICR))
					return true;
			if (getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT))
				if (getPhaseAndFrequencyCorrectPWMTop() != null && getPhaseAndFrequencyCorrectPWMTop().equals(PhaseAndFrequencyCorrectPWMTopValues.ICR))
					return true;
		}
		
		return false;
	}
	public String getIcrName() {
		return "Input Capture Register (Top)";
	}
	public String getOcrAName() {
		String name = null;
		if (getTimer().equals(TimerEnum.TIMER0) || getTimer().equals(TimerEnum.TIMER2))
			name = "Output Compare Register";
		if (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3))
			name = "Output Compare Register A";
		
		// Top Value Suffix (CTC):
		if (getMode() != null && getMode().equals(TimerOperationModes.CTC)) {
			if (getCtcTop() != null && getCtcTop().equals(CTCTopValues.OCRnA))
				name += " (Top)";
		}
		// Top Value Suffix (FastPWM):
		if (getMode() != null && (getMode().equals(TimerOperationModes.PWM_FAST))) {
			if (getFastPWMTop() != null && getFastPWMTop().equals(PWMTopValues.OCRnA))
				name += " (Top)";
		}
		// Top Value Suffix (Phase Correct PWM):
		if (getMode() != null && (getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT))) {
			if (getPhaseCorrectPWMTop() != null && getPhaseCorrectPWMTop().equals(PWMTopValues.OCRnA))
				name += " (Top)";
		}
		// Top Value Suffix (Phase and Frequency Correct PWM):
		if (getMode() != null && (getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT))) {
			if (getPhaseAndFrequencyCorrectPWMTop() != null && getPhaseAndFrequencyCorrectPWMTop().equals(PhaseAndFrequencyCorrectPWMTopValues.OCRnA))
				name += " (Top)";
		}
		
		if (name == null)
			return "Undeterminded";
		return name;
	}
	public String getOcrBName() {
		if (getTimer().equals(TimerEnum.TIMER0) || getTimer().equals(TimerEnum.TIMER2))
			return "N/A";
		if (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3))
			return "Output Compare Register B";
		return "Undeterminded";
	}
	public String getOcrCName() {
		if (getTimer().equals(TimerEnum.TIMER0) || getTimer().equals(TimerEnum.TIMER2))
			return "N/A";
		if (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3))
			return "Output Compare Register C";
		return "Undeterminded";
	}
	public boolean getOcrChannelsVisibility() {
		if (getTimer() != null && (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3)))
			return true;
		return false;
	}
	public String getDescription() {

		String ls = System.getProperty("line.separator");
		
		// General
		int maxValue = 255;
		if (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3))
			maxValue = 65536;
		int timerNumber = 0;
		if (getTimer().equals(TimerEnum.TIMER1))
			timerNumber = 1;
		if (getTimer().equals(TimerEnum.TIMER2))
			timerNumber = 2;
		if (getTimer().equals(TimerEnum.TIMER3))
			timerNumber = 3;
		String tickDuration = "";
		DecimalFormat df = new DecimalFormat("###.##########");
		DecimalFormatSymbols sym = new DecimalFormatSymbols();
		sym.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(sym);
		tickDuration = df.format((double)1.0/getTickFrequency());
		String tickFrequency = df.format(getTickFrequency());
						
		// Overflow
		String overflowInt = "";
		if (!model.getOverflowInterrupt())
			overflowInt = "not ";
		String overflowTime = df.format(((double) (maxValue + 1)) / getTickFrequency());
		
		// CTC
		String ctcTopReg = "ICR (Input Capture Register)";
		if (getCtcTop() != null && getCtcTop().equals(CTCTopValues.OCRnA)) {
			ctcTopReg = "OCR" + timerNumber;
			if (timerNumber % 2 == 1)
				ctcTopReg += "A";
			ctcTopReg += " (Output Capture Register)";
		}
		
		String d = "";
		
		if (getMode().equals(TimerOperationModes.OVERFLOW)) {
			d = "The Timer will run from 0 to " + maxValue + " (MAX). The counter will overflow and restart at 0." + ls + ls;
			d += "Interrupts are " + overflowInt + "generated on overflow.";
			d += ls + ls + "Timer will run at " + tickFrequency + "Hz (Prescale: " + getPrescale().toString() + ").";
			d += ls + "Each tick will take " + tickDuration + "s." + ls;
			d += "=> Timer will overflow every " + overflowTime + "s.";
		}
		if (getMode().equals(TimerOperationModes.CTC)) {
			d = "The Timer will run from 0 to the Top Value stored in " + ctcTopReg + "." + ls + ls;
			d += "Interrupts can be generated on Compare Match. Output Pins can be toggled for frequency generation.";
			d += ls + ls + "Timer will run at " + tickFrequency + "Hz.";
		}
		if (getMode().equals(TimerOperationModes.PWM_FAST)) {
			d = "The Timer will run in Fast PWM Mode. Period cannot be altered during runtime. Period can be fixed 8Bit, 9Bit, 10Bit and defined in ICR (Input Capture Register) or OCRnA (Output Compare Register). If duty-cycles should be altered during runtime, Phase Correct PWM Mode is recommended instead!";
			d += ls + ls + "Timer will run at " + tickFrequency + "Hz.";
		}
		if (getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT)) {
			d = "The Timer will run in Phase Correct PWM Mode. Period cannot be altered during runtime. Period can be fixed 8Bit, 9Bit, 10Bit and defined in ICR (Input Capture Register) or OCRnA (Output Compare Register). Duty-cycles can be altered during runtime.";
		}
		if (getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT)) {
			d = "The Timer will run in Phase and Frequency Correct PWM Mode. Period can be fixed 8Bit, 9Bit, 10Bit and defined in ICR (Input Capture Register) or OCRnA (Output Compare Register). When period is controlled via ICR or OCRnA, it can be altered during runtime. Duty-cycles can be altered during runtime.";
		}
		
		return d;
	}
	
	public void triggerUpdateView() {
		changes.firePropertyChange("description", null, null);
		changes.firePropertyChange("icrName", null, null);
		changes.firePropertyChange("ocrAName", null, null);
		changes.firePropertyChange("ocrBName", null, null);
		changes.firePropertyChange("ocrCName", null, null);
		changes.firePropertyChange("icrVisibility", null, null);
		changes.firePropertyChange("ocrChannelsVisibility", null, null);
	}
	
	public void setIcrVisibility(boolean b) {	
		// Useless, but required for DataBinding (otherwise: how is OneWay binding done?)
	}
	public void setOcrChannelsVisibility(boolean b) {
		// Useless, but required for DataBinding (otherwise: how is OneWay binding done?)
	}
	
	// Overflow
	public boolean getOverflowInterrupt() {
		return model.getOverflowInterrupt();
	}
	
	public void setOverflowInterrupt(boolean i) {
		this.model.setOverflowInterrupt(i);
		changes.firePropertyChange("overflowInterrupt", null, null);
		triggerUpdateView();
	}
	
	// CTC
	public CTCTopValues getCtcTop() {
		return model.getCtcTop();
	}
	public boolean getCompareInterruptA() {
		return model.getCompareInterruptA();
	}
	public boolean getCompareInterruptB() {
		return model.getCompareInterruptB();
	}
	public boolean getCompareInterruptC() {
		return model.getCompareInterruptC();
	}
	public CTCOutputPinMode getComparePinModeA() {
		return model.getComparePinModeA();
	}
	public CTCOutputPinMode getComparePinModeB() {
		return model.getComparePinModeB();
	}
	public CTCOutputPinMode getComparePinModeC() {
		return model.getComparePinModeC();
	}
	
	public void setCtcTop(CTCTopValues v) {
		model.setCtcTop(v);
		changes.firePropertyChange("ctcTop", null, null);
		triggerUpdateView();
	}
	public void setCompareInterruptA(boolean i) {
		model.setCompareInterruptA(i);
		changes.firePropertyChange("compareInterruptA", null, null);
		triggerUpdateView();
	}
	public void setCompareInterruptB(boolean i) {
		model.setCompareInterruptB(i);
		changes.firePropertyChange("compareInterruptB", null, null);
		triggerUpdateView();
	}
	public void setCompareInterruptC(boolean i) {
		model.setCompareInterruptC(i);
		changes.firePropertyChange("compareInterruptC", null, null);
		triggerUpdateView();
	}
	public void setComparePinModeA(CTCOutputPinMode m) {
		model.setComparePinModeA(m);
		changes.firePropertyChange("comparePinModeA", null, null);
		triggerUpdateView();
	}
	public void setComparePinModeB(CTCOutputPinMode m) {
		model.setComparePinModeB(m);
		changes.firePropertyChange("comparePinModeB", null, null);
		triggerUpdateView();
	}
	public void setComparePinModeC(CTCOutputPinMode m) {
		model.setComparePinModeC(m);
		changes.firePropertyChange("comparePinModeC", null, null);
		triggerUpdateView();
	}
		
	// PWM
	public PWMTopValues getFastPWMTop() {
		return model.getFastPWMTop();
	}
	public PWMTopValues getPhaseCorrectPWMTop() {
		return model.getPhaseCorrectPWMTop();
	}
	public PhaseAndFrequencyCorrectPWMTopValues getPhaseAndFrequencyCorrectPWMTop() {
		return model.getPhaseAndFrequencyCorrectPWMTop();
	}
	
	public void setFastPWMTop(PWMTopValues p) {
		model.setFastPWMTop(p);
		changes.firePropertyChange("fastPWMTop", null, null);
		triggerUpdateView();
	}
	public void setPhaseCorrectPWMTop(PWMTopValues p) {
		model.setPhaseCorrectPWMTop(p);
		changes.firePropertyChange("phaseCorrectPWMTop", null, null);
		triggerUpdateView();
	}
	public void setPhaseAndFrequencyCorrectPWMTop(PhaseAndFrequencyCorrectPWMTopValues p) {
		model.setPhaseAndFrequencyCorrectPWMTop(p);
		changes.firePropertyChange("phaseAndFrequencyCorrectPWMTop", null, null);
		triggerUpdateView();
	}
	public ConfigurationViewModel getParent() {
		return parent;
	}
	public void setParent(ConfigurationViewModel parent) {
		this.parent = parent;
	}
	
	// Private Helper Methods:
	private double getTickFrequency() {
		if (parent != null) {
			int freq = parent.getFrequency();
			double prescale = getPrescale().getNumeric();
			return (double) freq / prescale;
		}
		return 0.0;
	}
	
}
