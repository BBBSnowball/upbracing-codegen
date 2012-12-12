package de.upbracing.shared.timer.model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class stores the processor frequency together with the list of of use {@link UseCaseModel} objects.
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 */
public class ConfigurationModel {
	
	// Private fields:
	private int frequency;								// Each CPU runs at a specific frequency :)
	private int errorTolerance;							// Quantization error percentage tolerated
	private ArrayList<UseCaseModel>	configurations;		// Each ConfigurationModel has an arbitrary
														// number of UseCaseConfigurations
	// Constructor:
	/**
	 * Creates a new instance of this class and initializes the list of {@link UseCaseModel} objects.
	 */
	public ConfigurationModel() {
		frequency = 8000000;
		errorTolerance = 5;
		configurations = new ArrayList<UseCaseModel>();
	}
	
	// Public Getters:
	/**
	 * Gets the configured frequency of the AT90CAN128 processor.
	 * @return the frequency of the processor.
	 */
	public int getFrequency() {
		return this.frequency;
	}
	/**
	 * Gets the configured error tolerance in percent.
	 * @return the error tolerance in percent.
	 */
	public int getErrorTolerance() {
		return this.errorTolerance;
	}
	/**
	 * Gets the list of {@link UseCaseModel} objects.
	 * @return the list of {@link UseCaseModel} objects.
	 */
	public ArrayList<UseCaseModel> getConfigurations() {
		return configurations;
	}
	
	// Public Setters:
	/**
	 * Sets the frequency of the AT90CAN128 processor.
	 * (Note, that invalid frequency values will be stored. But keep in mind, that validation will fail.)
	 * @param f the new frequency setting.
	 */
	public void setFrequency(int f) {
		this.frequency = f;
	}
	/**
	 * Sets the error tolerance in percent.
	 * @param t the new error tolerance setting.
	 */
	public void setErrorTolerance(int t) {
		this.errorTolerance = t;
	}
	/**
	 * Changes the list of {@link UseCaseModel} objects.
	 * (This method is invoked during XML deserialization within {@link ConfigurationModel#Load Load} and not intended to be used otherwise.)
	 * @param c new list of {@link UseCaseModel} objects.
	 */
	public void setConfigurations(ArrayList<UseCaseModel> c) {
		this.configurations = c;
	}
	
	// Configuration Add/Remove Methods:
	/**
	 * Creates a new configuration and adds this to the list of {@link UseCaseModel} objects.
	 * @return the newly created {@link UseCaseModel}.
	 */
	public UseCaseModel addConfiguration() {
		UseCaseModel model = new UseCaseModel();
		configurations.add(model);
		return model;
	}
	/**
	 * Removes a given {@link UseCaseModel}.
	 * @param m the {@link UseCaseModel} to remove.
	 */
	public void removeConfiguration(UseCaseModel m) {
		if (configurations.contains(m))
			configurations.remove(m);
	}
	
	public static ConfigurationModel GetClone(ConfigurationModel model) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder oos = new XMLEncoder(baos);
		oos.writeObject(model);
		oos.flush();
		oos.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toString().getBytes());
		XMLDecoder ois = new XMLDecoder(bais);
		return (ConfigurationModel) ois.readObject();
	}
	
	// Save/Load Methods:
	/**
	 * Loads a previously saved XML representation of a {@link ConfigurationModel} from disk.
	 * @param path to the XML file containing the model representation.
	 * @return the deserialized {@link ConfigurationModel} object.
	 * @throws FileNotFoundException
	 */
	public static ConfigurationModel Load(String path) throws FileNotFoundException {
		
		// Check, if file exists:
		File f = new File(path);
		if (!f.exists())
			return null;
		
		// Just load the previously serialized file from "path"
		FileInputStream str = new FileInputStream(path);
		XMLDecoder dec = new XMLDecoder(str);
		return (ConfigurationModel)dec.readObject();
	}
	
	/**
	 * Saves this configuration model as an XML representation to a specified path to disk.
	 * @param path to the XML file being created.
	 * @throws IOException
	 */
	public void Save(String path) throws IOException {
		
		// Just serialize the model object to "path"
		FileOutputStream os = new FileOutputStream(path);
		XMLEncoder enc = new XMLEncoder(os);
		enc.writeObject(this);
		enc.close();
		os.close();
	}
	
}
