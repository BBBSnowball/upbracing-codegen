package de.upbracing.eculist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ECUDefinition {
	private String name, path, buildDir, type, node_id, node_name;
	private List<EEPROMValue> eeprom_values;
	
	public ECUDefinition(String name, String path, String buildDir,
			String type, String node_id, String node_name) {
		super();
		this.name = name;
		this.path = path;
		this.buildDir = buildDir;
		this.type = type;
		this.node_id = node_id;
		this.node_name = node_name;
		this.eeprom_values = new ArrayList<EEPROMValue>();
	}
	
	public ECUDefinition() {
		this.eeprom_values = new ArrayList<EEPROMValue>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBuildDir() {
		return buildDir;
	}

	public void setBuildDir(String buildDir) {
		this.buildDir = buildDir;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNodeId() {
		return node_id;
	}

	public void setNodeId(String node_id) {
		this.node_id = node_id;
	}

	public String getNodeName() {
		return node_name;
	}

	public void setNodeName(String node_name) {
		this.node_name = node_name;
	}

	public List<EEPROMValue> getEepromValues() {
		return eeprom_values;
	}

	public void setEepromValues(Collection<EEPROMValue> eeprom_values) {
		this.eeprom_values = new ArrayList<EEPROMValue>(eeprom_values);
	}
}
