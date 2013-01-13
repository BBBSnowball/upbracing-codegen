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
	
	private String beforeTx = null;
	private String afterTx = null;
	private String immBeforeTx = null;
	private String immAfterTx = null;
	private String getValue = null;
	private String param = null;
	
	//Code replacements for periodic tasks
	private String beforeTask = null;
	private String afterTask = null;
	private String beforeReadValueTask = null;
	private String afterReadValueTask = null;
	private String readValueTask = null;
	
	private boolean noGlobalVar = false;
	private String globalVarName = null;
	
	// The raw value on the CAN bus can be converted to a physical
	// value using offset and factor. According to CAETEC DBC file
	// format documentation:
	//   physical_value = raw_value * factor + offset
	//   raw_value = (physical_value – offset) / factor
	// We use fractions for the factor, so we could implement it on
	// the MCU without using floating-point arithmetics. At the moment,
	// those values are only used to print a warning.
	
	/** expected offset of the value */
	private float expected_offset = 0;
	/** expected factor (numerator part) */
	private int expected_factor_numerator = 1;
	/** expected factor (denominator part) */
	private int expected_factor_denominator = 1;

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
			if (getSign().equals("+"))
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

	public String getBeforeTx() {
		return beforeTx;
	}

	public void setBeforeTx(String beforeTx) {
		this.beforeTx = beforeTx;
	}

	public String getAfterTx() {
		return afterTx;
	}

	public void setAfterTx(String afterTx) {
		this.afterTx = afterTx;
	}

	public String getImmBeforeTx() {
		return immBeforeTx;
	}

	public void setImmBeforeTx(String immBeforeTx) {
		this.immBeforeTx = immBeforeTx;
	}

	public String getImmAfterTx() {
		return immAfterTx;
	}

	public void setImmAfterTx(String immAfterTx) {
		this.immAfterTx = immAfterTx;
	}

	public String getGetValue() {
		return getValue;
	}

	public void setGetValue(String getValue) {
		this.getValue = getValue;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getBeforeTask() {
		return beforeTask;
	}

	public void setBeforeTask(String beforeTask) {
		this.beforeTask = beforeTask;
	}

	public String getAfterTask() {
		return afterTask;
	}

	public void setAfterTask(String afterTask) {
		this.afterTask = afterTask;
	}

	public String getBeforeReadValueTask() {
		return beforeReadValueTask;
	}

	public void setBeforeReadValueTask(String beforeReadValueTask) {
		this.beforeReadValueTask = beforeReadValueTask;
	}

	public String getAfterReadValueTask() {
		return afterReadValueTask;
	}

	public void setAfterReadValueTask(String afterReadValueTask) {
		this.afterReadValueTask = afterReadValueTask;
	}

	public String getReadValueTask() {
		return readValueTask;
	}

	public void setReadValueTask(String readValueTask) {
		this.readValueTask = readValueTask;
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

	/** get expected offset for conversion to physical units (physical_value = raw_value * factor + offset) */
	public float getExpectedOffset() {
		return expected_offset;
	}

	/** set expected offset for conversion to physical units (physical_value = raw_value * factor + offset) */
	public void setExpectedOffset(float offset) {
		this.expected_offset = offset;
	}

	/** get expected factor (numerator part) for conversion to physical units (physical_value = raw_value * factor + offset) */
	public int getExpectedFactorNumerator() {
		return expected_factor_numerator;
	}

	/** get expected factor (denominator part) for conversion to physical units (physical_value = raw_value * factor + offset) */
	public int getExpectedFactorDenominator() {
		return expected_factor_denominator;
	}
	
	/** get expected factor (as float) for conversion to physical units (physical_value = raw_value * factor + offset) */
	public float getExpectedFactor() {
		return expected_factor_numerator / (float) expected_factor_denominator;
	}

	/** get expected factor (as fraction) for conversion to physical units (physical_value = raw_value * factor + offset) */
	public void setExpectedFactor(int numerator, int denominator) {
		this.expected_factor_numerator = numerator;
		this.expected_factor_denominator = denominator;
	}

	/** get expected factor (as Ruby fraction object) for conversion to physical units (physical_value = raw_value * factor + offset) */
	public void setExpectedFactor(org.jruby.RubyRational factor) {
		//NOTE numerator and denominator don't use the context parameter, so we can
		//     pass a null. This might change though...
		this.expected_factor_numerator = (Integer)factor.numerator(null).toJava(Integer.class);
		this.expected_factor_denominator = (Integer)factor.denominator(null).toJava(Integer.class);
	}
}
