package de.upbracing.code_generation.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.script.ScriptException;
import org.junit.Test;

import de.upbracing.code_generation.Helpers;
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

	@Test
	public void testGenerateFromDBC() throws FileNotFoundException, ScriptException {

		MCUConfiguration config = Helpers.loadConfig("tests/de/upbracing/code_generation/test/files/cantest_config.rb");
		
		GeneratorTester gen = new GeneratorTester(new CanGenerator(), config);
		gen.testTemplates("expected_results/can");
	}
	
	@Test
	public void testGenerate() {
		
		MCUConfiguration config = new MCUConfiguration();
		config.setEcus(new ArrayList<ECUDefinition>());
		
		DBC dbc = new DBC("");
		dbc.setEcus(new HashMap<String, DBCEcu>());
		dbc.setEcuNames(new ArrayList<String>());
		dbc.setValueTables(new HashMap<String, DBCValueTable>());
		dbc.setMessages(new HashMap<String, DBCMessage>());
		
		//Value Tables
		DBCValueTable valTable = new DBCValueTable();
		valTable.put("3", "BOOTLOADER_ACTIVE");
		valTable.put("2", "START_FLIPPER");
		valTable.put("1", "START_TETRIS");
		valTable.put("0", "MAIN_ACK");
		dbc.getValueTables().put("main2display", valTable);
		
		valTable = new DBCValueTable();
		valTable.put("14194", "DUNKER_VPOS_ActualPosition_cnt");
		valTable.put("14224", "DUNKER_MovA");
		dbc.getValueTables().put("Dunkermotor_CANOPEN_Index", valTable);
		
		valTable = new DBCValueTable();
		valTable.put("68", "LenkradCANtoRS232");
		valTable.put("67", "LenkradDisplay");
		valTable.put("66", "LenkradMain");
		valTable.put("71", "Sensorboard");
		dbc.getValueTables().put("BootloaderNode", valTable);
		
		valTable = new DBCValueTable();
		valTable.put("1", "true");
		valTable.put("0", "false");
		dbc.getValueTables().put("boolean", valTable);		
		
		//Ecu
		DBCEcu ecu = new DBCEcu("Lenkrad-Display");
		ecu.setComment("Test Comment");
		ecu.setRxMsgs(new ArrayList<DBCMessage>());
		ecu.setRxSignals(new ArrayList<DBCSignal>());
		ecu.setTxMsgs(new ArrayList<DBCMessage>());
		dbc.getEcus().put("LenkradDisplay", ecu);
		dbc.getEcuNames().add("LenkradDisplay");
		config.getEcus().add(new ECUDefinition("Lenkrad-Display", "", "", "", "0x43", "LenkradDisplay"));
		
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
		dbc.getMessages().put("0", message);
		
		message = new DBCMessage(0x1, "1", false, "Bootloader_1", 8, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Bootloader_1", message);
		dbc.getMessages().put("1", message);

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
		dbc.getMessages().put("1535", message);

		message = new DBCMessage(0x10, "2147483664", true, "Kupplung_Soll", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Kupplung_Soll", "+", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Kupplung_Soll", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Kupplung_Soll", message);
		dbc.getMessages().put("2147483664", message);

		message = new DBCMessage(0x71, "2147483761", true, "Gear", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Gang", "+", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Gang", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Gear", message);
		dbc.getMessages().put("2147483761", message);

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
		dbc.getMessages().put("2147483776", message);

		message = new DBCMessage(0x81, "2147483777", true, "Sensoren_2", 2, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Druck_Kraftstoff", "+", "1", 0, 16, message, 1, 0, 0, 0, "kPa", Arrays.asList(ecu));
		message.getSignals().put("Druck_Kraftstoff", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Sensoren_2", message);
		dbc.getMessages().put("2147483777", message);

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
		dbc.getMessages().put("2147483784", message);

		message = new DBCMessage(0x101, "2147483905", true, "Kupplung_Calibration", 2, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Kupplung_RAW", "+", "0", 7, 16, message, 1, 0, 0, 1023, "", Arrays.asList(ecu));
		message.getSignals().put("Kupplung_RAW", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Kupplung_Calibration", message);
		dbc.getMessages().put("2147483905", message);

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
		dbc.getMessages().put("2147483912", message);

		message = new DBCMessage(0x110, "2147483920", true, "Geschwindigkeit", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Geschwindigkeit", "+", "1", 0, 8, message, 1, 0, 0, 0, "km/h", Arrays.asList(ecu));
		message.getSignals().put("Geschwindigkeit", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Geschwindigkeit", message);
		dbc.getMessages().put("2147483920", message);

		message = new DBCMessage(0x4201, "2147500545", true, "Lenkrad_main2display", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Lenkrad_main2display", "-", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("Lenkrad_main2display", signal);
		message.getSignalOrder().add(signal);
		ecu.getRxSignals().add(signal);
		ecu.getRxMsgs().add(message);
		dbc.getMessages().put("Lenkrad_main2display", message);
		dbc.getMessages().put("2147500545", message);

		//TX Messages
		message = new DBCMessage(0x250, "2147484240", true, "Kupplung_Calibration_Control", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("KupplungKalibrationActive", "+", "1", 0, 8, message, 1, 0, 0, 1, "bool", Arrays.asList(ecu));
		message.getSignals().put("KupplungKalibrationActive", signal);
		message.getSignalOrder().add(signal);
		ecu.getTxMsgs().add(message);
		dbc.getMessages().put("Kupplung_Calibration_Control", message);
		dbc.getMessages().put("2147484240", message);
		
		message = new DBCMessage(0x60, "2147483744", true, "Launch", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Launch", "+", "1", 0, 8, message, 1, 0, 0, 0, "bool", Arrays.asList(ecu));
		message.getSignals().put("Launch", signal);
		message.getSignalOrder().add(signal);
		ecu.getTxMsgs().add(message);
		dbc.getMessages().put("Launch", message);
		dbc.getMessages().put("2147483744", message);

		message = new DBCMessage(0x90, "2147483792", true, "Radio", 1, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("Radio", "+", "1", 0, 8, message, 1, 0, 0, 0, "bool", Arrays.asList(ecu));
		message.getSignals().put("Radio", signal);
		message.getSignalOrder().add(signal);
		ecu.getTxMsgs().add(message);
		dbc.getMessages().put("Radio", message);
		dbc.getMessages().put("2147483792", message);

		message = new DBCMessage(0x4242, "2147500610", true, "CockpitBrightness", 4, Arrays.asList(ecu));
		message.setSignals(new HashMap<String, DBCSignal>());
		message.setSignalOrder(new ArrayList<DBCSignal>());
		signal = new DBCSignal("CockpitRPMBrightness", "+", "1", 0, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("CockpitRPMBrightness", signal);
		message.getSignalOrder().add(signal);
		signal = new DBCSignal("CockpitGangBrightness", "+", "1", 8, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("CockpitGangBrightness", signal);
		message.getSignalOrder().add(signal);
		signal = new DBCSignal("CockpitShiftLightPeriod", "+", "1", 16, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("CockpitShiftLightPeriod", signal);
		message.getSignalOrder().add(signal);
		signal = new DBCSignal("CockpitShiftLightAlwaysFlash", "+", "1", 24, 8, message, 1, 0, 0, 0, "", Arrays.asList(ecu));
		message.getSignals().put("CockpitShiftLightAlwaysFlash", signal);
		message.getSignalOrder().add(signal);
		ecu.getTxMsgs().add(message);
		dbc.getMessages().put("CockpitBrightness", message);
		dbc.getMessages().put("2147500610", message);

		config.setCan(dbc);
		config.selectEcu("Lenkrad-Display");
		
		
		//Configuration:
		DBCConfig canconfig = config.getCan();
		
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
		canconfig.getMessage("Kupplung_Calibration_Control").getSignal("KupplungKalibrationActive").setValueTable("boolean");
		canconfig.getMessage("Radio").getSignal("Radio").setValueTable("boolean");
		canconfig.getMessage("Launch").getSignal("Launch").setValueTable("boolean");

		
		//Message without send method
		//config.getCanConfig().getMessage("CockpitBrightness").setNoSendMessage(true);

		//set periodic sending for some messages

		//Group with same period and code replacement
		canconfig.getMessage("Launch").setPeriod("3ms");
		canconfig.getMessage("Launch").setBeforeTask("//Test comment before task message");
		canconfig.getMessage("Launch").getSignal("Launch").setBeforeTask("//Test comment before task signal");
		canconfig.getMessage("Launch").getSignal("Launch").setAfterTask("//Test comment after task signal");
		canconfig.getMessage("Launch").setAfterTask("//Test comment after task message");
		canconfig.getMessage("Radio").setPeriod(0.003);
		canconfig.getMessage("Radio").setBeforeTask("//Another test comment before task");
		canconfig.getMessage("Radio").setAfterTask("//Another test comment after task");
		
		canconfig.getMessage("Kupplung_Calibration_Control").setPeriod("0.5s");
		canconfig.getMessage("Kupplung_Calibration_Control").setTaskAll("//Test replacement of entire task handler for this message");
		canconfig.getMessage("Kupplung_Calibration_Control").setBeforeTask("#error This should not be visible!");
		canconfig.getMessage("Kupplung_Calibration_Control").setAfterTask("#error This should not be visible!");

		canconfig.getMessage("CockpitBrightness").setPeriod(1.0/3.0);
		canconfig.getMessage("CockpitBrightness").getSignal("CockpitRPMBrightness").setBeforeReadValueTask("//Test before read value");
		canconfig.getMessage("CockpitBrightness").getSignal("CockpitRPMBrightness").setReadValueTask("34");
		canconfig.getMessage("CockpitBrightness").getSignal("CockpitRPMBrightness").setAfterReadValueTask("//Test after read value");

		GeneratorTester gen = new GeneratorTester(new CanGenerator(), config);
		gen.testTemplates("expected_results/can");
		
	}

}
