package de.upbracing.code_generation.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.upbracing.dbc.DBCEcu;
import de.upbracing.dbc.DBCMessage;
import de.upbracing.dbc.DBCSignal;

public class DBCMessageConfig extends DBCMessage {

	private String alias;
	private String rxMob;
	private String txMob;
	private boolean usingGeneralTransmitter;
	
	
	public DBCMessageConfig(DBCMessage message, List<DBCEcu> newtxecus) {
		super(message.getId(), message.getRawId(), message.isExtended(),
				message.getName(), message.getLength(), newtxecus);
		setComment(message.getComment());
		
		//Old signals are only used temporarily and must later be replaced by the correct signalConfig objects
		setSignals(message.getSignals());
		setSignalOrder(message.getSignalOrder());
	}

	public void replaceSignalObjects(Map<DBCSignal, DBCSignalConfig> signalMap) {
		//Replace Signals and SignalOrder
		
		Map<String, DBCSignal> newSignals = new HashMap<String, DBCSignal>();
		for(Map.Entry<String, DBCSignal> entry : getSignals().entrySet()) {
			newSignals.put(entry.getKey(), signalMap.get(entry.getValue()));
		}
		setSignals(newSignals);
		
		
		Collection<DBCSignal> newSignalOrder = new LinkedList<DBCSignal>();
		for (Iterator<DBCSignal> signal = getSignalOrder().iterator(); signal.hasNext(); )
		{
			newSignalOrder.add(signalMap.get(signal.next()));
		}
		setSignalOrder(newSignalOrder);
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isUsingGeneralTransmitter() {
		return usingGeneralTransmitter;
	}

	public void setUsingGeneralTransmitter(boolean usingGeneralTransmitter) {
		this.usingGeneralTransmitter = usingGeneralTransmitter;
	}

	public String getRxMob() {
		return rxMob;
	}

	public void setRxMob(String rxMob) {
		this.rxMob = rxMob;
	}

	public String getTxMob() {
		return txMob;
	}

	public void setTxMob(String txMob) {
		this.txMob = txMob;
	}

}
