package de.upbracing.code_generation.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map;

import de.upbracing.dbc.DBCEcu;
import de.upbracing.dbc.DBCMessage;
import de.upbracing.dbc.DBCSignal;

public class DBCEcuConfig extends DBCEcu {

	public DBCEcuConfig(DBCEcu ecu) {
		super(ecu.getName());
		setComment(ecu.getComment());
		
		//Old messages and signals are only used temporarily and must later be replaced 
		//by the correct message/signalConfig objects
		setRxMsgs(ecu.getRxMsgs());
		setRxSignals(ecu.getRxSignals());
		setTxMsgs(ecu.getTxMsgs());
	}

	public void replaceSignalObjects(Map<DBCSignal, DBCSignalConfig> signalMap) {
		//Replace RX Signals
				
		Collection<DBCSignal> newRxSignals = new Vector<DBCSignal>();
		for (Iterator<DBCSignal> signal = getRxSignals().iterator(); signal.hasNext(); )
		{
			DBCSignalConfig signalconfig = signalMap.get(signal.next());
			if (signalconfig != null)
				newRxSignals.add(signalconfig);

		}
		setRxSignals(newRxSignals);
		
	}
	
	public void replaceMessageObjects(Map<DBCMessage, DBCMessageConfig> messageMap) {
		//Replace RX and TX Messages
		
		Collection<DBCMessage> newRxMsgs = new Vector<DBCMessage>();
		for (Iterator<DBCMessage> message = getRxMsgs().iterator(); message.hasNext(); )
		{
			newRxMsgs.add(messageMap.get(message.next()));
		}
		setRxMsgs(newRxMsgs);
		
		Collection<DBCMessage> newTxMsgs = new Vector<DBCMessage>();
		for (Iterator<DBCMessage> message = getTxMsgs().iterator(); message.hasNext(); )
		{
			newTxMsgs.add(messageMap.get(message.next()));
		}
		setTxMsgs(newTxMsgs);
		
		
	}
}
