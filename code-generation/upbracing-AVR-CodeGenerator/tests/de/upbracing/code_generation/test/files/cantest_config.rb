
ecus = read_ecu_list("ecu-list-cantest.xml")
$config.ecus = ecus

$config.can = parse_dbc("cantest.dbc")

$config.selectEcu("Lenkrad-Display")



$config.can.getMessage("Bootloader_1").addAlias("RS232_FORWARD_DATA")
		
#Multiple Messages per MOB
$config.can.getMessage("Kupplung_Soll").rxMob = "Kupplung"
$config.can.getMessage("Kupplung_Calibration").rxMob = "Kupplung"

$config.can.getMessage("Kupplung_Soll").getSignal("Kupplung_Soll").putValue = 
	"if (!demo_mode) {\n" + 
	"	display_values[DI_Kupplung_Soll].value8 = value;\n" +
	"	display_values[DI_Kupplung_Soll].changed = 1;\n" +
	"}\n"
$config.can.getMessage("Kupplung_Soll").getSignal("Kupplung_Soll").afterRx = "clutch_calibration_mode = false;"
		
$config.can.getMessage("Kupplung_Calibration").getSignal("Kupplung_RAW").putValue = 
	"display_values[DI_Kupplung_Soll].value8 = value / 4;\n" +
	"display_values[DI_Kupplung_Soll].changed = 1;\n"
$config.can.getMessage("Kupplung_Calibration").getSignal("Kupplung_RAW").afterRx = "clutch_calibration_mode = true;"
		
$config.can.getMessage("Bootloader_SelectNode").rxHandler = "handle_bootloader_selectnode();"
$config.can.getMessage("Bootloader_1").rxHandler = "handle_rs232_forward_data();"
$config.can.getMessage("Bootloader_1").setMobDisabled(true);
$config.can.getMessage("ClutchGetPos").rxHandler = "handle_clutch_actuator();"
$config.can.getMessage("Lenkrad_main2display").rxHandler = "handle_main2display();"
		
$config.can.getMessage("Kupplung_Calibration_Control").setUsingGeneralTransmitter(true)
$config.can.getMessage("CockpitBrightness").setUsingGeneralTransmitter(true)

#Set Value Tables for signals
$config.can.getMessage("Kupplung_Calibration_Control").getSignal("KupplungKalibrationActive").setValueTable("boolean")
$config.can.getMessage("Radio").getSignal("Radio").setValueTable("boolean")
$config.can.getMessage("Launch").getSignal("Launch").setValueTable("boolean")

