/**
 * 
 */
package de.upbracing.shared.timer.model.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import de.upbracing.shared.timer.model.*;
import de.upbracing.shared.timer.model.enums.CTCTopValues;
import de.upbracing.shared.timer.model.enums.PWMDualSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMSingleSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMTopValues;
import de.upbracing.shared.timer.model.enums.PhaseAndFrequencyCorrectPWMTopValues;
import de.upbracing.shared.timer.model.enums.PrescaleFactors;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;
import de.upbracing.shared.timer.model.validation.ConfigurationModelValidator;
import de.upbracing.shared.timer.model.validation.UseCaseModelValidator;
import de.upbracing.shared.timer.model.validation.ValidationResult;

/*
 * This class tests ConfigurationModelValidator and UseCaseModelValidator
 * for correctness.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class ValidatorTest 
{
	private ConfigurationModel model;
	private ConfigurationModelValidator validator;
	
	@Before
	/*
	 * Creates a new ConfigurationModel and ConfigurationModelValidator,
	 * which is used in each test case. This method is invoked before each test.
	 */
	public void initModel() 
	{
		// Create a new ConfigurationModel
		model = new ConfigurationModel();
		
		// Create a validator for this model
		validator = new ConfigurationModelValidator(model);
	}
	
	@Test
	/*
	 * Tests the frequency validation for correctness.
	 * See the inline comments for details.
	 */
	public void testFrequencyValidation() 
	{
		// Assert, that a valid default frequency was chosen
		assertTrue(model.getFrequency() >= 1);
		assertTrue(model.getFrequency() <= 16000000);
		assertEquals(ValidationResult.OK, validator.getFrequencyError());
		
		// Set to a value too low for the processor
		model.setFrequency(0);
		assertEquals(0, model.getFrequency());
		assertEquals(ValidationResult.ERROR, validator.getFrequencyError());
		
		// Set to a value too high for the processor
		model.setFrequency(16000001);
		assertEquals(16000001, model.getFrequency());
		assertEquals(ValidationResult.ERROR, validator.getFrequencyError());
		
		// Set it to a valid value again and assert, that everything is fine now
		model.setFrequency(4000000);
		assertEquals(4000000, model.getFrequency());
		assertEquals(ValidationResult.OK, validator.getFrequencyError());
	}

	@Test
	/*
	 * Tests the name validation for correctness.
	 * See the inline comments for details.
	 */
	public void testNameValidation()
	{
		// Assert, that no UseCaseModels are present
		assertEquals(0, model.getConfigurations().size());
		
		// Create two UseCaseModels with default name
		// -> this should produce a validation error
		UseCaseModel uc1 = model.addConfiguration();
		UseCaseModel uc2 = model.addConfiguration();
		
		// Create validators for these UseCaseModels
		UseCaseModelValidator v1 = new UseCaseModelValidator(model, uc1);
		UseCaseModelValidator v2 = new UseCaseModelValidator(model, uc2);
		
		// Now see, whether this produces the expected name error:
		assertEquals(ValidationResult.ERROR, v1.getNameError());
		assertEquals(ValidationResult.ERROR, v2.getNameError());
		
		// Now change the name of uc1 to "newName"
		uc1.setName("newName");
		assertEquals("newName", uc1.getName());
		
		// The name collision should be resolved now
		assertEquals(ValidationResult.OK, v1.getNameError());
		assertEquals(ValidationResult.OK, v2.getNameError());
		
		// Also change the name of uc2 to "newName"
		uc2.setName("newName");
		assertEquals("newName", uc2.getName());
		
		// Now there should be a name collision again
		assertEquals(ValidationResult.ERROR, v1.getNameError());
		assertEquals(ValidationResult.ERROR, v2.getNameError());
	}
	
	@Test
	/*
	 * Tests the period validation of 8Bit timers in single slope mode for correctness.
	 * See the inline comments for details.
	 */
	public void testEightBitSingleSlopePeriodValidation()
	{
		// Create a new UseCaseModel
		UseCaseModel uc = model.addConfiguration();
		
		// Create a validator for this UseCaseModel
		UseCaseModelValidator v = new UseCaseModelValidator(model, uc);
		
		// This will test register calculation and validation for Single-Slope mode:
		// Scenario:
		// - 4MHz processing speed
		model.setFrequency(4000000);
		assertEquals(4000000, model.getFrequency());
		// - Fast PWM Mode (Single Slope)
		uc.setMode(TimerOperationModes.PWM_FAST);
		assertEquals(TimerOperationModes.PWM_FAST, uc.getMode());
		// - Timer 0 (8Bit)
		uc.setTimer(TimerEnum.TIMER0);
		assertEquals(TimerEnum.TIMER0, uc.getTimer());
		// - Prescale divisor 1024
		uc.setPrescale(PrescaleFactors.ONETHOUSANDANDTWENTYFOUR);
		assertEquals(PrescaleFactors.ONETHOUSANDANDTWENTYFOUR, uc.getPrescale());
		assertEquals(1024, uc.getPrescale().getNumeric());
		// - ICR: 0.04992s (OK)
		//   -> No quantization error
		//   -> Expected register value: 194
		uc.setIcrPeriod(0.04992);
		assertEquals(0.04992, uc.getIcrPeriod(), 0.0);
		assertEquals(0.04992, v.calculateQuantizedPeriod(uc.getIcrPeriod()), 0.0);
		assertEquals(ValidationResult.OK, v.getIcrPeriodError());
		assertEquals(194, v.calculateRegisterValue(uc.getIcrPeriod()));
		// - OCRA: 0.05s (WARNING)
		//   -> Quantization time: 0.04992s
		//   -> Expected register value: 194
		uc.setOcrAPeriod(0.05);
		assertEquals(0.05, uc.getOcrAPeriod(), 0.0);
		assertFalse(((Double) v.calculateQuantizedPeriod(uc.getOcrAPeriod())).equals(0.05));
		assertEquals(0.04992, v.calculateQuantizedPeriod(uc.getOcrAPeriod()), 0.0);
		assertEquals(ValidationResult.WARNING, v.getOcrAPeriodError());
		assertEquals(194, v.calculateRegisterValue(v.calculateQuantizedPeriod(uc.getOcrAPeriod())));
		// - ICR: 0.1s (ERROR)
		//   -> Quantized time: 0.99968s
		//   -> Expected register value: 390 (> 255)
		uc.setIcrPeriod(0.1);
		assertEquals(0.1, uc.getIcrPeriod(), 0.0);
		assertEquals(0.100096, v.calculateQuantizedPeriod(uc.getIcrPeriod()), 0.0);
		assertEquals(ValidationResult.ERROR, v.getIcrPeriodError());
		assertEquals(390, v.calculateRegisterValue(uc.getIcrPeriod()));
	}
	
	@Test
	/*
	 * Tests the period validation of 8Bit timers in dual slope mode for correctness.
	 * See the inline comments for details.
	 */
	public void testEightBitDualSlopePeriodValidation()
	{
		// Create a new UseCaseModel
		UseCaseModel uc = model.addConfiguration();
		
		// Create a validator for this UseCaseModel
		UseCaseModelValidator v = new UseCaseModelValidator(model, uc);
		
		// This will test register calculation and validation for Dual-Slope mode:
		// Scenario:
		// - 4MHz processing speed
		model.setFrequency(4000000);
		assertEquals(4000000, model.getFrequency());
		// - Fast PWM Mode (Single Slope)
		uc.setMode(TimerOperationModes.PWM_PHASE_CORRECT);
		assertEquals(TimerOperationModes.PWM_PHASE_CORRECT, uc.getMode());
		// - Timer 0 (8Bit)
		uc.setTimer(TimerEnum.TIMER0);
		assertEquals(TimerEnum.TIMER0, uc.getTimer());
		// - Prescale divisor 1024
		uc.setPrescale(PrescaleFactors.ONETHOUSANDANDTWENTYFOUR);
		assertEquals(PrescaleFactors.ONETHOUSANDANDTWENTYFOUR, uc.getPrescale());
		assertEquals(1024, uc.getPrescale().getNumeric());
		// - ICR: 0.04992s (WARNING)
		//   -> Quantized time: 0.050176
		//   -> Expected register value: 98
		uc.setIcrPeriod(0.04992);
		assertEquals(0.04992, uc.getIcrPeriod(), 0.0);
		assertFalse(((Double) v.calculateQuantizedPeriod(uc.getOcrAPeriod())).equals(0.04992));
		assertEquals(0.050176, v.calculateQuantizedPeriod(uc.getIcrPeriod()), 0.0);
		assertEquals(ValidationResult.WARNING, v.getIcrPeriodError());
		assertEquals(98, v.calculateRegisterValue(uc.getIcrPeriod()));
		// - OCRA: 0.100352s (OK)
		//   -> No quantization error	
		//   -> Expected register value: 196
		uc.setOcrAPeriod(0.100352);
		assertEquals(0.100352, uc.getOcrAPeriod(), 0.0);
		assertEquals(0.100352, v.calculateQuantizedPeriod(uc.getOcrAPeriod()), 0.0);
		assertEquals(ValidationResult.OK, v.getOcrAPeriodError());
		assertEquals(196, v.calculateRegisterValue(v.calculateQuantizedPeriod(uc.getOcrAPeriod())));
		// - ICR: 0.2s (ERROR)
		//   -> Quantized time: 0.99968s
		//   -> Expected register value: 391 (> 255)
		uc.setIcrPeriod(0.2);
		assertEquals(0.2, uc.getIcrPeriod(), 0.0);
		assertEquals(0.200192, v.calculateQuantizedPeriod(uc.getIcrPeriod()), 0.0);
		assertEquals(ValidationResult.ERROR, v.getIcrPeriodError());
		assertEquals(391, v.calculateRegisterValue(uc.getIcrPeriod()));
	}
	
	@Test
	/*
	 * Tests the period validation of 16Bit timers in single slope mode for correctness.
	 * See the inline comments for details.
	 */
	public void testSixteenBitSingleSlopePeriodValidation() 
	{
		// Create a new UseCaseModel
		UseCaseModel uc = model.addConfiguration();
		
		// Create a validator for this UseCaseModel
		UseCaseModelValidator v = new UseCaseModelValidator(model, uc);
		
		// This will test register calculation and validation for Single-Slope mode:
		// Scenario:
		// - 8MHz processing speed
		model.setFrequency(8000000);
		assertEquals(8000000, model.getFrequency());
		// - Fast PWM Mode (Single Slope)
		uc.setMode(TimerOperationModes.PWM_FAST);
		assertEquals(TimerOperationModes.PWM_FAST, uc.getMode());
		// - Timer 1 (16 Bit)
		uc.setTimer(TimerEnum.TIMER1);
		assertEquals(TimerEnum.TIMER1, uc.getTimer());
		// - Prescale divisor 256
		uc.setPrescale(PrescaleFactors.TWOHUNDREDANDSIXTYFIVE);
		assertEquals(PrescaleFactors.TWOHUNDREDANDSIXTYFIVE, uc.getPrescale());
		assertEquals(256, uc.getPrescale().getNumeric());
		// - ICR: 1s (OK)
		//   -> No quantization error
		//   -> Expected register value: 31249
		//   -> Expect OK
		uc.setIcrPeriod(1.0);
		assertEquals(1.0, uc.getIcrPeriod(), 0.0);
		assertEquals(1.0, v.calculateQuantizedPeriod(uc.getIcrPeriod()), 0.0);
		assertEquals(ValidationResult.OK, v.getIcrPeriodError());
		assertEquals(31249, v.calculateRegisterValue(uc.getIcrPeriod()));
		// - OCRnA: 0.99001s (WARNING) 
		//   -> Quantized Time: 0.990016s
		//   -> Expected quantized register value: 30937
		//   -> Expect WARNING
		uc.setOcrAPeriod(0.99001);
		assertEquals(0.99001, uc.getOcrAPeriod(), 0.0);
		assertFalse(((Double) v.calculateQuantizedPeriod(uc.getOcrAPeriod())).equals(0.99001));
		assertEquals(0.990016, v.calculateQuantizedPeriod(uc.getOcrAPeriod()), 0.0);
		assertEquals(ValidationResult.WARNING, v.getOcrAPeriodError());
		assertEquals(30937, v.calculateRegisterValue(v.calculateQuantizedPeriod(uc.getOcrAPeriod())));
		// - OCRnB: 2.1s (ERROR)
		//   -> No quantization error, but
		//   -> Expected register value: 65624 (> 65535)
		//   -> expect ERROR
		uc.setOcrBPeriod(2.1);
		assertEquals(2.1, uc.getOcrBPeriod(), 0.0);
		assertEquals(2.1, v.calculateQuantizedPeriod(uc.getOcrBPeriod()), 0.0);
		assertEquals(ValidationResult.ERROR, v.getOcrBPeriodError());
		assertEquals(65624, v.calculateRegisterValue(uc.getOcrBPeriod()));
		// - OCRnC: 1s (OK)
		//	 -> No quantization error
		//   -> Expected register value: 31249
		//   -> Smaller than top value: expect WARNING
		uc.setOcrCPeriod(1.0);
		assertEquals(1.0, uc.getOcrCPeriod(), 0.0);
		assertEquals(1.0, v.calculateQuantizedPeriod(uc.getOcrCPeriod()), 0.0);
		assertEquals(ValidationResult.WARNING, v.getOcrCPeriodError());
		assertEquals(31249,	v.calculateRegisterValue(uc.getOcrCPeriod()));
	}
	
	@Test
	/*
	 * Tests the period validation of 16Bit timers in dual slope mode for correctness.
	 * See the inline comments for details.
	 */
	public void testSixteenBitDualSlopePeriodValidation() 
	{
		// Create a new UseCaseModel
		UseCaseModel uc = model.addConfiguration();
		
		// Create a validator for this UseCaseModel
		UseCaseModelValidator v = new UseCaseModelValidator(model, uc);
		
		// This will test register calculation and validation for Dual-Slope mode:
		// Scenario:
		// - 8MHz processing speed
		model.setFrequency(8000000);
		assertEquals(8000000, model.getFrequency());
		// - Fast PWM Mode (Single Slope)
		uc.setMode(TimerOperationModes.PWM_PHASE_CORRECT);
		assertEquals(TimerOperationModes.PWM_PHASE_CORRECT, uc.getMode());
		// - Timer 1 (16 Bit)
		uc.setTimer(TimerEnum.TIMER1);
		assertEquals(TimerEnum.TIMER1, uc.getTimer());
		// - Prescale divisor 256
		uc.setPrescale(PrescaleFactors.TWOHUNDREDANDSIXTYFIVE);
		assertEquals(PrescaleFactors.TWOHUNDREDANDSIXTYFIVE, uc.getPrescale());
		assertEquals(256, uc.getPrescale().getNumeric());
		// - ICR: 1s (OK)
		//   -> No quantization error
		//   -> Expected register value: 15625
		uc.setIcrPeriod(1.0);
		assertEquals(1.0, uc.getIcrPeriod(), 0.0);
		assertEquals(1.0, v.calculateQuantizedPeriod(uc.getIcrPeriod()), 0.0);
		assertEquals(ValidationResult.OK, v.getIcrPeriodError());
		assertEquals(15625, v.calculateRegisterValue(uc.getIcrPeriod()));
		// - OCRnA: 0.99001s (WARNING) 
		//   -> Quantized Time: 0.990016s
		//   -> Expected quantized register value: 15469
		uc.setOcrAPeriod(0.99001);
		assertEquals(0.99001, uc.getOcrAPeriod(), 0.0);
		assertFalse(((Double) v.calculateQuantizedPeriod(uc.getOcrAPeriod())).equals(0.99001));
		assertEquals(0.990016, v.calculateQuantizedPeriod(uc.getOcrAPeriod()), 0.0);
		assertEquals(ValidationResult.WARNING, v.getOcrAPeriodError());
		assertEquals(15469, v.calculateRegisterValue(v.calculateQuantizedPeriod(uc.getOcrAPeriod())));
		// - OCRnB: 4.2s (ERROR)
		//   -> No quantization error, but
		//   -> Expected register value: 65625 (> 65535)
		uc.setOcrBPeriod(4.2);
		assertEquals(4.2, uc.getOcrBPeriod(), 0.0);
		assertEquals(4.2, v.calculateQuantizedPeriod(uc.getOcrBPeriod()), 0.0);
		assertEquals(ValidationResult.ERROR, v.getOcrBPeriodError());
		assertEquals(65625, v.calculateRegisterValue(uc.getOcrBPeriod()));
		// - OCRnC: 1s (OK)
		//	 -> No quantization error
		//   -> Expected register value: 15625
		//   -> Smaller than top value: expect WARNING		
		uc.setOcrCPeriod(1.0);
		assertEquals(1.0, uc.getOcrCPeriod(), 0.0);
		assertEquals(1.0, v.calculateQuantizedPeriod(uc.getOcrCPeriod()), 0.0);
		assertEquals(ValidationResult.WARNING, v.getOcrCPeriodError());
		assertEquals(15625,	v.calculateRegisterValue(uc.getOcrCPeriod()));
	}

	@Test
	/*
	 * Tests the top value validation of 8Bit timers for correctness.
	 * See the inline comments for details.
	 */
	public void testEightBitTopValueValidation()
	{
		// Create a new UseCaseModel
		UseCaseModel uc = model.addConfiguration();
		
		// Create a validator for this UseCaseModel
		UseCaseModelValidator v = new UseCaseModelValidator(model, uc);
		
		// Timer 0:
		uc.setTimer(TimerEnum.TIMER0);
		assertEquals(TimerEnum.TIMER0, uc.getTimer());
		
		// CTC Mode:
		uc.setMode(TimerOperationModes.CTC);
		assertEquals(TimerOperationModes.CTC, uc.getMode());
		// Only OCR0A is valid as top value register!
		uc.setCtcTop(CTCTopValues.ICR);
		assertEquals(CTCTopValues.ICR, uc.getCtcTop());
		assertEquals(ValidationResult.ERROR, v.getCtcTopError());
		uc.setCtcTop(CTCTopValues.OCRnA);
		assertEquals(CTCTopValues.OCRnA, uc.getCtcTop());
		assertEquals(ValidationResult.OK, v.getCtcTopError());
		
		// Fast PWM Mode:
		uc.setMode(TimerOperationModes.PWM_FAST);
		assertEquals(TimerOperationModes.PWM_FAST, uc.getMode());
		// Only fixed value 255 is valid as top value!
		uc.setFastPWMTop(PWMTopValues.BIT8);
		assertEquals(PWMTopValues.BIT8, uc.getFastPWMTop());
		assertEquals(ValidationResult.OK, v.getFastPWMTopError());
		uc.setFastPWMTop(PWMTopValues.BIT9);
		assertEquals(PWMTopValues.BIT9, uc.getFastPWMTop());
		assertEquals(ValidationResult.ERROR, v.getFastPWMTopError());
		uc.setFastPWMTop(PWMTopValues.BIT10);
		assertEquals(PWMTopValues.BIT10, uc.getFastPWMTop());
		assertEquals(ValidationResult.ERROR, v.getFastPWMTopError());
		uc.setFastPWMTop(PWMTopValues.ICR);
		assertEquals(PWMTopValues.ICR, uc.getFastPWMTop());
		assertEquals(ValidationResult.ERROR, v.getFastPWMTopError());
		uc.setFastPWMTop(PWMTopValues.OCRnA);
		assertEquals(PWMTopValues.OCRnA, uc.getFastPWMTop());
		assertEquals(ValidationResult.ERROR, v.getFastPWMTopError());
		
		// Phase correct PWM Mode:
		uc.setMode(TimerOperationModes.PWM_PHASE_CORRECT);
		assertEquals(TimerOperationModes.PWM_PHASE_CORRECT, uc.getMode());
		// Only fixed value 255 is valid as top value!
		uc.setPhaseCorrectPWMTop(PWMTopValues.BIT8);
		assertEquals(PWMTopValues.BIT8, uc.getPhaseCorrectPWMTop());
		assertEquals(ValidationResult.OK, v.getPhaseCorrectPWMTopError());
		uc.setPhaseCorrectPWMTop(PWMTopValues.BIT9);
		assertEquals(PWMTopValues.BIT9, uc.getPhaseCorrectPWMTop());
		assertEquals(ValidationResult.ERROR, v.getPhaseCorrectPWMTopError());
		uc.setPhaseCorrectPWMTop(PWMTopValues.BIT10);
		assertEquals(PWMTopValues.BIT10, uc.getPhaseCorrectPWMTop());
		assertEquals(ValidationResult.ERROR, v.getPhaseCorrectPWMTopError());
		uc.setPhaseCorrectPWMTop(PWMTopValues.ICR);
		assertEquals(PWMTopValues.ICR, uc.getPhaseCorrectPWMTop());
		assertEquals(ValidationResult.ERROR, v.getPhaseCorrectPWMTopError());
		uc.setPhaseCorrectPWMTop(PWMTopValues.OCRnA);
		assertEquals(PWMTopValues.OCRnA, uc.getPhaseCorrectPWMTop());
		assertEquals(ValidationResult.ERROR, v.getPhaseCorrectPWMTopError());
		
		// Phase and frequency correct PWM Mode is not valid for 8Bit Timers
		// -> therefore, it is not tested here
	}
	
	@Test
	/*
	 * Tests the top value validation of 16Bit timers for correctness.
	 * See the inline comments for details.
	 */
	public void testSixteenBitTopValueValidation()
	{
		// Create a new UseCaseModel
		UseCaseModel uc = model.addConfiguration();
		
		// Create a validator for this UseCaseModel
		UseCaseModelValidator v = new UseCaseModelValidator(model, uc);
		
		// Timer 0:
		uc.setTimer(TimerEnum.TIMER1);
		assertEquals(TimerEnum.TIMER1, uc.getTimer());
		
		// CTC Mode:
		uc.setMode(TimerOperationModes.CTC);
		assertEquals(TimerOperationModes.CTC, uc.getMode());
		// All top value register choices are valid!
		uc.setCtcTop(CTCTopValues.ICR);
		assertEquals(CTCTopValues.ICR, uc.getCtcTop());
		assertEquals(ValidationResult.OK, v.getCtcTopError());
		uc.setCtcTop(CTCTopValues.OCRnA);
		assertEquals(CTCTopValues.OCRnA, uc.getCtcTop());
		assertEquals(ValidationResult.OK, v.getCtcTopError());
		
		// Fast PWM Mode:
		uc.setMode(TimerOperationModes.PWM_FAST);
		assertEquals(TimerOperationModes.PWM_FAST, uc.getMode());
		// All top value choices are valid!
		uc.setFastPWMTop(PWMTopValues.BIT8);
		assertEquals(PWMTopValues.BIT8, uc.getFastPWMTop());
		assertEquals(ValidationResult.OK, v.getFastPWMTopError());
		uc.setFastPWMTop(PWMTopValues.BIT9);
		assertEquals(PWMTopValues.BIT9, uc.getFastPWMTop());
		assertEquals(ValidationResult.OK, v.getFastPWMTopError());
		uc.setFastPWMTop(PWMTopValues.BIT10);
		assertEquals(PWMTopValues.BIT10, uc.getFastPWMTop());
		assertEquals(ValidationResult.OK, v.getFastPWMTopError());
		uc.setFastPWMTop(PWMTopValues.ICR);
		assertEquals(PWMTopValues.ICR, uc.getFastPWMTop());
		assertEquals(ValidationResult.OK, v.getFastPWMTopError());
		uc.setFastPWMTop(PWMTopValues.OCRnA);
		assertEquals(PWMTopValues.OCRnA, uc.getFastPWMTop());
		assertEquals(ValidationResult.OK, v.getFastPWMTopError());
		
		// Phase correct PWM Mode:
		uc.setMode(TimerOperationModes.PWM_PHASE_CORRECT);
		assertEquals(TimerOperationModes.PWM_PHASE_CORRECT, uc.getMode());
		// All top value choices are valid!
		uc.setPhaseCorrectPWMTop(PWMTopValues.BIT8);
		assertEquals(PWMTopValues.BIT8, uc.getPhaseCorrectPWMTop());
		assertEquals(ValidationResult.OK, v.getPhaseCorrectPWMTopError());
		uc.setPhaseCorrectPWMTop(PWMTopValues.BIT9);
		assertEquals(PWMTopValues.BIT9, uc.getPhaseCorrectPWMTop());
		assertEquals(ValidationResult.OK, v.getPhaseCorrectPWMTopError());
		uc.setPhaseCorrectPWMTop(PWMTopValues.BIT10);
		assertEquals(PWMTopValues.BIT10, uc.getPhaseCorrectPWMTop());
		assertEquals(ValidationResult.OK, v.getPhaseCorrectPWMTopError());
		uc.setPhaseCorrectPWMTop(PWMTopValues.ICR);
		assertEquals(PWMTopValues.ICR, uc.getPhaseCorrectPWMTop());
		assertEquals(ValidationResult.OK, v.getPhaseCorrectPWMTopError());
		uc.setPhaseCorrectPWMTop(PWMTopValues.OCRnA);
		assertEquals(PWMTopValues.OCRnA, uc.getPhaseCorrectPWMTop());
		assertEquals(ValidationResult.OK, v.getPhaseCorrectPWMTopError());
		
		// Phase and frequency correct PWM Mode:
		uc.setMode(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT);
		assertEquals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT, uc.getMode());
		// All top value register choices are valid!
		uc.setPhaseAndFrequencyCorrectPWMTop(PhaseAndFrequencyCorrectPWMTopValues.ICR);
		assertEquals(PhaseAndFrequencyCorrectPWMTopValues.ICR, uc.getPhaseAndFrequencyCorrectPWMTop());
		assertEquals(ValidationResult.OK, v.getPhaseAndFrequencyCorrectPWMTopError());
		uc.setPhaseAndFrequencyCorrectPWMTop(PhaseAndFrequencyCorrectPWMTopValues.OCRnA);
		assertEquals(PhaseAndFrequencyCorrectPWMTopValues.OCRnA, uc.getPhaseAndFrequencyCorrectPWMTop());
		assertEquals(ValidationResult.OK, v.getPhaseAndFrequencyCorrectPWMTopError());
	}

	@Test
	/*
	 * Tests the mode and timer validation for correctness.
	 * See the inline comments for details.
	 */
	public void testModeAndTimerValidation() 
	{
		// Create a new UseCaseModel
		UseCaseModel uc = model.addConfiguration();
		
		// Create a validator for this UseCaseModel
		UseCaseModelValidator v = new UseCaseModelValidator(model, uc);
		
		// 8 Bit timer:
		uc.setTimer(TimerEnum.TIMER2);
		assertEquals(TimerEnum.TIMER2, uc.getTimer());
		// All modes except Phase and frequency correct PWM mode are valid!
		uc.setMode(TimerOperationModes.OVERFLOW);
		assertEquals(TimerOperationModes.OVERFLOW, uc.getMode());
		assertEquals(ValidationResult.OK, v.getModeError());
		uc.setMode(TimerOperationModes.CTC);
		assertEquals(TimerOperationModes.CTC, uc.getMode());
		assertEquals(ValidationResult.OK, v.getModeError());
		uc.setMode(TimerOperationModes.PWM_FAST);
		assertEquals(TimerOperationModes.PWM_FAST, uc.getMode());
		assertEquals(ValidationResult.OK, v.getModeError());
		uc.setMode(TimerOperationModes.PWM_PHASE_CORRECT);
		assertEquals(TimerOperationModes.PWM_PHASE_CORRECT, uc.getMode());
		assertEquals(ValidationResult.OK, v.getModeError());
		uc.setMode(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT);
		assertEquals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT, uc.getMode());
		assertEquals(ValidationResult.ERROR, v.getModeError());
		
		// 16 Bit Timer:
		uc.setTimer(TimerEnum.TIMER3);
		assertEquals(TimerEnum.TIMER3, uc.getTimer());
		// All modes except Phase and frequency correct PWM mode are valid!
		uc.setMode(TimerOperationModes.OVERFLOW);
		assertEquals(TimerOperationModes.OVERFLOW, uc.getMode());
		assertEquals(ValidationResult.OK, v.getModeError());
		uc.setMode(TimerOperationModes.CTC);
		assertEquals(TimerOperationModes.CTC, uc.getMode());
		assertEquals(ValidationResult.OK, v.getModeError());
		uc.setMode(TimerOperationModes.PWM_FAST);
		assertEquals(TimerOperationModes.PWM_FAST, uc.getMode());
		assertEquals(ValidationResult.OK, v.getModeError());
		uc.setMode(TimerOperationModes.PWM_PHASE_CORRECT);
		assertEquals(TimerOperationModes.PWM_PHASE_CORRECT, uc.getMode());
		assertEquals(ValidationResult.OK, v.getModeError());
		uc.setMode(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT);
		assertEquals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT, uc.getMode());
		assertEquals(ValidationResult.OK, v.getModeError());
	}

	@Test
	/*
	 * Tests the output pin mode validation in single slope mode for correctness.
	 * See the inline comments for details.
	 */
	public void testSingleSlopePWMOutputPinModeValidation() 
	{
		// Create a new UseCaseModel
		UseCaseModel uc = model.addConfiguration();
		
		// Create a validator for this UseCaseModel
		UseCaseModelValidator v = new UseCaseModelValidator(model, uc);
		
		// Mode: Fast PWM
		uc.setMode(TimerOperationModes.PWM_FAST);
		assertEquals(TimerOperationModes.PWM_FAST, uc.getMode());
		
		// 8 Bit timer:
		uc.setTimer(TimerEnum.TIMER2);
		assertEquals(TimerEnum.TIMER2, uc.getTimer());
		// All output pinmodes except for Toggle are valid!
		uc.setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode.NORMAL);
		assertEquals(PWMSingleSlopeOutputPinMode.NORMAL, uc.getSingleSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getSingleSlopePWMPinModeAError());
		uc.setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode.TOGGLE);
		assertEquals(PWMSingleSlopeOutputPinMode.TOGGLE, uc.getSingleSlopePWMPinModeA());
		assertEquals(ValidationResult.ERROR, v.getSingleSlopePWMPinModeAError());
		uc.setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode.CLEAR);
		assertEquals(PWMSingleSlopeOutputPinMode.CLEAR, uc.getSingleSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getSingleSlopePWMPinModeAError());
		uc.setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode.SET);
		assertEquals(PWMSingleSlopeOutputPinMode.SET, uc.getSingleSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getSingleSlopePWMPinModeAError());
		
		// 16 Bit timer:
		uc.setTimer(TimerEnum.TIMER3);
		assertEquals(TimerEnum.TIMER3, uc.getTimer());
		// All output pinmodes are valid!
		// Exception: Toggle is only valid for channel A, if ICR or OCR3A is set as top value register!
		uc.setFastPWMTop(PWMTopValues.BIT8);
		assertEquals(PWMTopValues.BIT8, uc.getFastPWMTop());
		uc.setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode.NORMAL);
		assertEquals(PWMSingleSlopeOutputPinMode.NORMAL, uc.getSingleSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getSingleSlopePWMPinModeAError());
		uc.setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode.TOGGLE);
		assertEquals(PWMSingleSlopeOutputPinMode.TOGGLE, uc.getSingleSlopePWMPinModeA());
		assertEquals(ValidationResult.ERROR, v.getSingleSlopePWMPinModeAError());
		uc.setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode.CLEAR);
		assertEquals(PWMSingleSlopeOutputPinMode.CLEAR, uc.getSingleSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getSingleSlopePWMPinModeAError());
		uc.setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode.SET);
		assertEquals(PWMSingleSlopeOutputPinMode.SET, uc.getSingleSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getSingleSlopePWMPinModeAError());
		
		uc.setFastPWMTop(PWMTopValues.ICR);
		assertEquals(PWMTopValues.ICR, uc.getFastPWMTop());
		uc.setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode.TOGGLE);
		assertEquals(PWMSingleSlopeOutputPinMode.TOGGLE, uc.getSingleSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getSingleSlopePWMPinModeAError());
		uc.setFastPWMTop(PWMTopValues.OCRnA);
		assertEquals(PWMTopValues.OCRnA, uc.getFastPWMTop());
		uc.setSingleSlopePWMPinModeA(PWMSingleSlopeOutputPinMode.TOGGLE);
		assertEquals(PWMSingleSlopeOutputPinMode.TOGGLE, uc.getSingleSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getSingleSlopePWMPinModeAError());
	}
	
	@Test
	/*
	 * Tests the output pin mode validation in dual slope mode for correctness.
	 * See the inline comments for details.
	 */
	public void testDualSlopePWMOutputPinModeValidation() 
	{
		// Create a new UseCaseModel
		UseCaseModel uc = model.addConfiguration();
		
		// Create a validator for this UseCaseModel
		UseCaseModelValidator v = new UseCaseModelValidator(model, uc);
		
		// Mode: Phase correct PWM
		uc.setMode(TimerOperationModes.PWM_PHASE_CORRECT);
		assertEquals(TimerOperationModes.PWM_PHASE_CORRECT, uc.getMode());
		
		// 8 Bit timer:
		uc.setTimer(TimerEnum.TIMER2);
		assertEquals(TimerEnum.TIMER2, uc.getTimer());
		// All output pinmodes except for Toggle are valid!
		uc.setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode.NORMAL);
		assertEquals(PWMDualSlopeOutputPinMode.NORMAL, uc.getDualSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getDualSlopePWMPinModeAError());
		uc.setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode.TOGGLE);
		assertEquals(PWMDualSlopeOutputPinMode.TOGGLE, uc.getDualSlopePWMPinModeA());
		assertEquals(ValidationResult.ERROR, v.getDualSlopePWMPinModeAError());
		uc.setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode.CLEAR_SET);
		assertEquals(PWMDualSlopeOutputPinMode.CLEAR_SET, uc.getDualSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getDualSlopePWMPinModeAError());
		uc.setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode.SET_CLEAR);
		assertEquals(PWMDualSlopeOutputPinMode.SET_CLEAR, uc.getDualSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getDualSlopePWMPinModeAError());
		
		// 16 Bit timer:
		uc.setTimer(TimerEnum.TIMER3);
		assertEquals(TimerEnum.TIMER3, uc.getTimer());
		// All output pinmodes are valid!
		// Exception: Toggle is only valid for channel A, if ICR or OCR3A is set as top value register!
		uc.setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode.NORMAL);
		assertEquals(PWMDualSlopeOutputPinMode.NORMAL, uc.getDualSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getDualSlopePWMPinModeAError());
		uc.setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode.TOGGLE);
		assertEquals(PWMDualSlopeOutputPinMode.TOGGLE, uc.getDualSlopePWMPinModeA());
		assertEquals(ValidationResult.ERROR, v.getDualSlopePWMPinModeAError());
		uc.setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode.CLEAR_SET);
		assertEquals(PWMDualSlopeOutputPinMode.CLEAR_SET, uc.getDualSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getDualSlopePWMPinModeAError());
		uc.setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode.SET_CLEAR);
		assertEquals(PWMDualSlopeOutputPinMode.SET_CLEAR, uc.getDualSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getDualSlopePWMPinModeAError());
		
		uc.setPhaseCorrectPWMTop(PWMTopValues.ICR);
		assertEquals(PWMTopValues.ICR, uc.getPhaseCorrectPWMTop());
		uc.setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode.TOGGLE);
		assertEquals(PWMDualSlopeOutputPinMode.TOGGLE, uc.getDualSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getDualSlopePWMPinModeAError());
		uc.setPhaseCorrectPWMTop(PWMTopValues.OCRnA);
		assertEquals(PWMTopValues.OCRnA, uc.getPhaseCorrectPWMTop());
		uc.setDualSlopePWMPinModeA(PWMDualSlopeOutputPinMode.TOGGLE);
		assertEquals(PWMDualSlopeOutputPinMode.TOGGLE, uc.getDualSlopePWMPinModeA());
		assertEquals(ValidationResult.OK, v.getDualSlopePWMPinModeAError());
	}
}
