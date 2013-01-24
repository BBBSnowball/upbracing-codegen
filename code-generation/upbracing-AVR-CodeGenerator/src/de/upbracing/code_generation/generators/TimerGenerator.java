package de.upbracing.code_generation.generators;

import java.util.ArrayList;

import de.upbracing.code_generation.TimerHeaderTemplate;
import de.upbracing.code_generation.TimerCFileTemplate;
import de.upbracing.code_generation.Messages.Severity;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.shared.timer.model.UseCaseModel;
import de.upbracing.shared.timer.model.validation.ConfigurationModelValidator;
import de.upbracing.shared.timer.model.validation.UseCaseModelValidator;
import de.upbracing.shared.timer.model.validation.ValidationResult;

public class TimerGenerator extends AbstractGenerator {
	
	public TimerGenerator() {
		super("timer.h", new TimerHeaderTemplate(),
				"timer.c", new TimerCFileTemplate());
	}
	
	public TimerGenerator(String filePrefix) {
		super(filePrefix + ".h", new TimerHeaderTemplate(),
				filePrefix + ".c", new TimerCFileTemplate());
	}
	
	@Override
	public boolean validate(CodeGeneratorConfigurations config, boolean after_update_config, Object generator_data) {
		// if we don't have a model, there is nothing to do
		if (config.getTimerConfig() == null)
			return true;
		
		ConfigurationModelValidator modelValidator = new ConfigurationModelValidator(config.getTimerConfig());
		
		// 1) Check UseCaseModel Container (ConfigurationModel)
		if (modelValidator.validate().equals(ValidationResult.ERROR)) {
			// This is a showstopper, since the container was faulty
			return false;
		}
		
		// 2) Check each UseCaseConfiguration 
		ArrayList<UseCaseModel> faultyModels = new ArrayList<UseCaseModel>();		
		for (UseCaseModel m: config.getTimerConfig().getConfigurations()) {

			UseCaseModelValidator ucValidator = new UseCaseModelValidator(config.getTimerConfig(), m);
			String message;
			if (ucValidator.validate().equals(ValidationResult.ERROR)) {
				// This is not really a showstopper, but the user should be warned,
				// that this particular UseCaseConfiguration cannot be generated.
				message = "Timer configuration \"" + m.getName() + "\" is not properly configured. -> No code is generated for this Use Case!";
				config.getMessages().addMessage(Severity.ERROR, message);
				// Remove the faulty models from code generation process (Part 1)
				faultyModels.add(m);
			} else if (ucValidator.validate().equals(ValidationResult.WARNING)) {
				message = "Timer configuration \"" + m.getName() + "\" was validated with warnings. -> Code is generated, but it may not function as expected.";
				config.getMessages().addMessage(Severity.WARNING, message);
			}
		}
		
		// Remove the faulty models from code generation process (Part 2)
		for (UseCaseModel m: faultyModels) {
			config.getTimerConfig().getConfigurations().remove(m);
		}
		
		return true;
	}
}
