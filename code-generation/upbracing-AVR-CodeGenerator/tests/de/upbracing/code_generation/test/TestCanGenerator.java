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
		config.getEcus().add(new ECUDefinition("Cockpit", "", "", "", "0x43", ""));
		
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

		message = new DBCMessage(0x10, "2147483664", true, "Kupplung_Soll", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Kupplung_Soll", "+", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Kupplung_Soll", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Kupplung_Soll", message);
		
		message = new DBCMessage(0x101, "2147483905", true, "Kupplung_Calibration", 2, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Kupplung_RAW", "+", "0", 7, 16, message, 1, 0, 0, 1023, "", Arrays.asList(ecu));
		message.getSignals().put("Kupplung_RAW", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Kupplung_Calibration", message);
		
		
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
		
		
		
		//Configuration:
		
		//Multiple Messages per MOB
		config.getCanConfig().getMessage("Kupplung_Soll").setRxMob("Kupplung");
		config.getCanConfig().getMessage("Kupplung_Calibration").setRxMob("Kupplung");

		//Code modification for receive handler
		config.getCanConfig().getMessage("Gear").setBeforeRx(
				"//This code for gear is included before rx\n"+
				"//Blablabla");
		config.getCanConfig().getMessage("Gear").setAfterRx(
				"//This code for gear is included after rx\n"+
				"//Hahaha. Next Line");
		config.getCanConfig().getMessage("Bootloader_SelectNode").setRxHandler(
				"handle_bootloader_selectnode();");
		
		//Message without send method
		//config.getCanConfig().getMessage("CockpitBrightness").setNoSendMessage(true);
		
		
		String expected = loadRessource("TestCanGenerator.testGenerate.result1.txt");
		String result = new CanTemplate().generate(config);
		assertEquals(expected, result);	
		
	}

}
