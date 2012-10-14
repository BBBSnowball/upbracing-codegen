package de.upbracing.code_generation.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

import de.upbracing.code_generation.config.DBCConfig;
import de.upbracing.code_generation.config.DBCSignalConfig;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.generators.CanGenerator;
import de.upbracing.dbc.DBC;
import de.upbracing.dbc.DBCEcu;
import de.upbracing.dbc.DBCMessage;
import de.upbracing.dbc.DBCSignal;
import de.upbracing.dbc.DBCValueTable;
import de.upbracing.eculist.ECUDefinition;

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
		valTable.put("1", "true");
		valTable.put("0", "false");
		dbc.getValueTables().put("boolean", valTable);

		valTable = new DBCValueTable();
		valTable.put("3", "BOOTLOADER_ACTIVE");
		valTable.put("2", "START_FLIPPER");
		valTable.put("1", "START_TETRIS");
		valTable.put("0", "MAIN_ACK");
		dbc.getValueTables().put("main2display", valTable);
		
		valTable = new DBCValueTable();
		valTable.put("68", "LenkradCANtoRS232");
		valTable.put("67", "LenkradDisplay");
		valTable.put("66", "LenkradMain");
		valTable.put("71", "Sensorboard");
		dbc.getValueTables().put("BootloaderNode", valTable);
		

		
		
		
		//Ecu
		DBCEcu ecu = new DBCEcu("Display"); //Cockpit?
		ecu.setComment("Test Comment");
		ecu.setRxMsgs(new ArrayList<DBCMessage>());
		ecu.setRxSignals(new ArrayList<DBCSignal>());
		ecu.setTxMsgs(new ArrayList<DBCMessage>());
		dbc.getEcus().put("Display", ecu);
		dbc.getEcuNames().add("Display");
		config.getEcus().add(new ECUDefinition("Display", "", "", "", "0x43", ""));
		
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
		
		message = new DBCMessage(0x1, "1", false, "Bootloader_1", 8, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Bootloader_1", message);
		
		message = new DBCMessage(0x5ff, "1535", false, "ClutchGetPos", 8, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("CANOPEN_SubIndex", "-", "1", 24, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("CANOPEN_SubIndex", signal);
		message.getSignalOrder().add(signal);
		//ecu.getRxSignals().add(signal);
		signal = new DBCSignal("Clutch_IstPosition", "-", "1", 32, 32, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Clutch_IstPosition", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		signal = new DBCSignal("CANOPEN_Index", "+", "1", 8, 16, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("CANOPEN_Index", signal);
		message.getSignalOrder().add(signal);
		//ecu.getRxSignals().add(signal);
		signal = new DBCSignal("CANOPEN_1", "+", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("CANOPEN_1", signal);
		message.getSignalOrder().add(signal);
		//ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("ClutchGetPos", message);
		
		message = new DBCMessage(0x10, "2147483664", true, "Kupplung_Soll", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Kupplung_Soll", "+", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Kupplung_Soll", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Kupplung_Soll", message);
		
		message = new DBCMessage(0x71, "2147483761", true, "Gear", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Gang", "+", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Gang", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Gear", message);

		message = new DBCMessage(0x80, "2147483776", true, "Sensoren", 6, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Temp_Oel", "+", "1", 16, 8, message, 1, 0, 0, 0, "deg C", Arrays.asList(ecu));
		message.getSignals().put("Temp_Oel", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		signal = new DBCSignal("Druck_Oel", "+", "1", 32, 16, message, 1, 0, 0, 0, "kPa", Arrays.asList(ecu));
		message.getSignals().put("Druck_Oel", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Sensoren", message);

		message = new DBCMessage(0x81, "2147483777", true, "Sensoren_2", 2, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Druck_Kraftstoff", "+", "1", 0, 16, message, 1, 0, 0, 0, "kPa", Arrays.asList(ecu));
		message.getSignals().put("Druck_Kraftstoff", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Sensoren_2", message);
		
		message = new DBCMessage(0x88, "2147483784", true, "OpenSquirt_Engine", 8, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Drehzahl", "+", "0", 7, 16, message, 1, 0, 0, 0, "rpm", Arrays.asList(ecu));
		message.getSignals().put("Drehzahl", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		signal = new DBCSignal("Druck_Ansaug", "-", "0", 23, 16, message, 10, 0, 0, 0, "kPa", Arrays.asList(ecu));
		message.getSignals().put("Druck_Ansaug", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		signal = new DBCSignal("Lambda", "-", "0", 39, 16, message, 147, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Lambda", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		signal = new DBCSignal("ThrottlePosition", "-", "0", 55, 16, message, 10, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("ThrottlePosition", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("OpenSquirt_Engine", message);
		
		message = new DBCMessage(0x101, "2147483905", true, "Kupplung_Calibration", 2, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Kupplung_RAW", "+", "0", 7, 16, message, 1, 0, 0, 1023, "", Arrays.asList(ecu));
		message.getSignals().put("Kupplung_RAW", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Kupplung_Calibration", message);
		
		message = new DBCMessage(0x108, "2147483912", true, "OpenSquirt_Sensoren1", 6, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Temp_Wasser", "-", "0", 7, 16, message, 10, 0, 0, 0, "deg C", Arrays.asList(ecu));
		message.getSignals().put("Temp_Wasser", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		signal = new DBCSignal("Temp_Ansaug", "-", "0", 23, 16, message, 10, 0, 0, 0, "deg C", Arrays.asList(ecu));
		message.getSignals().put("Temp_Ansaug", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		signal = new DBCSignal("Boardspannung", "-", "0", 39, 16, message, 10, 0, 0, 0, "V", Arrays.asList(ecu));
		message.getSignals().put("Boardspannung", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("OpenSquirt_Sensoren1", message);
		
		message = new DBCMessage(0x110, "2147483920", true, "Geschwindigkeit", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Geschwindigkeit", "+", "1", 0, 8, message, 1, 0, 0, 0, "km/h", Arrays.asList(ecu));
		message.getSignals().put("Geschwindigkeit", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Geschwindigkeit", message);
		
		message = new DBCMessage(0x4201, "2147500545", true, "Lenkrad_main2display", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Lenkrad_main2display", "-", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Lenkrad_main2display", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Lenkrad_main2display", message);
		
		//TX Messages
		message = new DBCMessage(0x60, "2147483744", true, "Launch", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Launch", "+", "1", 0, 8, message, 1, 0, 0, 0, "bool", Arrays.asList(ecu));
		message.getSignals().put("Launch", signal);
		message.getSignalOrder().add(signal);
		ecu.getTxMsgs().add(message);
		dbc.getMessages().put("Launch", message);
		
		message = new DBCMessage(0x90, "2147483792", true, "Radio", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Radio", "+", "1", 0, 8, message, 1, 0, 0, 0, "bool", Arrays.asList(ecu));
		message.getSignals().put("Radio", signal);
		message.getSignalOrder().add(signal);
		ecu.getTxMsgs().add(message);
		dbc.getMessages().put("Radio", message);
		
		message = new DBCMessage(0x250, "2147484240", true, "Kupplung_Calibration_Control", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("KupplungKalibrationActive", "+", "1", 0, 8, message, 1, 0, 0, 1, "bool", Arrays.asList(ecu));
		message.getSignals().put("KupplungKalibrationActive", signal);
		message.getSignalOrder().add(signal);
		ecu.getTxMsgs().add(message);
		dbc.getMessages().put("Kupplung_Calibration_Control", message);
		
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
		config.selectEcu("Display");
		
		
		
		//Configuration:
		DBCConfig canconfig = config.getCanConfig();
		
		//Alias
		canconfig.getMessage("Bootloader_1").addAlias("RS232_FORWARD_DATA");
		
		//Multiple Messages per MOB
		canconfig.getMessage("Kupplung_Soll").setRxMob("Kupplung");
		canconfig.getMessage("Kupplung_Calibration").setRxMob("Kupplung");

		//Code modification for receive handler
/*		canconfig.getMessage("Gear").setBeforeRx(
				"//This code for gear is included before rx\n"+
				"//Blablabla");
		canconfig.getMessage("Gear").setAfterRx(
				"//This code for gear is included after rx\n"+
				"//Hahaha. Next Line");
		*/
		DBCSignalConfig signalconfig = (DBCSignalConfig) canconfig.getMessage("Kupplung_Soll").getSignals().get("Kupplung_Soll");
		signalconfig.setPutValue(
				"if (!demo_mode) {\n" + 
				"	display_values[DI_Kupplung_Soll].value8 = value;\n" +
				"	display_values[DI_Kupplung_Soll].changed = 1;\n" +
				"}\n");
		signalconfig.setAfterRx("clutch_calibration_mode = false;");	
		
		signalconfig = (DBCSignalConfig) canconfig.getMessage("Kupplung_Calibration").getSignals().get("Kupplung_RAW");
		signalconfig.setPutValue(
				"display_values[DI_Kupplung_Soll].value8 = value / 4;\n" +
				"display_values[DI_Kupplung_Soll].changed = 1;\n");
		signalconfig.setAfterRx("clutch_calibration_mode = true;");
		
		canconfig.getMessage("Bootloader_SelectNode").setRxHandler(
				"handle_bootloader_selectnode();");
		canconfig.getMessage("Bootloader_1").setRxHandler(
				"handle_rs232_forward_data();");
		canconfig.getMessage("Bootloader_1").setMobDisabled(true);
		canconfig.getMessage("ClutchGetPos").setRxHandler(
				"handle_clutch_actuator();");
		canconfig.getMessage("Lenkrad_main2display").setRxHandler(
				"handle_main2display();");
		
		canconfig.getMessage("Kupplung_Calibration_Control").setUsingGeneralTransmitter(true);
		canconfig.getMessage("CockpitBrightness").setUsingGeneralTransmitter(true);

		//Set Value Tables for signals
		canconfig.getMessage("Kupplung_Calibration_Control").getSignals().get("KupplungKalibrationActive").setValueTable("boolean");
		canconfig.getMessage("Radio").getSignals().get("Radio").setValueTable("boolean");
		canconfig.getMessage("Launch").getSignals().get("Launch").setValueTable("boolean");

		
		//Message without send method
		//config.getCanConfig().getMessage("CockpitBrightness").setNoSendMessage(true);

		GeneratorTester gen = new GeneratorTester(new CanGenerator(), config);
		gen.testTemplates("expected_results/can");
		
	}

}
