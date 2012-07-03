package de.upbracing.dbc;

import java.util.Collection;
import java.util.Map;

import org.simpleframework.xml.Default;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.convert.Convert;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

@Default(required=false)
public class DBC {
	private String version;
	//@Element(required=false)
	//@Convert(ConvertMapMap.class)
	@ElementMap(valueType=DBCValueTable.class)
	private Map<String, DBCValueTable> valueTables;
	private Map<String, DBCEcu> ecus;
	private Map<String, DBCMessage> messages;
	private Map<String, DBCSignal> signals;
	private Collection<String> ecuNames;
	
	public DBC(String version) {
		super();
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, DBCValueTable> getValueTables() {
		return valueTables;
	}

	public void setValueTables(Map<String, DBCValueTable> valueTables) {
		this.valueTables = valueTables;
	}

	public Map<String, DBCEcu> getEcus() {
		return ecus;
	}

	public void setEcus(Map<String, DBCEcu> ecus) {
		this.ecus = ecus;
	}

	public Map<String, DBCMessage> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, DBCMessage> messages) {
		this.messages = messages;
	}

	public Map<String, DBCSignal> getSignals() {
		return signals;
	}

	public void setSignals(Map<String, DBCSignal> signals) {
		this.signals = signals;
	}

	public Collection<String> getEcuNames() {
		return ecuNames;
	}

	public void setEcuNames(Collection<String> ecuNames) {
		this.ecuNames = ecuNames;
	}
	
	//TODO implement serialization for value tables
	private class ConvertMapMap implements Converter<Map<String, Map<String, String>>> {
		@Override
		public Map<String, Map<String, String>> read(InputNode arg0)
				throws Exception {
			return null;
		}

		@Override
		public void write(OutputNode arg0, Map<String, Map<String, String>> arg1)
				throws Exception {
			
		}
	}
}
