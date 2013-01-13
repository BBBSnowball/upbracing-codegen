package de.upbracing.dbc;

import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Default;

/**
 * signal definition in a DBC file
 * 
 * @author benny
 */
@Default(required=false)
public class DBCSignal {
	private String name, sign, endianness;
	private int start, length;
	private DBCMessage message;
	private float factor, offset, min_limit, max_limit;
	private String unit;
	private List<DBCEcu> rx_ecus;
	private Map<String, String> values;
	private String valueTable;
	
	/** constructor
	 * 
	 * @param name signal name
	 * @param sign signed? ("+" - unsigned, "-" - signed)
	 * @param endianness endianness from DBC file ("0" or "1")
	 * @param start start bit as defined by DBC
	 * @param length length in bits
	 * @param message message the signal belongs to
	 * @param factor factor for conversion to engineering units
	 * @param offset offset for conversion to engineering units
	 * @param min_limit minimum value in engineering units
	 * @param max_limit maximum value in engineering units
	 * @param unit name of engineering unit
	 * @param rx_ecus list of ECUs that receive this signal
	 */
	public DBCSignal(String name, String sign, String endianness, int start,
			int length, DBCMessage message, float factor, float offset,
			float min_limit, float max_limit, String unit, List<DBCEcu> rx_ecus) {
		this.name = name;
		this.sign = sign;
		this.endianness = endianness;
		this.start = start;
		this.length = length;
		this.message = message;
		this.factor = factor;
		this.offset = offset;
		this.min_limit = min_limit;
		this.max_limit = max_limit;
		this.unit = unit;
		this.rx_ecus = rx_ecus;
	}

	/** get signal name */
	public String getName() {
		return name;
	}

	/** is value signed? ("+" - unsigned, "-" - signed) */
	public String getSign() {
		return sign;
	}
	
	/** true for signed values, false for unsigned */
	public boolean isSignedValue() {
		if (sign.equals("+"))
			return false;
		else if (sign.equals("-"))
			return true;
		else
			throw new IllegalStateException("invalid DBC file: sign is neither '+' nor '-'");
	}

	/** get endianness from DBC file ("0" or "1") */
	public String getEndianness() {
		return endianness;
	}

	/** start bit as defined by DBC */
	public int getStart() {
		return start;
	}

	/** get length in bits */
	public int getLength() {
		return length;
	}

	/** get message the signal belongs to */
	public DBCMessage getMessage() {
		return message;
	}
	
	/** get factor for conversion to engineering units */
	public float getFactor() {
		return factor;
	}

	/** get offset for conversion to engineering units */
	public float getOffset() {
		return offset;
	}

	/** get minimum value in engineering units */
	public float getMinLimit() {
		return min_limit;
	}

	/** get maximum value in engineering units */
	public float getMaxLimit() {
		return max_limit;
	}

	/** get name of engineering unit */
	public String getUnit() {
		return unit;
	}

	/** get list of ECUs that receive this signal */
	public List<DBCEcu> getRxEcus() {
		return rx_ecus;
	}

	/** get value table or null */
	public Map<String, String> getValues() {
		return values;
	}

	/** set value table */
	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	/** get name of value table or null */
	public String getValueTable() {
		return valueTable;
	}

	/** set name of value table */
	public void setValueTable(String valueTable) {
		this.valueTable = valueTable;
	}
}
