package de.upbracing.code_generation.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.upbracing.dbc.DBC;
import de.upbracing.dbc.DBCEcu;
import de.upbracing.dbc.DBCMessage;

/**
 * Wrapper for DBC class.
 * Used for configuration of code generator.
 * 
 * @author sven
 */
public class DBCConfig extends DBC {
	private String header_declarations;
	private String cfile_declarations;
	private MCUConfiguration config;
	
	public final String NL = System.getProperties().getProperty("line.separator");

	public DBCConfig(DBC dbc, MCUConfiguration config) {
		super(dbc.getVersion());
		this.config = config;
		
		setEcuNames(dbc.getEcuNames());
		
		//The value tables are directly used
		setValueTables(dbc.getValueTables());
		
		//Convert ecu list to a list with EcuConfig objects and store the mapping in a hashmap
		setEcus(new HashMap<String, DBCEcu>());
		Map<DBCEcu, DBCEcuConfig> ecuMap = new HashMap<DBCEcu, DBCEcuConfig>(); 
		Map<String, DBCEcu> oldEcus = dbc.getEcus();
		for(Map.Entry<String, DBCEcu> entry : oldEcus.entrySet()) {
			DBCEcuConfig newEcu = new DBCEcuConfig(entry.getValue());
			getEcus().put(entry.getKey(), newEcu);
			ecuMap.put(entry.getValue(), newEcu);
		}
		
		
		//Convert the messages to messageConfig objects
		setMessages(new HashMap<String, DBCMessage>());
		Map<DBCMessage, DBCMessageConfig> messageMap = new HashMap<DBCMessage, DBCMessageConfig>();
		Map<String, DBCMessage> oldMessages = dbc.getMessages();
		for(Map.Entry<String, DBCMessage> entry : oldMessages.entrySet()) {
			//Convert the txEcu list to a list with the DBCEcuConfig objects
			List<DBCEcu> newtxecus = new LinkedList<DBCEcu>();
			for ( Iterator<DBCEcu> ecu = entry.getValue().getTxEcus().iterator(); ecu.hasNext(); )
			{
				newtxecus.add(ecuMap.get(ecu.next()));
			}
			
			//Because all messages appear twice in the messages map, 
			//look up first if we have already created the object...
			DBCMessageConfig newMessage;
			if (messageMap.containsKey(entry.getValue())) {
				 newMessage = messageMap.get(entry.getValue());
			} else {
				 newMessage = new DBCMessageConfig(entry.getValue(), newtxecus);
			}
			
			getMessages().put(entry.getKey(), newMessage);
			messageMap.put(entry.getValue(), newMessage);
		}
		
		//For all messages, replace the signals with signalConfig objects
		for(Map.Entry<String, DBCMessage> entry : getMessages().entrySet()) {
			if (entry.getKey().equals(entry.getValue().getName())) { //Check to not replace it twice
				((DBCMessageConfig)entry.getValue()).replaceSignalObjects(ecuMap);
			}
		}

		//For all ecus, replace the signals with signalConfig objects and messages with messageConfig objects
		for(Map.Entry<String, DBCEcu> entry : getEcus().entrySet()) {
			((DBCEcuConfig)entry.getValue()).replaceMessageObjectsAndSignals(messageMap);
		}

			
	}
	
	public DBCMessageConfig getMessage(String name) {
		if (getMessages().containsKey(name))
			return (DBCMessageConfig)getMessages().get(name);
		
		//Also look for aliases
		for(Map.Entry<String, DBCMessage> entry : getMessages().entrySet()) {
			if (((DBCMessageConfig)entry.getValue()).getAliases().contains(name)) 
				return (DBCMessageConfig)entry.getValue();
		}
		
		throw new IllegalArgumentException("Couldn't find this message: " + name);

	}
	
	public DBCEcu getEcu(String name) {
		if (getEcus().containsKey(name))
			return getEcus().get(name);
		
		throw new IllegalArgumentException("Couldn't find this ecu: " + name);
	}
	
	/** alias for {@link #getMessage} */
	public DBCMessageConfig msg(String name) {
		return getMessage(name);
	}
	
	/**
	 * Messages without signals cannot be assigned to ecus in the dbc file.
	 * Therefore they can be added manually. 
	 * This method is called by addRx(String)
	 * 
	 * @param msg DBCMessageConfig object
	 */
	public void addRx(DBCMessageConfig msg) {
		DBCEcuConfig ecu = null;
		
		if (config.getCurrentEcu() != null) {
			ecu = (DBCEcuConfig)getEcu(config.getCurrentEcu().getNodeName());
		
			//If it fails, try with normal name
			if (ecu == null)
				ecu = (DBCEcuConfig)getEcu(config.getCurrentEcu().getName());
		}
		
		if (ecu == null) {
			throw new IllegalArgumentException("Couldn't determine the currect ecu ");
		}
		
		ecu.getRxMsgs().add(msg);
	}
	
	/**
	 * Messages without signals cannot be assigned to ecus in the dbc file.
	 * Therefore they can be added here manually. 
	 * This method can be called by the config script
	 * 
	 * @param msgname a string with the message name
	 */
	public void addRx(String msgname) {
		addRx(getMessage(msgname));
	}

	public String getHeaderDeclarations() {
		return header_declarations;
	}

	public void setHeaderDeclarations(String header_declarations) {
		this.header_declarations = header_declarations;
	}

	public String getCFileDeclarations() {
		return cfile_declarations;
	}

	public void setCFileDeclarations(String cfile_declarations) {
		this.cfile_declarations = cfile_declarations;
	}

	private String combineDeclarations(String a, String b) {
		if (a != null)
			return a + NL + b;
		else
			return b;
	}

	public void addDeclarations(String decls) {
		header_declarations = combineDeclarations(header_declarations, decls);
	}
	
	public void addDeclarationsInCFile(String decls) {
		cfile_declarations = combineDeclarations(cfile_declarations, decls);
	}
}
