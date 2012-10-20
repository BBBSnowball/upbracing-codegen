package de.upbracing.shared.timer.model.validation;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.upbracing.shared.timer.model.ConfigurationModel;
import de.upbracing.shared.timer.model.UseCaseModel;
import de.upbracing.shared.timer.model.enums.CTCTopValues;
import de.upbracing.shared.timer.model.enums.PWMDualSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMSingleSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMTopValues;
import de.upbracing.shared.timer.model.enums.PhaseAndFrequencyCorrectPWMTopValues;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;

/**
 * This class validates a {@link UseCaseModel}.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class UseCaseModelValidator extends AValidatorBase {

	private ConfigurationModel parent;
	private UseCaseModel model;
	
	/**
	 * Creates a new instance of the {@link UseCaseModelValidator} class.
	 * @param parent is the {@link ConfigurationModel} this {@link UseCaseModel} belongs to
	 * @param model is the {@link UseCaseModel} which is to be validated
	 */
	public UseCaseModelValidator(ConfigurationModel parent, UseCaseModel model) {
		this.model = model;
		this.parent = parent;
	}
	
	/**
	 * Checks, whether this name is unique within {@code parent}s collection of {@code UseCaseModel}s.
	 * @return {@link ValidationResult#ERROR}, if this name is not unique. {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getNameError() {
		
		// Check for name collisions
		if (isNameColliding())
			return ValidationResult.ERROR;
		
		// Check for valid C identifier
		if (!isNameValidCIdentifier())
			return ValidationResult.ERROR;
		
		return ValidationResult.OK;
	}
	
	/**
	 * Checks, whether the desired ICR period can be reached exactly, only with quantization errors or not at all.
	 * @return {@link ValidationResult#ERROR}, if the desired period is too high for the current configuration.
	 * {@link ValidationResult#WARNING} will be output, when the period is below the maximum period but cannot
	 * be reached exactly. {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getIcrPeriodError() {
		if (!validateMaxPeriod(model.getIcrPeriod()))
			return ValidationResult.ERROR;
		double quantizedPeriod = calculateQuantizedPeriod(model.getIcrPeriod());
		if (quantizedPeriod != model.getIcrPeriod())
			return ValidationResult.WARNING;
		return ValidationResult.OK;
	}
	
	/**
	 * Checks, whether the desired OCRnA period can be reached exactly, only with quantization errors or not at all.
	 * @return {@link ValidationResult#ERROR}, if the desired period is too high for the current configuration.
	 * {@link ValidationResult#WARNING} will be output, when the period is below the maximum period but cannot
	 * be reached exactly. {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getOcrAPeriodError() {
		// Check, if in valid range
		if (!validateMaxPeriod(model.getOcrAPeriod()))
			return ValidationResult.ERROR;
		
		// Check, if greater or equal top value period
		if (!isOcrATopRegister()) 
		{
			// Furthermore, this test is unnecessary, if this is the TOP value register
			if (model.getOcrAPeriod() > getTopPeriod())
				return ValidationResult.WARNING;
		}
		
		
		// Check, if quantization is needed
		double quantizedPeriod = calculateQuantizedPeriod(model.getOcrAPeriod());
		if (quantizedPeriod != model.getOcrAPeriod())
			return ValidationResult.WARNING;
		return ValidationResult.OK;
	}

	/**
	 * Checks, whether the desired OCRnB period can be reached exactly, only with quantization errors or not at all.
	 * @return {@link ValidationResult#ERROR}, if the desired period is too high for the current configuration.
	 * {@link ValidationResult#WARNING} will be output, when the period is below the maximum period but cannot
	 * be reached exactly. {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getOcrBPeriodError() {
		if (!validateMaxPeriod(model.getOcrBPeriod()))
			return ValidationResult.ERROR;
		
		// Check, if greater or equal top value period
		if (model.getOcrBPeriod() > getTopPeriod())
			return ValidationResult.WARNING;
		
		double quantizedPeriod = calculateQuantizedPeriod(model.getOcrBPeriod());
		if (quantizedPeriod != model.getOcrBPeriod())
			return ValidationResult.WARNING;
		return ValidationResult.OK;
	}

	/**
	 * Checks, whether the desired OCRnC period can be reached exactly, only with quantization errors or not at all.
	 * @return {@link ValidationResult#ERROR}, if the desired period is too high for the current configuration.
	 * {@link ValidationResult#WARNING} will be output, when the period is below the maximum period but cannot
	 * be reached exactly. {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getOcrCPeriodError() {
		if (!validateMaxPeriod(model.getOcrCPeriod()))
			return ValidationResult.ERROR;
		
		// Check, if greater or equal top value period
		if (model.getOcrCPeriod() > getTopPeriod())
			return ValidationResult.WARNING;
		
		double quantizedPeriod = calculateQuantizedPeriod(model.getOcrCPeriod());
		if (quantizedPeriod != model.getOcrCPeriod())
			return ValidationResult.WARNING;
		return ValidationResult.OK;
	}
	
	
	/**
	 * In CTC mode, 8Bit timers may only use OCRnA as top value register. 
	 * This method checks, whether this constraint is met.
	 * @return {@link ValidationResult#ERROR}, if non-OCR top value was chosen in combination with an 8Bit timer.
	 * {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getCtcTopError() {
		if ((model.getTimer().equals(TimerEnum.TIMER0)
				|| model.getTimer().equals(TimerEnum.TIMER2))
				&& !model.getCtcTop().equals(CTCTopValues.OCRnA)) {
				return ValidationResult.ERROR;
		}
		return ValidationResult.OK;
	}
	
	/**
	 * In all PWM modes, 8Bit timers may only use the fixed value 255 as top value. 
	 * This method checks, whether this constraint is met.
	 * @return {@link ValidationResult#ERROR}, if another top value than 255 was chosen in combination with an 8Bit timer.
	 * {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getFastPWMTopError() {
		if (model.getTimer().equals(TimerEnum.TIMER0)
				|| model.getTimer().equals(TimerEnum.TIMER2)) {
			if (!model.getFastPWMTop().equals(PWMTopValues.BIT8))
				return ValidationResult.ERROR;
		}
		
		return ValidationResult.OK;
	}
	
	/**
	 * In all PWM modes, 8Bit timers may only use the fixed value 255 as top value. 
	 * This method checks, whether this constraint is met.
	 * @return {@link ValidationResult#ERROR}, if another top value than 255 was chosen in combination with an 8Bit timer.
	 * {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getPhaseCorrectPWMTopError() {
		if (model.getTimer().equals(TimerEnum.TIMER0)
				|| model.getTimer().equals(TimerEnum.TIMER2)) {
			if (!model.getPhaseCorrectPWMTop().equals(PWMTopValues.BIT8))
				return ValidationResult.ERROR;
		}
		
		return ValidationResult.OK;
	}
	
	/**
	 * In all PWM modes, 8Bit timers may only use the fixed value 255 as top value. 
	 * This method checks, whether this constraint is met.
	 * @return {@link ValidationResult#ERROR}, if another top value than 255 was chosen in combination with an 8Bit timer.
	 * {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getPhaseAndFrequencyCorrectPWMTopError() {
		if (model.getTimer().equals(TimerEnum.TIMER0)
				|| model.getTimer().equals(TimerEnum.TIMER2)) {
			if (!model.getPhaseCorrectPWMTop().equals(PWMTopValues.BIT8))
				return ValidationResult.ERROR;
		}
		
		return ValidationResult.OK;
	}
	
	
	/** 
	 * Phase and frequency correct PWM mode is usable with 16Bit timers only.
	 * This method checks, whether this constraint is met.
	 * @return {@link ValidationResult#ERROR}, if phase and frequency correct PWM mode was chosen together with an 8Bit timer.
	 * {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getModeError() {
		
		// Check Mode and Timer collisions
		if (!isModeValidForTimer())
			return ValidationResult.ERROR;
		
		return ValidationResult.OK;
	}
	
	/** 
	 * Phase and frequency correct PWM mode is usable with 16Bit timers only.
	 * This method checks, whether this constraint is met.
	 * @return {@link ValidationResult#ERROR}, if phase and frequency correct PWM mode was chosen together with an 8Bit timer.
	 * {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getTimerError() {
		
		// Check Mode and Timer collisions
		if (!isModeValidForTimer())
			return ValidationResult.ERROR;
		
		return ValidationResult.OK;
	}
	
	
	/**
	 * Toggling of Output Pin A is only valid for 16Bit timers. Additionally, ICR or OCRnA needs to be chosen as top value register. 
	 * @return {@link ValidationResult#ERROR}, if output toggle was selected for an 8Bit timer or if top value register is not
	 * set to ICR or OCRnA while toggle is selected for a 16Bit timer.
	 * {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getSingleSlopePWMPinModeAError() {
		if (model.getSingleSlopePWMPinModeA() != null &&
				model.getSingleSlopePWMPinModeA().equals(PWMSingleSlopeOutputPinMode.TOGGLE)) {
			// 8 Bit Timers are invalid for toggle
			if (model.getTimer().equals(TimerEnum.TIMER0) || model.getTimer().equals(TimerEnum.TIMER2))
				return ValidationResult.ERROR;
			
			if (model.getMode().equals(TimerOperationModes.PWM_FAST)) {
				// Only valid, if WGMn3 is set:
				// -> ICR or OCRnA as Top
				if (!(model.getFastPWMTop().equals(PWMTopValues.ICR) ||
						model.getFastPWMTop().equals(PWMTopValues.OCRnA))) 
					return ValidationResult.ERROR;
			}
		}
		return ValidationResult.OK;
	}
	
	/**
	 * Toggling of Output Pin A is only valid for 16Bit timers. Additionally, ICR or OCRnA needs to be chosen as top value register. 
	 * @return {@link ValidationResult#ERROR}, if output toggle was selected for an 8Bit timer or if top value register is not
	 * set to ICR or OCRnA while toggle is selected for a 16Bit timer.
	 * {@link ValidationResult#OK} will be output otherwise.
	 */
	public ValidationResult getDualSlopePWMPinModeAError() {
		if (model.getDualSlopePWMPinModeA() != null &&
				model.getDualSlopePWMPinModeA().equals(PWMDualSlopeOutputPinMode.TOGGLE)) {
			// 8 Bit Timers are invalid for toggle
			if (model.getTimer().equals(TimerEnum.TIMER0) || model.getTimer().equals(TimerEnum.TIMER2))
				return ValidationResult.ERROR;
			
			if (model.getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT)) {
				// Only valid, if WGMn3 is set:
				// -> ICR or OCRnA as Top
				if (!(model.getPhaseCorrectPWMTop().equals(PWMTopValues.ICR) ||
						model.getPhaseCorrectPWMTop().equals(PWMTopValues.OCRnA))) 
					return ValidationResult.ERROR;
			}
			if (model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT)) {
				// Only valid, if WGMn3 is set:
				// -> ICR or OCRnA as Top
				if (!(model.getPhaseAndFrequencyCorrectPWMTop().equals(PhaseAndFrequencyCorrectPWMTopValues.ICR) ||
						model.getPhaseAndFrequencyCorrectPWMTop().equals(PhaseAndFrequencyCorrectPWMTopValues.OCRnA))) 
					return ValidationResult.ERROR;
			}
		}
		return ValidationResult.OK;
	}
	
	/** Gets the error message describing the name error.
	 * @return the created error message.
	 */
	public String getNameErrorText() {
		// Check for empty name
		if (model.getName().equals(""))
			return "Please choose a name for this configuration.";
		
		// Check for name collisions
		if (isNameColliding())
			return "Each configuration must have a unique name.\nPlease choose another name.";
		
		// Check for valid C identifier
		if (!isNameValidCIdentifier())
			return "The name of this configuration is not a valid C identifier prefix. Only letters, underscores and digits are allowed.\nName may not start with a digit.";
		
		return "";
	}

	/**
	 * Gets the error message describing the ICR period error.
	 * @return the created error message.
	 */
	public String getIcrPeriodErrorText() {
		if (getIcrPeriodError() == ValidationResult.ERROR)
			return getPeriodTooHighErrorText(model.getIcrPeriod());
		if (getIcrPeriodError() == ValidationResult.WARNING)
			return "The Top value " + formatPeriod(model.getIcrPeriod()) + " cannot be reached exactly. It will be quantized to " + formatPeriod(calculateQuantizedPeriod(model.getIcrPeriod())) + ".";
		return "";
	}
	
	/**
	 * Gets the error message describing the OCRnA period error.
	 * @return the created error message.
	 */
	public String getOcrAPeriodErrorText() {
		if (getOcrAPeriodError() == ValidationResult.ERROR)
			return getPeriodTooHighErrorText(model.getOcrAPeriod());
		
		// Check, if greater or equal top value period
		if (!isOcrATopRegister()) 
		{
			// Furthermore, this test is unnecessary, if this is the TOP value register
			if (model.getOcrAPeriod() > getTopPeriod())
				return getPeriodAboveTopValueWarningText(model.getOcrAPeriod());
		}
		
		if (getOcrAPeriodError() == ValidationResult.WARNING) 
		{
			return "The desired period " + formatPeriod(model.getOcrAPeriod()) + " cannot be reached exactly. It will be quantized to " + formatPeriod(calculateQuantizedPeriod(model.getOcrAPeriod())) + ".";
		}
		return "";
	}
	
	/**
	 * Gets the error message describing the OCRnB period error.
	 * @return the created error message.
	 */
	public String getOcrBPeriodErrorText() {
		if (getOcrBPeriodError() == ValidationResult.ERROR)
			return getPeriodTooHighErrorText(model.getOcrBPeriod());
		if (getOcrBPeriodError() == ValidationResult.WARNING)
		{
			// Check, if greater or equal top value period
			if (model.getOcrBPeriod() > getTopPeriod())
				return getPeriodAboveTopValueWarningText(model.getOcrBPeriod());
			
			return "The desired period " + formatPeriod(model.getOcrBPeriod()) + " cannot be reached exactly. It will be quantized to " + formatPeriod(calculateQuantizedPeriod(model.getOcrBPeriod())) + ".";
		}
		return "";
	}
	
	/**
	 * Gets the error message describing the OCRnC period error.
	 * @return the created error message.
	 */
	public String getOcrCPeriodErrorText() {
		if (getOcrCPeriodError() == ValidationResult.ERROR)
			return getPeriodTooHighErrorText(model.getOcrCPeriod());
		if (getOcrCPeriodError() == ValidationResult.WARNING)
		{
			// Check, if greater or equal top value period
			if (model.getOcrCPeriod() > getTopPeriod())
				return getPeriodAboveTopValueWarningText(model.getOcrCPeriod());
			return "The desired period " + formatPeriod(model.getOcrCPeriod()) + " cannot be reached exactly. It will be quantized to " + formatPeriod(calculateQuantizedPeriod(model.getOcrCPeriod())) + ".";
		}
		return "";
	}
	
	/**
	 * Gets the error message describing the CTC top value error.
	 * @return the created error message.
	 */
	public String getCtcTopErrorText() {
		if (getCtcTopError().equals(ValidationResult.ERROR))
			return "For 8Bit Timers only OCR can be used for storing the Top value. Please choose OCR as top value register or select a 16Bit timer for this configuration.";
		return "";
	}
	
	/**
	 * Gets the error message describing the fast PWM top value error.
	 * @return the created error message.
	 */
	public String getFastPWMTopErrorText() {
		if (getFastPWMTopError().equals(ValidationResult.ERROR))
			return "For 8Bit Timers only the fixed value 255 can be used as Top value. Please select 255 as top value or choose a 16Bit timer for this configuration.";
		return "";
	}
	
	/**
	 * Gets the error message describing the phase correct PWM top value error.
	 * @return the created error message.
	 */
	public String getPhaseCorrectPWMTopErrorText() {
		if (getPhaseAndFrequencyCorrectPWMTopError().equals(ValidationResult.ERROR))
			return "For 8Bit Timers only the fixed value 255 can be used as Top value. Please select 255 as top value or choose a 16Bit timer for this configuration.";
		return "";
	}
	
	/**
	 * Gets the error message describing the phase and frequency correct PWM top value error.
	 * @return the created error message.
	 */
	public String getPhaseAndFrequencyCorrectPWMTopErrorText() {
		if (getPhaseAndFrequencyCorrectPWMTopError().equals(ValidationResult.ERROR))
			return "For 8Bit Timers only the fixed value 255 can be used as Top value. Please select 255 as top value or choose a 16Bit timer for this configuration.";
		return "";
	}
	
	/**
	 * Gets the error message describing the mode error.
	 * @return the created error message.
	 */
	public String getModeErrorText() {
		// Check Mode and Timer collisions
		if (!isModeValidForTimer())
			return "This mode is not valid for 8Bit timers. Please choose another mode and/or timer.";
		
		return "";
	}
	
	/**
	 * Gets the error message describing the timer selection error.
	 * @return the created error message.
	 */
	public String getTimerErrorText() {
		// Check Mode and Timer collisions
		if (!isModeValidForTimer())
			return "This mode is not valid for 8Bit timers. Please choose another mode and/or timer.";
		
		return "";
	}
	
	/**
	 * Gets the error message describing the waveform generation error for output pin A in fast PWM operation mode.
	 * @return the created error message.
	 */
	public String getSingleSlopePWMPinModeAErrorText() {
		if (getSingleSlopePWMPinModeAError().equals(ValidationResult.ERROR)) {
			if (model.getTimer().equals(TimerEnum.TIMER0) || model.getTimer().equals(TimerEnum.TIMER2)) {
				return "Output toggle is only valid for 16 Bit timers.";
			}
			return "For output toggle, ICR or OCR" + model.getTimer().ordinal() + "A needs to be top value register.";
		}
		return "";
	}
	
	/**
	 * Gets the error message describing the waveform generation error for output pin A in phase
	 * as well as phase and frequency correct PWM operation mode.
	 * @return the created error message.
	 */
	public String getDualSlopePWMPinModeAErrorText() {
		if (getDualSlopePWMPinModeAError().equals(ValidationResult.ERROR)) {
			if (model.getTimer().equals(TimerEnum.TIMER0) || model.getTimer().equals(TimerEnum.TIMER2)) {
				return "Output toggle is only valid for 16 Bit timers.";
			}
			return "For output toggle, ICR or OCR" + model.getTimer().ordinal() + "A needs to be top value register.";
		}
		return "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateValidation() {
		
		// Very primitive, but will make the Data Binding work...
		changes.firePropertyChange("nameError", null, null);
		changes.firePropertyChange("nameErrorText", null, null);
		changes.firePropertyChange("icrPeriodError", null, null);
		changes.firePropertyChange("icrPeriodErrorText", null, null);
		changes.firePropertyChange("ocrAPeriodError", null, null);
		changes.firePropertyChange("ocrAPeriodErrorText", null, null);
		changes.firePropertyChange("ocrBPeriodError", null, null);
		changes.firePropertyChange("ocrBPeriodErrorText", null, null);
		changes.firePropertyChange("ocrCPeriodError", null, null);
		changes.firePropertyChange("ocrCPeriodErrorText", null, null);
		changes.firePropertyChange("ctcTopError", null, null);
		changes.firePropertyChange("ctcTopErrorText", null, null);
		changes.firePropertyChange("fastPWMTopError", null, null);
		changes.firePropertyChange("fastPWMTopErrorText", null, null);
		changes.firePropertyChange("phaseCorrectPWMTopError", null, null);
		changes.firePropertyChange("phaseCorrectPWMTopErrorText", null, null);
		changes.firePropertyChange("phaseAndFrequencyCorrectPWMTopError", null, null);
		changes.firePropertyChange("phaseAndFrequencyCorrectPWMTopErrorText", null, null);
		changes.firePropertyChange("modeError", null, null);
		changes.firePropertyChange("modeErrorText", null, null);
		changes.firePropertyChange("timerError", null, null);
		changes.firePropertyChange("timerErrorText", null, null);
		changes.firePropertyChange("singleSlopePWMPinModeAError", null, null);
		changes.firePropertyChange("singleSlopePWMPinModeAErrorText", null, null);
		changes.firePropertyChange("dualSlopePWMPinModeAError", null, null);
		changes.firePropertyChange("dualSlopePWMPinModeAErrorText", null, null);
	}
	
	/**
	 * Calculates the register value for a given period.
	 * @param period is the desired period in seconds, for which the corresponding register value is calculated.
	 * Operation mode is regarded. This means, that for phase as well as phase and frequency correct
	 * PWM the register value will be half of the value in other modes. (See datasheet: Dual-Slope PWM modes)
	 * @return the register value for the given period.
	 */
	public int calculateRegisterValue(double period) {
		
		int frequency = parent.getFrequency();
		// Get Timer Tick Rate
		int prescale = model.getPrescale().getNumeric();
		double timerFreq = ((double) frequency / (double) prescale);
		double timerPeriod = 1.0 / timerFreq;
		// Calculate Register Value From Desired Period
		int registerValue = (int) Math.floor((period / timerPeriod) + 0.5d) - 1;
		if (model.getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT)
				|| model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT))
			registerValue = (int) Math.floor(((period * frequency) / (2 * prescale)) + 0.5d);
		
		return registerValue;
	}
	
	/**
	 * Calculates the quantized period for a given desired period.
	 * @param period is the desired period in seconds.
	 * @return the actual, quantized period that will be stored in the register after code generation.
	 */
	public double calculateQuantizedPeriod(double period) {
		int regValue = calculateRegisterValue(period);
		double quantizedValue = calculatePeriodForRegisterValue(regValue);
		return quantizedValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValidationResult validate() {
	
		// Aggregate validation statuses
		ArrayList<ValidationResult> errors = new ArrayList<ValidationResult>();
		errors.add(getNameError());
		errors.add(getTimerError());
		errors.add(getModeError());
		
			// Period Errors:
			if (model.getMode().equals(TimerOperationModes.CTC)
					|| model.getMode().equals(TimerOperationModes.PWM_FAST)
					|| model.getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT)
					|| model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT)) {
				
				errors.add(getOcrAPeriodError());
				
				if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3)) {
					errors.add(getOcrBPeriodError());
					errors.add(getOcrCPeriodError());
				}
			}
			
			// Mode specific Errors:
			if (model.getMode().equals(TimerOperationModes.CTC)) {
				if (model.getCtcTop().equals(CTCTopValues.ICR))
					errors.add(getIcrPeriodError());
				errors.add(getCtcTopError());
			}
			else if (model.getMode().equals(TimerOperationModes.PWM_FAST)) {
				if (model.getFastPWMTop().equals(PWMTopValues.ICR))
					errors.add(getIcrPeriodError());
				errors.add(getFastPWMTopError());
			}
			else if (model.getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT)) {
				if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.ICR))
					errors.add(getIcrPeriodError());
				errors.add(getPhaseCorrectPWMTopError());
			}
			else if (model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT)) {
				if (model.getPhaseAndFrequencyCorrectPWMTop().equals(PhaseAndFrequencyCorrectPWMTopValues.ICR))
					errors.add(getIcrPeriodError());
				errors.add(getPhaseAndFrequencyCorrectPWMTopError());
			}
		
		// Return the worst ValidationResult
		ValidationResult max = ValidationResult.OK;
		for (ValidationResult r: errors) {
			if (r.equals(ValidationResult.WARNING))
				max = ValidationResult.WARNING;
			else if (r.equals(ValidationResult.ERROR))
				return ValidationResult.ERROR;
		}
		return max;
	}

	private boolean validateMaxPeriod(double period) {
		// Get Maximum Register Value
		int maxValue = getMaximumValue();
		double registerValue = calculateRegisterValue(period);
		if (Math.round(registerValue) > maxValue)
			return false;
		return true;
	}

	private double calculatePeriodForRegisterValue(int registerValue) {
		// registerValue = period / timerPeriod
		//               = period * timerFreq
		//               = period * (frequency / prescale)
		// <=> period = registerValue / (frequency / prescale)

		int frequency = parent.getFrequency();
		int prescale = model.getPrescale().getNumeric();
		double timerFreq = ((double) frequency / (double) prescale);
		
		if (model.getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT)
				|| model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT)) 
		{
			double dualSlopePeriod = (double) (2 * registerValue * prescale) / frequency;
			return dualSlopePeriod;
		}
		else
			return (double) (registerValue + 1) / timerFreq;
	}
	
	private int getMaximumValue() {
		int maxValue = 255;
		if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3))
			maxValue = 65535;
		return maxValue;
	}
	
	private String getPeriodTooHighErrorText(double period) {
				
		String text = "The Top value is too high for this timer frequency (" + 
				calculateRegisterValue(period) + " > " + getMaximumValue() +
				", max. period is " + formatPeriod(calculatePeriodForRegisterValue(getMaximumValue())) + ").\n" +
				"Please try to increase the prescale factor or choose a smaller period.";
	
		if (model.getTimer().equals(TimerEnum.TIMER0) || model.getTimer().equals(TimerEnum.TIMER2))
			text += " Additionally, you can choose a 16Bit timer for longer maximum periods.";
		
		return text;
	}
	
	private String getPeriodAboveTopValueWarningText(double period) {
		
		String topReg = "";
		if (model.getMode().equals(TimerOperationModes.CTC))
		{
			if (model.getCtcTop().equals(CTCTopValues.ICR))
				topReg = "ICR: ";
		}
		if (model.getMode().equals(TimerOperationModes.PWM_FAST))
		{
			if (model.getFastPWMTop().equals(PWMTopValues.ICR))
				topReg = "ICR: ";
			if (model.getFastPWMTop().equals(PWMTopValues.BIT8))
				topReg = "constant 8bit: ";
			if (model.getFastPWMTop().equals(PWMTopValues.BIT9))
				topReg = "constant 9bit: ";
			if (model.getFastPWMTop().equals(PWMTopValues.BIT10))
				topReg = "constant 10bit: ";
		}
		if (model.getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT))
		{
			if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.ICR))
				topReg = "ICR: ";
			if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.BIT8))
				topReg = "constant 8bit: ";
			if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.BIT9))
				topReg = "constant 9bit: ";
			if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.BIT10))
				topReg = "constant 10bit: ";
		}
		if (model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT))
		{
			if (model.getPhaseAndFrequencyCorrectPWMTop().equals(PWMTopValues.ICR))
				topReg = "ICR: ";
		}
		if (isOcrATopRegister())
			topReg = "OCRnA: ";
		
		return "The desired period " + formatPeriod(period) + " is longer than the top period value "
				+ "(" + topReg + formatPeriod(getTopPeriod()) + ").";
	}
	
	private static String formatPeriod(double period) {
		DecimalFormat f = new DecimalFormat("###.##########");
		if (period > 1)
			return f.format(period) + "s";
		else if (period > 1e-3)
			return f.format(period * 1e3) + " ms";
		else if (period > 1e-6)
			return f.format(period * 1e6) + " us";
		else
			return f.format(period * 1e9) + " ns";
	}
	
	private boolean isNameColliding() {
		// Check for name collisions
		for (UseCaseModel m: parent.getConfigurations()) {
			if (m != model && m.getName().equals(model.getName()))
				return true;
		}
		return false;
	}
	
	private boolean isNameValidCIdentifier() {
		// Check for valid C identifier
		// -> Only digits and letters + underscore
		// -> May not start with digit
		String regex = "^([_A-Za-z][_A-Za-z0-9]*)";
		if (model.getName().matches(regex))
			return true;
		return false;
	}
	
	private boolean isModeValidForTimer() {
		
		boolean isSixteenBit = false;
		if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3))
			isSixteenBit = true;
		
		// Invalid, if Phase and Frequency Correct Mode on 8Bit Timer!
		if (!isSixteenBit && model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT))
			return false;
		
		return true;
	}
	
	private boolean isOcrATopRegister() 
	{
		if (model.getMode().equals(TimerOperationModes.CTC))
		{
			if (model.getCtcTop().equals(CTCTopValues.OCRnA))
				return true;
		}
		if (model.getMode().equals(TimerOperationModes.PWM_FAST))
		{
			if (model.getFastPWMTop().equals(PWMTopValues.OCRnA))
				return true;
		}
		if (model.getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT))
		{
			if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.OCRnA))
				return true;
		}
		if (model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT))
		{
			if (model.getPhaseAndFrequencyCorrectPWMTop().equals(PhaseAndFrequencyCorrectPWMTopValues.OCRnA))
				return true;
		}
		
		return false;
	}
	
	private double getTopPeriod() {
		if (model.getMode().equals(TimerOperationModes.CTC))
		{
			if (model.getCtcTop().equals(CTCTopValues.ICR))
				return calculateQuantizedPeriod(model.getIcrPeriod());
			if (model.getCtcTop().equals(CTCTopValues.OCRnA))
				return calculateQuantizedPeriod(model.getOcrAPeriod());
		}
		if (model.getMode().equals(TimerOperationModes.PWM_FAST))
		{
			if (model.getFastPWMTop().equals(PWMTopValues.ICR))
				return calculateQuantizedPeriod(model.getIcrPeriod());
			if (model.getFastPWMTop().equals(PWMTopValues.OCRnA))
				return calculateQuantizedPeriod(model.getOcrAPeriod());
			if (model.getFastPWMTop().equals(PWMTopValues.BIT8))
				return calculatePeriodForRegisterValue(255);
			if (model.getFastPWMTop().equals(PWMTopValues.BIT9))
				return calculatePeriodForRegisterValue(511);
			if (model.getFastPWMTop().equals(PWMTopValues.BIT10))
				return calculatePeriodForRegisterValue(1023);
		}
		if (model.getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT))
		{
			if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.ICR))
				return calculateQuantizedPeriod(model.getIcrPeriod());
			if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.OCRnA))
				return calculateQuantizedPeriod(model.getOcrAPeriod());
			if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.BIT8))
				return calculatePeriodForRegisterValue(255);
			if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.BIT9))
				return calculatePeriodForRegisterValue(511);
			if (model.getPhaseCorrectPWMTop().equals(PWMTopValues.BIT10))
				return calculatePeriodForRegisterValue(1023);
		}
		if (model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT))
		{
			if (model.getPhaseAndFrequencyCorrectPWMTop().equals(PhaseAndFrequencyCorrectPWMTopValues.ICR))
				return calculateQuantizedPeriod(model.getIcrPeriod());
			if (model.getPhaseAndFrequencyCorrectPWMTop().equals(PhaseAndFrequencyCorrectPWMTopValues.OCRnA))
				return calculateQuantizedPeriod(model.getOcrAPeriod());
		}
		
		return 0.0;
	}
}
