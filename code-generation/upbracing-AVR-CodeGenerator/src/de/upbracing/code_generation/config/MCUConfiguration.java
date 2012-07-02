package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.simpleframework.xml.Default;

import de.upbracing.dbc.DBC;
import de.upbracing.eculist.ECUDefinition;

@Default(required=false)
public class MCUConfiguration {
	//DEBUG
	public int a, b;
	
	private List<ECUDefinition> ecus;
	//TODO we have to wrap the DBC model to allow configuration of the code generation
	private DBC can;

	public List<ECUDefinition> getEcus() {
		return ecus;
	}

	public void setEcus(Collection<ECUDefinition> ecus) {
		this.ecus = new ArrayList<ECUDefinition>(ecus);
	}

	public DBC getCan() {
		return can;
	}

	public void setCan(DBC can) {
		this.can = can;
	}
}
