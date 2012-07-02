package de.upbracing.dbc;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DBCSignal {
	private String name, sign, endianness;
	private int start, length;
	private DBCMessage message;
	private float factor, offset, min_limit, max_limit;
	private String unit;
	private List<DBCEcu> rx_ecus;
	private Map<String, String> values;
	private String valueTable;
	
	public DBCSignal(String name, String sign, String endianness, int start,
			int length, DBCMessage message, float factor, float offset,
			float min_limit, float max_limit, String unit, List<DBCEcu> rx_ecus) {
		super();
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

	public String getName() {
		return name;
	}

	public String getSign() {
		return sign;
	}

	public String getEndianness() {
		return endianness;
	}

	public int getStart() {
		return start;
	}

	public int getLength() {
		return length;
	}

	public DBCMessage getMessage() {
		return message;
	}

	public float getFactor() {
		return factor;
	}

	public float getOffset() {
		return offset;
	}

	public float getMinLimit() {
		return min_limit;
	}

	public float getMaxLimit() {
		return max_limit;
	}

	public String getUnit() {
		return unit;
	}

	public List<DBCEcu> getRxEcus() {
		return rx_ecus;
	}

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	public String getValueTable() {
		return valueTable;
	}

	public void setValueTable(String valueTable) {
		this.valueTable = valueTable;
	}
}
