package de.upbracing.configurationeditor.timer.viewmodel;

import de.upbracing.shared.timer.model.ConfigurationModel;
import de.upbracing.shared.timer.model.UseCaseModel;
import de.upbracing.shared.timer.model.enums.CTCOutputPinMode;
import de.upbracing.shared.timer.model.enums.CTCTopValues;
import de.upbracing.shared.timer.model.enums.PWMDualSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMSingleSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMTopValues;
import de.upbracing.shared.timer.model.enums.PhaseAndFrequencyCorrectPWMTopValues;
import de.upbracing.shared.timer.model.enums.PrescaleFactors;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;
import de.upbracing.shared.timer.model.validation.UseCaseModelValidator;

public class UseCaseViewModel extends AViewModelBase {
	
	private UseCaseModel model;
	private ConfigurationViewModel parent;
	private UseCaseModelValidator validator;
	private String descriptionCache;
	private String ls;
		
	// Constructor:
	public UseCaseViewModel(UseCaseModel m, ConfigurationModel parent) {
		this.ls = System.getProperty("line.separator");
		this.model = m;
		this.descriptionCache = "";
		this.validator = new UseCaseModelValidator(parent, m);
	}

	// Getter for underlying model
	public UseCaseModel getModel() {
		return model;
	}
	
	// Getter for Validator
	public UseCaseModelValidator getValidator() {
		return validator;
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
		if (!model.getName().equals(n)) {
			model.setName(n);
			changes.firePropertyChange("name", null, null);
			validator.updateNameValidation();
			
			// Alert the parent, that the name of one of its child has changed
			// -> this is needed to check for name collisions
			getParent().updateUseCaseNameValidation();
		}
	}
	public void setMode(TimerOperationModes m) {
		if (!model.getMode().equals(m)) {
			this.model.setMode(m);
			changes.firePropertyChange("mode", null, null);
			triggerUpdateView();
			validator.updateTimerModeValidation();
		}
	}
	public void setTimer(TimerEnum t) {
		if (!model.getTimer().equals(t)) {
			this.model.setTimer(t);
			changes.firePropertyChange("timer", null, null);
			triggerUpdateView();
			triggerUpdateChannelsVisibility();
			validator.updateTimerModeValidation();
		}
	}
	public void setPrescale(PrescaleFactors p) {
		if (!model.getPrescale().equals(p)) {
			this.model.setPrescale(p);
			changes.firePropertyChange("prescale", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
	}
	public void setIcrPeriod(double f) {
		if (model.getIcrPeriod() != f) {
			this.model.setIcrPeriod(f);
			changes.firePropertyChange("icrPeriod", null, null);
			validator.updateValidation();
			triggerUpdateView();
		}
	}
	public void setOcrAPeriod(double f) {
		if (model.getOcrAPeriod() != f) {
			this.model.setOcrAPeriod(f);
			changes.firePropertyChange("ocrAPeriod", null, null);
			validator.updateValidation();
			triggerUpdateView();
		}
	}
	public void setOcrBPeriod(double f) {
		if (model.getOcrBPeriod() != f) {
			this.model.setOcrBPeriod(f);
			changes.firePropertyChange("ocrBPeriod", null, null);
			validator.updateValidation();
			triggerUpdateView();
		}
	}
	public void setOcrCPeriod(double f) {
		if (model.getOcrCPeriod() != f) {
			this.model.setOcrCPeriod(f);
			changes.firePropertyChange("ocrCPeriod", null, null);
			validator.updateValidation();
			triggerUpdateView();
		}
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

		// General
		int maxValue = 255;
		if (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3))
			maxValue = 65535;
		int timerNumber = getTimer().ordinal();
		String tickDuration = UseCaseModelValidator.formatPeriod((double)1.0/getTickFrequency());
		String tickFrequency = UseCaseModelValidator.formatFrequency((double)1.0/getTickFrequency());
		
		// 8 or 16 Bit?
		int bit = 8;
		if (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3))
			bit = 16;
		
		// Text
		String text = "Timer Number: " + timerNumber + " (" + bit + "Bit)" + ls
				+ "Frequency: " + tickFrequency + " (with prescale divisor " + getPrescale().getNumeric() + ")" + ls
				+ "Resolution: " + tickDuration + ls
				+ "Max. Counter Value: " + maxValue + ls + ls;
		
		// CTC
		String d = text;
		
		if (getMode().equals(TimerOperationModes.OVERFLOW)) {
			d += getOverflowDescription(maxValue);
		}
		if (getMode().equals(TimerOperationModes.CTC)) {
			d += getCtcDescription();
		}
		if (getMode().equals(TimerOperationModes.PWM_FAST)) {
			d += getPWMDescription();
		}
		if (getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT)) {
			d += getPWMDescription();
		}
		if (getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT)) {
			d += getPWMDescription();
		}
		
		return d;
	}
	
	public void triggerUpdateView() {
		// Only update description, if text has changed!
		String newDescString = getDescription();
		if (!newDescString.equals(descriptionCache)) {
			changes.firePropertyChange("description", descriptionCache, newDescString);
			descriptionCache = newDescString;
		}
	}
	
	public void triggerUpdateChannelsVisibility() {
		// Trigger new channel names
		changes.firePropertyChange("icrName", null, null);
		changes.firePropertyChange("ocrAName", null, null);
		changes.firePropertyChange("ocrBName", null, null);
		changes.firePropertyChange("ocrCName", null, null);
		// Trigger visbilities		
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
		if (model.getOverflowInterrupt() != i) {
			this.model.setOverflowInterrupt(i);
			changes.firePropertyChange("overflowInterrupt", null, null);
			triggerUpdateView();
		}
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
		if (!model.getCtcTop().equals(v)) {
			model.setCtcTop(v);
			changes.firePropertyChange("ctcTop", null, null);
			triggerUpdateView();
			triggerUpdateChannelsVisibility();
			validator.updateValidation();
		}
	}
	public void setCompareInterruptA(boolean i) {
		if (model.getCompareInterruptA() != i) {
			model.setCompareInterruptA(i);
			changes.firePropertyChange("compareInterruptA", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
	}
	public void setCompareInterruptB(boolean i) {
		if (model.getCompareInterruptB() != i) {	
			model.setCompareInterruptB(i);
			changes.firePropertyChange("compareInterruptB", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
	}
	public void setCompareInterruptC(boolean i) {
		if (model.getCompareInterruptC() != i) {
			model.setCompareInterruptC(i);
			changes.firePropertyChange("compareInterruptC", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
	}
	public void setComparePinModeA(CTCOutputPinMode m) {
		if (!model.getComparePinModeA().equals(m)) {
			model.setComparePinModeA(m);
			changes.firePropertyChange("comparePinModeA", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
	}
	public void setComparePinModeB(CTCOutputPinMode m) {
		if (!model.getComparePinModeB().equals(m)) {	
			model.setComparePinModeB(m);
			changes.firePropertyChange("comparePinModeB", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
	}
	public void setComparePinModeC(CTCOutputPinMode m) {
		if (!model.getComparePinModeC().equals(m)) {
			model.setComparePinModeC(m);
			changes.firePropertyChange("comparePinModeC", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
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
	public ConfigurationViewModel getParent() {
		return parent;
	}
	public PWMSingleSlopeOutputPinMode getSingleSlopePWMPinModeA() {
		return model.getSingleSlopePWMPinModeA();
	}
	public PWMSingleSlopeOutputPinMode getSingleSlopePWMPinModeB() {
		return model.getSingleSlopePWMPinModeB();
	}
	public PWMSingleSlopeOutputPinMode getSingleSlopePWMPinModeC() {
		return model.getSingleSlopePWMPinModeC();
	}
	public PWMDualSlopeOutputPinMode getDualSlopePWMPinModeA() {
		return model.getDualSlopePWMPinModeA();
	}
	public PWMDualSlopeOutputPinMode getDualSlopePWMPinModeB() {
		return model.getDualSlopePWMPinModeB();
	}
	public PWMDualSlopeOutputPinMode getDualSlopePWMPinModeC() {
		return model.getDualSlopePWMPinModeC();
	}
	
	public void setFastPWMTop(PWMTopValues p) {
		if (!model.getFastPWMTop().equals(p)) {
			model.setFastPWMTop(p);
			changes.firePropertyChange("fastPWMTop", null, null);
			triggerUpdateView();
			triggerUpdateChannelsVisibility();
			validator.updateValidation();
		}
	}
	public void setPhaseCorrectPWMTop(PWMTopValues p) {
		if (!model.getPhaseCorrectPWMTop().equals(p)) {
			model.setPhaseCorrectPWMTop(p);
			changes.firePropertyChange("phaseCorrectPWMTop", null, null);
			triggerUpdateView();
			triggerUpdateChannelsVisibility();
			validator.updateValidation();
		}
	}
	public void setPhaseAndFrequencyCorrectPWMTop(PhaseAndFrequencyCorrectPWMTopValues p) {
		if (!model.getPhaseAndFrequencyCorrectPWMTop().equals(p)) {	
			model.setPhaseAndFrequencyCorrectPWMTop(p);
			changes.firePropertyChange("phaseAndFrequencyCorrectPWMTop", null, null);
			triggerUpdateView();
			triggerUpdateChannelsVisibility();
			validator.updateValidation();
		}
	}
	public void setParent(ConfigurationViewModel parent) {
		this.parent = parent;
	}
	public void setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode m)
	{
		if (!model.getSingleSlopePWMPinModeA().equals(m)) {
			model.setSingleSlopePWMPinModeA(m);
			changes.firePropertyChange("singleSlopePWMPinModeA", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
	}
	public void setSingleSlopePWMPinModeB(PWMSingleSlopeOutputPinMode m)
	{
		if (!model.getSingleSlopePWMPinModeB().equals(m)) {
			model.setSingleSlopePWMPinModeB(m);
			changes.firePropertyChange("singleSlopePWMPinModeB", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}			
	}
	public void setSingleSlopePWMPinModeC(PWMSingleSlopeOutputPinMode m)
	{
		if (!model.getSingleSlopePWMPinModeC().equals(m)) {
			model.setSingleSlopePWMPinModeC(m);
			changes.firePropertyChange("singleSlopePWMPinModeC", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
	}
	public void setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode m)
	{
		if (!model.getDualSlopePWMPinModeA().equals(m)) { 
			model.setDualSlopePWMPinModeA(m);
			changes.firePropertyChange("dualSlopePWMPinModeA", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
	}
	public void setDualSlopePWMPinModeB(PWMDualSlopeOutputPinMode m)
	{
		if (!model.getDualSlopePWMPinModeB().equals(m)) {
			model.setDualSlopePWMPinModeB(m);
			changes.firePropertyChange("dualSlopePWMPinModeB", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
	}
	public void setDualSlopePWMPinModeC(PWMDualSlopeOutputPinMode m)
	{
		if (!model.getDualSlopePWMPinModeC().equals(m)) {
			model.setDualSlopePWMPinModeC(m);
			changes.firePropertyChange("dualSlopePWMPinModeC", null, null);
			triggerUpdateView();
			validator.updateValidation();
		}
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
	
	// Private Description Helpers:
	private String getOverflowDescription(int maxValue) {
		
		String overflowTime = UseCaseModelValidator.formatPeriod(getValidator().calculatePeriodForRegisterValue(getValidator().getMaximumValue()));
		String text = "The Timer will run from 0 to " + maxValue + " (MAX). The counter will overflow and restart at 0." + ls + ls;
		text += "=> Timer will overflow every " + overflowTime + ".";
		if (model.getOverflowInterrupt())
			text += ls + "=> Interrupts are generated on overflow.";
		
		return text.trim();
	}
	
	private String getCtcDescription() {
		
		String ctcTopReg = "ICR (Input Capture Register)";
		if (getCtcTop() != null && getCtcTop().equals(CTCTopValues.OCRnA)) {
			ctcTopReg = "OCR" + getTimer().ordinal();
			if (getTimer().ordinal() % 2 == 1)
				ctcTopReg += "A";
			ctcTopReg += " (Output Capture Register)";
		}
		double topPeriod = getValidator().getTopPeriod();

		// Top value text:
		String text = "The Timer will run from 0 to the value stored in " + ctcTopReg + " (TOP). The counter will restart at 0." + ls + ls;
		text += "=> Timer will be reset every " + UseCaseModelValidator.formatPeriod(getValidator().getTopPeriod()) + "." + ls;
		
		// Interrupts:
		if (getCompareInterruptA())
		    text += getCtcInterruptDescription(getOcrAPeriod(), topPeriod);
		if (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3)) {
			if (getCompareInterruptB())
			    text += getCtcInterruptDescription(getOcrBPeriod(), topPeriod);
			if (getCompareInterruptC())
			    text += getCtcInterruptDescription(getOcrCPeriod(), topPeriod);
		}
		if (getCompareInterruptA() || getCompareInterruptB() || getCompareInterruptC())
			text += ls;
		
		// Output modes:
		if (getComparePinModeA() != null && !getComparePinModeA().equals(CTCOutputPinMode.NORMAL)) {
			text += ls + "=> Channel A: " + getComparePinModeA().toString().toLowerCase() + ".";
		}
		if (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3)) {
			if (getComparePinModeB() != null && !getComparePinModeB().equals(CTCOutputPinMode.NORMAL)) {
				text += ls + "=> Channel B: " + getComparePinModeB().toString().toLowerCase() + ".";	
			}
			if (getComparePinModeC() != null && !getComparePinModeC().equals(CTCOutputPinMode.NORMAL)) {
				text += ls + "=> Channel C: " + getComparePinModeC().toString().toLowerCase() + "."; 
			}
		}
		
		return text.trim();
	}
	
	private String getCtcInterruptDescription(double channelPeriod, double topPeriod) {
		double quantizedChannelPeriod = getValidator().calculateQuantizedPeriod(channelPeriod);
		String text = ls + "=> Interrupt is generated ";
		if (quantizedChannelPeriod == topPeriod) {
			text += "on timer reset.";
		} else {
			text += UseCaseModelValidator.formatPeriod(quantizedChannelPeriod) + " after timer reset.";
		}
		return text;
	}

	private String getPWMDescription() {
		
		String text = "The Timer will run in " + getMode().toString() + ". ";
		
		if (getMode().equals(TimerOperationModes.PWM_FAST) || getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT)) {
			text += "Period cannot be altered during runtime.";
		}
		
		// Base rate:
		text += ls + ls + "=> PWM period is " +  UseCaseModelValidator.formatPeriod(getValidator().getTopPeriod()) + "." + ls;
		text += "=> PWM base rate is " +  UseCaseModelValidator.formatFrequency(getValidator().getTopPeriod())  + "." + ls;
		
		// Channels:
		if (getMode().equals(TimerOperationModes.PWM_FAST)) {
			if (getSingleSlopePWMPinModeA() != null && !getSingleSlopePWMPinModeA().equals(PWMSingleSlopeOutputPinMode.NORMAL)) {
				text += ls + getPWMOutputPinDescription(getSingleSlopePWMPinModeA(), "A", getOcrAPeriod());
			}
			if (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3)) {
				if (getSingleSlopePWMPinModeB() != null && !getSingleSlopePWMPinModeB().equals(PWMSingleSlopeOutputPinMode.NORMAL)) {
					text += ls + getPWMOutputPinDescription(getSingleSlopePWMPinModeB(), "B", getOcrBPeriod());
				}
				if (getSingleSlopePWMPinModeC() != null && !getSingleSlopePWMPinModeC().equals(PWMSingleSlopeOutputPinMode.NORMAL)) {
					text += ls + getPWMOutputPinDescription(getSingleSlopePWMPinModeC(), "C", getOcrCPeriod());
				}
			}
		} else if (getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT) || getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT)) {
			if (getDualSlopePWMPinModeA() != null && !getDualSlopePWMPinModeA().equals(PWMDualSlopeOutputPinMode.NORMAL)) {
				text += ls + getPWMOutputPinDescription(getDualSlopePWMPinModeA(), "A", getOcrAPeriod());
			}
			if (getTimer().equals(TimerEnum.TIMER1) || getTimer().equals(TimerEnum.TIMER3)) {
				if (getDualSlopePWMPinModeB() != null && !getDualSlopePWMPinModeB().equals(PWMDualSlopeOutputPinMode.NORMAL)) {
					text += ls + getPWMOutputPinDescription(getDualSlopePWMPinModeB(), "B", getOcrBPeriod());
				}
				if (getDualSlopePWMPinModeC() != null && !getDualSlopePWMPinModeC().equals(PWMDualSlopeOutputPinMode.NORMAL)) {
					text += ls + getPWMOutputPinDescription(getDualSlopePWMPinModeC(), "C", getOcrCPeriod());
				}
			}
		}
		
		return text.trim();
	}
	
	private String getPWMOutputPinDescription(PWMSingleSlopeOutputPinMode pinMode, String channel, double period) {
		String txt = "=> Channel " + channel + ": " + pinMode.toString().toLowerCase();
		if (!pinMode.equals(PWMSingleSlopeOutputPinMode.TOGGLE)) {
			txt += " (duty-cycle: " + UseCaseModelValidator.formatPeriod(getValidator().calculateQuantizedPeriod(period)) + ")";
		}
		txt += ".";
		return txt;
	}
	private String getPWMOutputPinDescription(PWMDualSlopeOutputPinMode pinMode, String channel, double period) {
		String txt = "=> Channel " + channel + ": " + pinMode.toString().toLowerCase();
		if (!pinMode.equals(PWMDualSlopeOutputPinMode.TOGGLE)) {
			txt += " (duty-cycle: " + UseCaseModelValidator.formatPeriod(getValidator().calculateQuantizedPeriod(period)) + ")";
		}
		txt += ".";
		return txt;
	}
}
