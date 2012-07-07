package de.upbracing.dbc;

import java.util.Collection;

import org.simpleframework.xml.Default;

/**
 * ECU definition in a DBC file
 * 
 * @author benny
 */
@Default(required=false)
public class DBCEcu {
	private String name;
	private Collection<DBCMessage> txMsgs, rxMsgs;
	private Collection<DBCSignal> rxSignals;
	private String comment;
	
	/** constructor
	 * 
	 * @param name ECU name
	 */
	public DBCEcu(String name) {
		this.name = name;
	}

	/** get name */
	public String getName() {
		return name;
	}

	/** get comment */
	public String getComment() {
		return comment;
	}

	/** set comment */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/** get list of messages transmitted by this ECU */
	public Collection<DBCMessage> getTxMsgs() {
		return txMsgs;
	}

	/** get list of messages received by this ECU */
	public Collection<DBCMessage> getRxMsgs() {
		return rxMsgs;
	}

	/** get list of signals received by this ECU */
	public Collection<DBCSignal> getRxSignals() {
		return rxSignals;
	}

	/** set list of messages transmitted by this ECU */
	public void setTxMsgs(Collection<DBCMessage> txMsgs) {
		this.txMsgs = txMsgs;
	}

	/** set list of messages received by this ECU */
	public void setRxMsgs(Collection<DBCMessage> rxMsgs) {
		this.rxMsgs = rxMsgs;
	}

	/** set list of signals received by this ECU */
	public void setRxSignals(Collection<DBCSignal> rxSignals) {
		this.rxSignals = rxSignals;
	}
}
