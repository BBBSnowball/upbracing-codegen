package de.upbracing.configurationeditor.timer.viewmodel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import de.upbracing.shared.timer.model.ConfigurationModel;
import de.upbracing.shared.timer.model.UseCaseModel;

public class ConfigurationViewModel extends AViewModelBase {
	
	// Reference to the underlying model:
	private ConfigurationModel model;
	
	// Constructor:
	public ConfigurationViewModel(ConfigurationModel m) {
		this.model = m;
		
		// Init UseCaseConfigurationViewModels
		if (m.getConfigurations() != null)
			for (UseCaseModel uc: m.getConfigurations()) {
				UseCaseViewModel vm = new UseCaseViewModel(uc);
				vm.setParent(this);
				configurations.add(vm);
			}
	}
	
	// Routed Model Getters:
	public int getFrequency() {
		return model.getFrequency();
	}
	
	// Routed Model Setters:
	public void setFrequency(int f) {
		model.setFrequency(f);
		changes.firePropertyChange("frequency", null, null);
		for (UseCaseViewModel vm: configurations) {
			vm.triggerUpdateView();
		}
	}
	
	private ArrayList<UseCaseViewModel> configurations = new ArrayList<UseCaseViewModel>();

	
	
	public UseCaseViewModel addConfiguration() {
		
		UseCaseModel m = model.addConfiguration();
		UseCaseViewModel vm = new UseCaseViewModel(m);
		vm.setParent(this);
		configurations.add(vm);
		return vm;
	}
	
	public void removeConfiguration(UseCaseViewModel m) {
		if (configurations.contains(m)) {
			model.removeConfiguration(m.getModel());
			configurations.remove(m);
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
}
