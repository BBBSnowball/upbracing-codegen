package de.upbracing.code_generation.config;

import java.util.List;

import de.upbracing.dbc.DBCEcu;
import de.upbracing.dbc.DBCSignal;

public class DBCSignalConfig extends DBCSignal {

	public DBCSignalConfig(DBCSignal signal, List<DBCEcu> newrxecus, DBCMessageConfig newMessage) {
		//unfortunately the creation of the new rxEcu List has to be done before the constructor is called
		//and not here because Java doesn't allow (even sideeffectless) statements before super() 
		
		super(signal.getName(), signal.getSign(), signal.getEndianness(), signal.getStart(),
				signal.getLength(), newMessage, signal.getFactor(), signal.getOffset(),
				signal.getMinLimit(), signal.getMaxLimit(), signal.getUnit(), newrxecus);

		setValues(signal.getValues());
		setValueTable(signal.getValueTable());

	}

}


