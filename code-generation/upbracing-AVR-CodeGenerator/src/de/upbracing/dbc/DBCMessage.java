package de.upbracing.dbc;

import java.util.Collection;
import java.util.Map;

import org.simpleframework.xml.Default;

/**
 * message definition in a DBC file
 * 
 * @author benny
 */
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
	
	/** constructor
	 * 
	 * @param id CAN message id
	 * @param raw_id raw id string from the DBC file
	 * @param extended extended message?
	 * @param name message name
	 * @param length message length (DLC, in bytes)
	 * @param txEcus list of ECUs that transmit this message
	 */
	public DBCMessage(int id, String raw_id, boolean extended, String name,
			int length, Collection<DBCEcu> txEcus) {
		this.id = id;
		this.raw_id = raw_id;
		this.extended = extended;
		this.name = name;
		this.length = length;
		this.txEcus = txEcus;
	}

	/** get comment */
	public String getComment() {
		return comment;
	}

	/** set comment */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/** get message id */
	public int getId() {
		return id;
	}

	/** get raw message id */
	public String getRawId() {
		return raw_id;
	}

	/** is this an extended message? */
	public boolean isExtended() {
		return extended;
	}

	/** get name */
	public String getName() {
		return name;
	}

	/** get length */
	public int getLength() {
		return length;
	}

	/** get list of ECUs that transmit this message */
	public Collection<DBCEcu> getTxEcus() {
		return txEcus;
	}

	/** get signals of this message */
	public Map<String, DBCSignal> getSignals() {
		return signals;
	}

	/** get order of the signals */
	public Collection<DBCSignal> getSignalOrder() {
		return signalOrder;
	}

	/** set list of ECUs that transmit this message */
	public void setTxEcus(Collection<DBCEcu> txEcus) {
		this.txEcus = txEcus;
	}

	/** set signals of this message */
	public void setSignals(Map<String, DBCSignal> signals) {
		this.signals = signals;
	}

	/** set order of the signals */
	public void setSignalOrder(Collection<DBCSignal> signalOrder) {
		this.signalOrder = signalOrder;
	}
	
}
