package de.upbracing.dbc;

import java.util.Collection;
import java.util.Map;

public class DBC {
	private String version;
	private Map<String, Map<String, String>> valueTables;
	private Map<String, DBCEcu> ecus;
	private Map<String, DBCMessage> messages;
	private Map<String, DBCSignal> signals;
	private Collection<String> ecuNames;
	
	public DBC(String version) {
		super();
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, Map<String, String>> getValueTables() {
		return valueTables;
	}

	public void setValueTables(Map<String, Map<String, String>> valueTables) {
		this.valueTables = valueTables;
	}

	public Map<String, DBCEcu> getEcus() {
		return ecus;
	}

	public void setEcus(Map<String, DBCEcu> ecus) {
		this.ecus = ecus;
	}

	public Map<String, DBCMessage> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, DBCMessage> messages) {
		this.messages = messages;
	}

	public Map<String, DBCSignal> getSignals() {
		return signals;
	}

	public void setSignals(Map<String, DBCSignal> signals) {
		this.signals = signals;
	}

	public Collection<String> getEcuNames() {
		return ecuNames;
	}

	public void setEcuNames(Collection<String> ecuNames) {
		this.ecuNames = ecuNames;
	}
}
