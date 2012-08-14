package de.upbracing.dbc;

import java.util.Collection;
import java.util.Map;

import org.simpleframework.xml.Default;
import org.simpleframework.xml.ElementMap;

/**
 * CAN definition data (model for a DBC file)
 * 
 * @author benny
 */
@Default(required=false)
public class DBC {
	private String version;
	//@Element(required=false)
	//@Convert(ConvertMapMap.class)
	@ElementMap(valueType=DBCValueTable.class)
	private Map<String, DBCValueTable> valueTables;
	private Map<String, DBCEcu> ecus;
	private Map<String, DBCMessage> messages;
	private Collection<String> ecuNames;
	
	/** constructor
	 * 
	 * @param version version string
	 */
	public DBC(String version) {
		super();
		this.version = version;
	}

	/** get version string */
	public String getVersion() {
		return version;
	}

	/** set version string */
	public void setVersion(String version) {
		this.version = version;
	}

	/** get map of value tables (name -> value table) */
	public Map<String, DBCValueTable> getValueTables() {
		return valueTables;
	}

	/** set map of value tables (name -> value table) */
	public void setValueTables(Map<String, DBCValueTable> valueTables) {
		this.valueTables = valueTables;
	}

	/** get map of ecus (ecu name -> ecu definition) */
	public Map<String, DBCEcu> getEcus() {
		return ecus;
	}

	/** set map of ecus (ecu name -> ecu definition) */
	public void setEcus(Map<String, DBCEcu> ecus) {
		this.ecus = ecus;
	}

	/** get map of messages (message name -> message definition) */
	public Map<String, DBCMessage> getMessages() {
		return messages;
	}

	/** set map of messages (message name -> message definition) */
	public void setMessages(Map<String, DBCMessage> messages) {
		this.messages = messages;
	}

	/** get list of ecu names */
	public Collection<String> getEcuNames() {
		return ecuNames;
	}

	/** set list of ecu names */
	public void setEcuNames(Collection<String> ecuNames) {
		this.ecuNames = ecuNames;
	}
}
