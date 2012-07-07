package de.upbracing.eculist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.simpleframework.xml.Default;

/**
 * ECU (electronic control unit) definition read from ecu-list.xml
 * 
 * @author benny
 */
@Default(required=false)
public class ECUDefinition {
	private String name, path, buildDir, type, node_id, node_name;
	private List<EEPROMValue> eeprom_values;
	
	/** constructor
	 * 
	 * @param name name of the ECU
	 * @param path path of the source code project
	 * @param buildDir build directory (Debug or Release directory below <path>)
	 * @param type type of the microcontroller, e.g. "AT90CAN"
	 * @param node_id node id of the bootloader
	 * @param node_name node name in the DBC file
	 */
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
	
	/** constructor */
	public ECUDefinition() {
		this.eeprom_values = new ArrayList<EEPROMValue>();
	}

	/** get name */
	public String getName() {
		return name;
	}

	/** set name */
	public void setName(String name) {
		this.name = name;
	}

	/** get path of the source code project */
	public String getPath() {
		return path;
	}

	/** set path of the source code project */
	public void setPath(String path) {
		this.path = path;
	}

	/** get build directory (Debug or Release directory below <path>) */
	public String getBuildDir() {
		return buildDir;
	}

	/** set build directory (Debug or Release directory below <path>) */
	public void setBuildDir(String buildDir) {
		this.buildDir = buildDir;
	}

	/** get type of the microcontroller, e.g. "AT90CAN" */
	public String getType() {
		return type;
	}

	/** set type of the microcontroller, e.g. "AT90CAN" */
	public void setType(String type) {
		this.type = type;
	}

	/** get node_id node id of the bootloader */
	public String getNodeId() {
		return node_id;
	}

	/** set node_id node id of the bootloader */
	public void setNodeId(String node_id) {
		this.node_id = node_id;
	}

	/** get node name in the DBC file */
	public String getNodeName() {
		return node_name;
	}

	/** set node name in the DBC file */
	public void setNodeName(String node_name) {
		this.node_name = node_name;
	}

	/** get variable definitions for the non-volatile memory (EEPROM) */
	public List<EEPROMValue> getEepromValues() {
		return eeprom_values;
	}

	/** set variable definitions for the non-volatile memory (EEPROM) */
	public void setEepromValues(Collection<EEPROMValue> eeprom_values) {
		this.eeprom_values = new ArrayList<EEPROMValue>(eeprom_values);
	}
}
