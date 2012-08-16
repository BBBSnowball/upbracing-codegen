package de.upbracing.shared.timer.model.validation;

import de.upbracing.shared.timer.model.ConfigurationModel;
import de.upbracing.shared.timer.model.UseCaseModel;
import de.upbracing.shared.timer.model.enums.TimerEnum;

public class UseCaseModelValidator {

	private ConfigurationModel parent;
	private UseCaseModel model;
	
	public UseCaseModelValidator(ConfigurationModel parent, UseCaseModel model) {
		this.model = model;
		this.parent = parent;
	}
	
	public boolean getIcrError() {
		// What do I need here?
		// - Prescale factor
		// - Timer Number (Max value)
		// - Frequency
		
		// 1) Calculate register value
		int frequency = parent.getFrequency();
		int maxValue = 255;
		if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3))
			maxValue = 65535;
		int prescale = model.getPrescale().getNumeric();
		double timerFreq = ((double) frequency / (double) prescale);
		double timerPeriod = 1.0 / timerFreq;
		double registerValue = model.getIcrPeriod() / timerPeriod;
		if (registerValue > maxValue)
			return true;
		return false;
	}
}
