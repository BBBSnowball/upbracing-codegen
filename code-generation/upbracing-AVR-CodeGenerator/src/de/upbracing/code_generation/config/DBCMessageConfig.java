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
	private String rxHandler = null;
	private String beforeRx = null;
	private String afterRx = null;
	private boolean usingGeneralTransmitter = false;
	private boolean noSendMessage = false;
	private boolean rtr = false; //Not sure what this does? (needed for can_id_for_mob function)
	
	public DBCMessageConfig(DBCMessage message, List<DBCEcu> newtxecus) {
		super(message.getId(), message.getRawId(), message.isExtended(),
				message.getName(), message.getLength(), newtxecus);
		setComment(message.getComment());
		
		//Old signals are only used temporarily and must later be replaced by the correct signalConfig objects
		setSignals(message.getSignals());
		setSignalOrder(message.getSignalOrder());
	}

	public void replaceSignalObjects(Map<DBCEcu, DBCEcuConfig> ecuMap) {
		//Convert the signals to signalConfig objects
		Map<DBCSignal, DBCSignalConfig> signalMap = new HashMap<DBCSignal, DBCSignalConfig>();
		Map<String, DBCSignal> oldSignals = getSignals();
		Map<String, DBCSignal> newSignals = new HashMap<String, DBCSignal>();
		for(Map.Entry<String, DBCSignal> entry : oldSignals.entrySet()) {
			//Convert the rxEcu list to a list with the DBCEcuConfig objects
			List<DBCEcu> newrxecus = new LinkedList<DBCEcu>();
			for ( Iterator<DBCEcu> ecu = entry.getValue().getRxEcus().iterator(); ecu.hasNext(); )
			{
				newrxecus.add(ecuMap.get(ecu.next()));
			}
			
			DBCSignalConfig newSignal = new DBCSignalConfig(entry.getValue(), newrxecus, 
					this);
			
			newSignals.put(entry.getKey(), newSignal);
			signalMap.put(entry.getValue(), newSignal);
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

	public boolean isNoSendMessage() {
		return noSendMessage;
	}

	public void setNoSendMessage(boolean noSendMessage) {
		this.noSendMessage = noSendMessage;
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

	public String getRxHandler() {
		return rxHandler;
	}

	public void setRxHandler(String rxHandler) {
		this.rxHandler = rxHandler;
	}

	public String getBeforeRx() {
		return beforeRx;
	}

	public void setBeforeRx(String beforeRx) {
		this.beforeRx = beforeRx;
	}

	public String getAfterRx() {
		return afterRx;
	}

	public void setAfterRx(String afterRx) {
		this.afterRx = afterRx;
	}
	
	public boolean isRtr() {
		return rtr;
	}

	public void setRtr(boolean rtr) {
		this.rtr = rtr;
	}
	
	/**
	 * Calculates the CAN id for this message
	 * 
	 * @return int array of length 4 with can id
	 */
	public int[] canIdForMob() {
		// returns id and mask; you could write that to CANIDTx/CANIDMx like that
		// CANIDT1 = $res['id'][0]; CANIDT2 = $res['id'][1]; ...
		// CANIDM1 = $res['mask'][0]; CANIDM2 = $res['mask'][1]; ...
		// if ($res['ide']) CANCDMOB |= (1<<IDE); else CANCDMOB &= ~(1<<IDE);
		// A zero bit in $res['significant'] means that this bit will be ignored
		// by the mcu, no matter how the CANIDMx registers are set.
		
		int id = (int)getId();
		boolean rtr =  isRtr();
		int rtrtag = rtr ? 0x04 : 0;
				
		if (isExtended())
			return new int[]{(id >> 21) & 0xff, (id >> 13) & 0xff, (id >> 5) & 0xff, rtrtag | ((id & 0x1f) << 3)};
		else
			return new int[]{(id >> 3) & 0xff, (id & 7) << 5, 0, rtrtag};		
	}

}
