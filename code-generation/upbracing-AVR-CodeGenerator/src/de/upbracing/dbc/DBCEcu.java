package de.upbracing.dbc;

import java.util.Collection;

import org.simpleframework.xml.Default;

@Default(required=false)
public class DBCEcu {
	private String name;
	private Collection<DBCMessage> txMsgs, rxMsgs;
	private Collection<DBCSignal> rxSignals;
	private String comment;
	
	public DBCEcu(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Collection<DBCMessage> getTxMsgs() {
		return txMsgs;
	}

	public Collection<DBCMessage> getRxMsgs() {
		return rxMsgs;
	}

	public Collection<DBCSignal> getRxSignals() {
		return rxSignals;
	}

	public void setTxMsgs(Collection<DBCMessage> txMsgs) {
		this.txMsgs = txMsgs;
	}

	public void setRxMsgs(Collection<DBCMessage> rxMsgs) {
		this.rxMsgs = rxMsgs;
	}

	public void setRxSignals(Collection<DBCSignal> rxSignals) {
		this.rxSignals = rxSignals;
	}
}
