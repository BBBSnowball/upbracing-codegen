package de.upbracing.code_generation.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.upbracing.dbc.DBC;
import de.upbracing.dbc.DBCEcu;
import de.upbracing.dbc.DBCMessage;
import de.upbracing.dbc.DBCSignal;

/**
 * Wrapper for DBC class.
 * Used for configuration of code generator.
 * 
 * @author sven
 */
public class DBCConfig extends DBC {


	public DBCConfig(DBC dbc) {
		super(dbc.getVersion());
		
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
		
		
		//Convert the signals to signalConfig objects
		setSignals(new HashMap<String, DBCSignal>());
		Map<DBCSignal, DBCSignalConfig> signalMap = new HashMap<DBCSignal, DBCSignalConfig>();
		Map<String, DBCSignal> oldSignals = dbc.getSignals();
		for(Map.Entry<String, DBCSignal> entry : oldSignals.entrySet()) {
			//Convert the rxEcu list to a list with the DBCEcuConfig objects
			List<DBCEcu> newrxecus = new LinkedList<DBCEcu>();
			for ( Iterator<DBCEcu> ecu = entry.getValue().getRxEcus().iterator(); ecu.hasNext(); )
			{
				newrxecus.add(ecuMap.get(ecu.next()));
			}
			
			DBCSignalConfig newSignal = new DBCSignalConfig(entry.getValue(), newrxecus, 
					messageMap.get(entry.getValue().getMessage()));
			
			getSignals().put(entry.getKey(), newSignal);
			signalMap.put(entry.getValue(), newSignal);
		}
		
		
		//For all messages, replace the signals with signalConfig objects
		for(Map.Entry<String, DBCMessage> entry : getMessages().entrySet()) {
			((DBCMessageConfig)entry.getValue()).replaceSignalObjects(signalMap);
		}

		//For all ecus, replace the signals with signalConfig objects and messages with messageConfig objects
		for(Map.Entry<String, DBCEcu> entry : getEcus().entrySet()) {
			((DBCEcuConfig)entry.getValue()).replaceMessageObjects(messageMap);
			((DBCEcuConfig)entry.getValue()).replaceSignalObjects(signalMap);
		}

			
	}
	
	public DBCMessage getMessage(String name) {
		if (getMessages().containsKey(name))
			return getMessages().get(name);
		
		throw new RuntimeException("Unknown Message \"" + name + "\""); //TODO replace with special exception

	}
	
	public DBCEcu getEcu(String name) {
		if (getEcus().containsKey(name))
			return getEcus().get(name);
		
		throw new RuntimeException("Unknown ECU \"" + name + "\""); //TODO replace with special exception
	}

}
