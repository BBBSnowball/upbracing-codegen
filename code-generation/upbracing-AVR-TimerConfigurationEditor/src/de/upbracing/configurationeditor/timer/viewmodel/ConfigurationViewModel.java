package de.upbracing.configurationeditor.timer.viewmodel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import de.upbracing.shared.timer.model.ConfigurationModel;
import de.upbracing.shared.timer.model.UseCaseModel;
import de.upbracing.shared.timer.model.validation.ConfigurationModelValidator;

public class ConfigurationViewModel extends AViewModelBase {
	
	// Reference to the underlying model:
	private ConfigurationModel model;
	private ConfigurationModelValidator validator;
	
	// Constructor:
	public ConfigurationViewModel(ConfigurationModel m) {
		this.model = m;
		
		// Init UseCaseConfigurationViewModels
		if (m.getConfigurations() != null)
			for (UseCaseModel uc: m.getConfigurations()) {
				UseCaseViewModel vm = new UseCaseViewModel(uc, model);
				vm.setParent(this);
				configurations.add(vm);
			}
		
		this.validator = new ConfigurationModelValidator(model);
	}
	
	// Getter for Model
	// (Used for built-in CodeGenerator)
	public ConfigurationModel getModel() {
		return model;
	}
	
	// Getter for Validator
	public ConfigurationModelValidator getValidator() {
		return validator;
	}
	
	// Routed Model Getters:
	public int getFrequency() {
		return model.getFrequency();
	}
	public int getErrorTolerance() {
		return model.getErrorTolerance();
	}
	
	// Routed Model Setters:
	public void setFrequency(int f) {
		if (model.getFrequency() != f) {
			model.setFrequency(f);
			changes.firePropertyChange("frequency", null, null);
			for (UseCaseViewModel vm: configurations) {
				vm.triggerUpdateView();
				vm.getValidator().updatePeriodValidation();
			}
			this.validator.updateValidation();
		}
	}
	public void setErrorTolerance(int t) {
		if (model.getErrorTolerance() != t) {
			model.setErrorTolerance(t);
			changes.firePropertyChange("errorTolerance", null, null);
			for (UseCaseViewModel vm: configurations) {
				vm.triggerUpdateView();
				vm.getValidator().updatePeriodValidation();
			}
		}
	}
	
	private ArrayList<UseCaseViewModel> configurations = new ArrayList<UseCaseViewModel>();

	
	
	public UseCaseViewModel addConfiguration() {
		
		UseCaseModel m = model.addConfiguration();
		UseCaseViewModel vm = new UseCaseViewModel(m, model);
		vm.setParent(this);
		configurations.add(vm);
		for (UseCaseViewModel vm2: configurations) {
			vm2.getValidator().updateValidation();
		}
		return vm;
	}
	
	public void removeConfiguration(UseCaseViewModel m) {
		if (configurations.contains(m)) {
			model.removeConfiguration(m.getModel());
			configurations.remove(m);
			
			for (UseCaseViewModel vm: configurations) {
				vm.getValidator().updateValidation();
			}
		}
	}
	
	public ArrayList<UseCaseViewModel> getConfigurations() {
		for (UseCaseViewModel m: configurations) {
			m.setParent(this);
		}
		return configurations;
	}
	
	
	
	public void setConfigurations(ArrayList<UseCaseViewModel> c) {
		changes.firePropertyChange("configurations", this.configurations, this.configurations = c);
	}
	
	public static ConfigurationViewModel Load(String path) throws FileNotFoundException {
		
		// Get the model and construct:
		ConfigurationViewModel vm = new ConfigurationViewModel(ConfigurationModel.Load(path));
		return vm;
	}
	
	public void Save(String path) throws IOException {
		
		// Save the underlying model
		model.Save(path);
	}
	
	public void updateUseCaseNameValidation() {
		for (UseCaseViewModel m: configurations) {
			m.getValidator().updateNameValidation();
		}
	}
}
