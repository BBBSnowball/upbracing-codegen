package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.upbracing.dbc.DBCEcu;
import de.upbracing.dbc.DBCMessage;
import de.upbracing.dbc.DBCSignal;

public class DBCEcuConfig extends DBCEcu {

	private List<Mob> mobs = new ArrayList<Mob>();
	private List<List<DBCMessageConfig>> sendingTasks = new LinkedList<List<DBCMessageConfig>>();
	
	public DBCEcuConfig(DBCEcu ecu) {
		super(ecu.getName());
		setComment(ecu.getComment());
		
		//Old messages and signals are only used temporarily and must later be replaced 
		//by the correct message/signalConfig objects
		setRxMsgs(ecu.getRxMsgs());
		setRxSignals(ecu.getRxSignals());
		setTxMsgs(ecu.getTxMsgs());
	}
	
	public void addMob(Mob mob) {
		mobs.add(mob);
	}
	
	public List<Mob> getMobs() {
		return mobs;
	}
	
	public Mob getMobByName(String name) {
		for(Mob mob : mobs) {
			if (mob.getName().equals(name)) return mob;
		}
		return null;
	}

	public List<List<DBCMessageConfig>> getSendingTasks() {
		return sendingTasks;
	}

	public DBCMessageConfig getMessage(String name) {
		for (DBCMessage msg : getRxMsgs())
			if (msg.getName().equals(name))
				return (DBCMessageConfig)msg;

		for (DBCMessage msg : getTxMsgs())
			if (msg.getName().equals(name))
				return (DBCMessageConfig)msg;
		
		throw new IllegalArgumentException("Unknown Message \"" + name + "\"");
	}
	
	public void replaceMessageObjectsAndSignals(Map<DBCMessage, DBCMessageConfig> messageMap) {
		//Replace RX and TX Messages
		
		Collection<DBCMessage> newRxMsgs = new ArrayList<DBCMessage>(getRxMsgs().size());
		for (DBCMessage message : getRxMsgs()) {
			newRxMsgs.add(messageMap.get(message));
		}
		setRxMsgs(newRxMsgs);
		
		Collection<DBCMessage> newTxMsgs = new ArrayList<DBCMessage>(getTxMsgs().size());
		for (Iterator<DBCMessage> message = getTxMsgs().iterator(); message.hasNext(); )
		{
			newTxMsgs.add(messageMap.get(message.next()));
		}
		setTxMsgs(newTxMsgs);
		
		
		Collection<DBCSignal> newRxSignals = new ArrayList<DBCSignal>(getRxSignals().size());
		for (DBCSignal signal : getRxSignals()) {
			DBCMessage newMessage = messageMap.get(signal.getMessage());
			DBCSignal newSignal = newMessage.getSignals().get(signal.getName());
			newRxSignals.add(newSignal);
		}
		setRxSignals(newRxSignals);
	}
}
