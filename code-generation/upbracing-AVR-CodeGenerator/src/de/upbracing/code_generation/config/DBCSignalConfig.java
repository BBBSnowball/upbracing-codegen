package de.upbracing.code_generation.config;

import java.util.List;

import de.upbracing.dbc.DBCEcu;
import de.upbracing.dbc.DBCSignal;

public class DBCSignalConfig extends DBCSignal {

	private String beforeRx = null;
	private String afterRx = null;
	private String immBeforeRx = null;
	private String immAfterRx = null;
	private String putValue = null;
	private boolean noGlobalVar = false;
	private String globalVarName = null;

	public DBCSignalConfig(DBCSignal signal, List<DBCEcu> newrxecus, DBCMessageConfig newMessage) {
		//unfortunately the creation of the new rxEcu List has to be done before the constructor is called
		//and not here because Java doesn't allow (even sideeffectless) statements before super() 
		
		super(signal.getName(), signal.getSign(), signal.getEndianness(), signal.getStart(),
				signal.getLength(), newMessage, signal.getFactor(), signal.getOffset(),
				signal.getMinLimit(), signal.getMaxLimit(), signal.getUnit(), newrxecus);

		setValues(signal.getValues());
		setValueTable(signal.getValueTable());

	}
	
	/**
	 * returns the c type of the needed variable. i.e. "uint8_t"
	 * It takes into account the length and the sign
	 * 
	 * @return the c type
	 */
	public String getCType() {
		
		//if (!empty($signal['value_table']))
			//return $signal['value_table'];
		if (getValueTable() != null && !getValueTable().isEmpty())
			return getValueTable();
		else {
			if (getSign() == "+")
				return "uint" + getLength() + "_t";
			else
				return "int" + getLength() + "_t";
		}		
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

	public String getImmBeforeRx() {
		return immBeforeRx;
	}

	public void setImmBeforeRx(String immBeforeRx) {
		this.immBeforeRx = immBeforeRx;
	}

	public String getImmAfterRx() {
		return immAfterRx;
	}

	public void setImmAfterRx(String immAfterRx) {
		this.immAfterRx = immAfterRx;
	}

	public String getPutValue() {
		return putValue;
	}

	public void setPutValue(String putValue) {
		this.putValue = putValue;
	}

	public boolean isNoGlobalVar() {
		return noGlobalVar;
	}

	public void setNoGlobalVar(boolean noGlobalVar) {
		this.noGlobalVar = noGlobalVar;
	}

	/**
	 * Returns the name of the global variable associated to this signal.
	 * If no custom name is set the name is equal to getName().
	 * 
	 * @return name of the global variable
	 */
	public String getGlobalVarName() {
		if (globalVarName == null) {
			return getName();
		}
		return globalVarName;
	}

	public void setGlobalVarName(String globalVarName) {
		this.globalVarName = globalVarName;
	}
	
}


