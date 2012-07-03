package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.simpleframework.xml.Default;

import de.upbracing.dbc.DBC;
import de.upbracing.eculist.ECUDefinition;
import de.upbracing.eculist.EEPROMValue;

@Default(required=false)
public class MCUConfiguration {
	//DEBUG
	public int a, b;
	
	private List<ECUDefinition> ecus;
	private ECUDefinition currentEcu;
	//TODO we have to wrap the DBC model to allow configuration of the code generation
	private DBC can;
	private EEPROMConfig eeprom = new EEPROMConfig();

	public List<ECUDefinition> getEcus() {
		return ecus;
	}

	public void setEcus(Collection<ECUDefinition> ecus) {
		this.ecus = new ArrayList<ECUDefinition>(ecus);
	}

	public ECUDefinition getCurrentEcu() {
		return currentEcu;
	}

	public DBC getCan() {
		return can;
	}

	public void setCan(DBC can) {
		this.can = can;
	}
	
	public EEPROMConfig getEeprom() {
		return eeprom;
	}

	public void selectEcu(ECUDefinition ecu) {
		if (ecus == null || !ecus.contains(ecu))
			throw new IllegalArgumentException("The current ECU must be in the ECU list.");
		
		this.currentEcu = ecu;
		
		for (EEPROMValue v : ecu.getEepromValues()) {
			getEeprom().add(v.getName(), v.getType(), v.getDefault());
		}
	}
	
	public void selectEcu(String name) {
		for (ECUDefinition ecu : getEcus()) {
			if (ecu.getName().equals(name)) {
				selectEcu(ecu);
				return;
			}
		}
		throw new IllegalArgumentException("No such ECU could be found.");
	}
}
