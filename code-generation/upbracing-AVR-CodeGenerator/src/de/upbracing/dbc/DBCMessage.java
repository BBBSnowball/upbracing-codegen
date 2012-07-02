package de.upbracing.dbc;

import java.util.Collection;
import java.util.Map;

import org.simpleframework.xml.Default;

@Default(required=false)
public class DBCMessage {
	private int id;
	private String raw_id;
	private boolean extended;
	private String name;
	private int length;
	private Collection<DBCEcu> txEcus;
	private Map<String, DBCSignal> signals;
	private Collection<DBCSignal> signalOrder;
	private String comment;
	
	public DBCMessage(int id, String raw_id, boolean extended, String name,
			int length, Collection<DBCEcu> txEcus) {
		super();
		this.id = id;
		this.raw_id = raw_id;
		this.extended = extended;
		this.name = name;
		this.length = length;
		this.txEcus = txEcus;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getId() {
		return id;
	}

	public String getRawId() {
		return raw_id;
	}

	public boolean isExtended() {
		return extended;
	}

	public String getName() {
		return name;
	}

	public int getLength() {
		return length;
	}

	public Collection<DBCEcu> getTxEcus() {
		return txEcus;
	}

	public Map<String, DBCSignal> getSignals() {
		return signals;
	}

	public Collection<DBCSignal> getSignalOrder() {
		return signalOrder;
	}

	public void setTxEcus(Collection<DBCEcu> txEcus) {
		this.txEcus = txEcus;
	}

	public void setSignals(Map<String, DBCSignal> signals) {
		this.signals = signals;
	}

	public void setSignalOrder(Collection<DBCSignal> signalOrder) {
		this.signalOrder = signalOrder;
	}
	
}
