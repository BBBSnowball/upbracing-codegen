package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

import de.upbracing.code_generation.CanTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.dbc.DBC;
import de.upbracing.dbc.DBCEcu;
import de.upbracing.dbc.DBCMessage;
import de.upbracing.dbc.DBCSignal;
import de.upbracing.dbc.DBCValueTable;
import de.upbracing.eculist.ECUDefinition;

import static de.upbracing.code_generation.test.TestHelpers.*;

public class TestCanGenerator {

	/*@Test
	public void testGenerateFromDBC() throws FileNotFoundException, ScriptException {

		MCUConfiguration config = Helpers.loadConfig("config.rb");
		
		config.selectEcu("Cockpit");
		
		String expected = loadRessource("TestCanGenerator.testGenerate.result1.txt");
		String result = new CanTemplate().generate(config);
		assertEquals(expected, result);	
		
	}*/
	
	@Test
	public void testGenerate() {

		MCUConfiguration config = new MCUConfiguration();
		config.setEcus(new ArrayList<ECUDefinition>());
		
		DBC dbc = new DBC("???"); //TODO What DBC version?
		dbc.setEcus(new HashMap<String, DBCEcu>());
		dbc.setEcuNames(new ArrayList<String>());
		dbc.setValueTables(new HashMap<String, DBCValueTable>());
		dbc.setMessages(new HashMap<String, DBCMessage>());
		
		//Value Tables
		DBCValueTable valTable = new DBCValueTable();
		
		valTable.put("BOOTLOADER_ACTIVE", "3");
		valTable.put("START_FLIPPER", "2");
		valTable.put("START_TETRIS", "1");
		valTable.put("MAIN_ACK", "0");
		dbc.getValueTables().put("main2display", valTable);
				
		//Ecu
		DBCEcu ecu = new DBCEcu("Cockpit");
		ecu.setComment("Test Comment");
		ecu.setRxMsgs(new ArrayList<DBCMessage>());
		ecu.setRxSignals(new ArrayList<DBCSignal>());
		ecu.setTxMsgs(new ArrayList<DBCMessage>());
		dbc.getEcus().put("Cockpit", ecu);
		dbc.getEcuNames().add("Cockpit");
		config.getEcus().add(new ECUDefinition("Cockpit", "", "", "", "", "0x43"));
		
		//RX Messages
		DBCMessage message = new DBCMessage(0x0, "0", false, "Bootloader_SelectNode", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		//TODO Fix the parameters. Some are just made up
		DBCSignal signal = new DBCSignal("Bootloader_SelectNode", "-", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Bootloader_SelectNode", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Bootloader_SelectNode", message);
		
		message = new DBCMessage(0x71, "2147483761", true, "Gear", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Gang", "+", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Gang", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Gear", message);

		
		//TX Messages
		message = new DBCMessage(0x4242, "2147500610", true, "CockpitBrightness", 3, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("CockpitRPMBrightness", "+", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("CockpitRPMBrightness", signal);
		message.getSignalOrder().add(signal);
		signal = new DBCSignal("CockpitGangBrightness", "+", "1", 8, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("CockpitGangBrightness", signal);
		message.getSignalOrder().add(signal);
		ecu.getTxMsgs().add(message);
		dbc.getMessages().put("CockpitBrightness", message);

		config.setCan(dbc);
		config.selectEcu("Cockpit");
		
		String expected = loadRessource("TestCanGenerator.testGenerate.result1.txt");
		String result = new CanTemplate().generate(config);
		assertEquals(expected, result);	
		
	}

}
