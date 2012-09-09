package de.upbracing.shared.timer.model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigurationModel {
	
	// Private fields:
	private int frequency = 8000000;					// Each CPU runs at a specific frequency :)
	private ArrayList<UseCaseModel>	configurations;		// Each ConfigurationModel has an arbitrary
														// number of UseCaseConfigurations
	// Constructor:
	public ConfigurationModel() {
		configurations = new ArrayList<UseCaseModel>();
	}
	
	// Public Getters:
	public int getFrequency() {
		return this.frequency;
	}
	public ArrayList<UseCaseModel> getConfigurations() {
		return configurations;
	}
	
	// Public Setters:
	public void setFrequency(int f) {
		this.frequency = f;
	}
	public void setConfigurations(ArrayList<UseCaseModel> c) {
		this.configurations = c;
	}
	
	// Configuration Add/Remove Methods:
	public UseCaseModel addConfiguration() {
		UseCaseModel model = new UseCaseModel();
		configurations.add(model);
		return model;
	}
	public void removeConfiguration(UseCaseModel m) {
		if (configurations.contains(m))
			configurations.remove(m);
	}
	
	// Save/Load Methods:
	public static ConfigurationModel Load(String path) throws FileNotFoundException {
		
		// Just load the previously serialized file from "path"
		FileInputStream str = new FileInputStream(path);
		XMLDecoder dec = new XMLDecoder(str);
		return (ConfigurationModel)dec.readObject();
	}
	
	public void Save(String path) throws IOException {
		
		// Just serialize the model object to "path"
		FileOutputStream os = new FileOutputStream(path);
		XMLEncoder enc = new XMLEncoder(os);
		enc.writeObject(this);
		enc.close();
		os.close();
	}
	
}
