# create temporary copies of the xml file, as we cannot load it from the JAR file
# (works for the DBC file because we load it with the JRuby File class)
require 'tempfile'
xmlfile = Tempfile.new(["ecu-list-cantest", ".xml"])
begin
xmlfile.write JRubyHelpers.readResource("de/upbracing/code_generation/test/files/ecu-list-cantest.xml")
xmlfile.close


ecus = read_ecu_list(xmlfile.path)
$config.ecus = ecus

$config.can = parse_dbc("classpath:/de/upbracing/code_generation/test/files/cantest.dbc")
NL = $config.can.NL

$config.selectEcu("Lenkrad-Display")



$config.can.getMessage("Bootloader_1").addAlias("RS232_FORWARD_DATA")
$config.can.addRx("RS232_FORWARD_DATA")
		
#Multiple Messages per MOB
$config.can.getMessage("Kupplung_Soll").rxMob = "Kupplung"
$config.can.getMessage("Kupplung_Calibration").rxMob = "Kupplung"

$config.can.getMessage("Kupplung_Soll").getSignal("Kupplung_Soll").putValue = 
	"if (!demo_mode) {" + NL +
	"	display_values[DI_Kupplung_Soll].value8 = value;" + NL +
	"	display_values[DI_Kupplung_Soll].changed = 1;" + NL +
	"}" + NL
$config.can.getMessage("Kupplung_Soll").getSignal("Kupplung_Soll").afterRx = "clutch_calibration_mode = false;"
		
$config.can.getMessage("Kupplung_Calibration").getSignal("Kupplung_RAW").putValue = 
	"display_values[DI_Kupplung_Soll].value8 = value / 4;" + NL +
	"display_values[DI_Kupplung_Soll].changed = 1;" + NL
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

#set periodic sending for some messages

$config.can.getMessage("Launch").period = "3ms";
$config.can.getMessage("Launch").beforeTask = "//Test comment before task message";
$config.can.getMessage("Launch").getSignal("Launch").beforeTask = "//Test comment before task signal";
$config.can.getMessage("Launch").getSignal("Launch").afterTask = "//Test comment after task signal";
$config.can.getMessage("Launch").afterTask = "//Test comment after task message";
$config.can.getMessage("Radio").period = 0.003;
$config.can.getMessage("Radio").beforeTask = "//Another test comment before task";
$config.can.getMessage("Radio").afterTask = "//Another test comment after task";
		
$config.can.getMessage("Kupplung_Calibration_Control").period = "0.5s";
$config.can.getMessage("Kupplung_Calibration_Control").taskAll = "//Test replacement of entire task handler for this message";
$config.can.getMessage("Kupplung_Calibration_Control").beforeTask = "#error This should not be visible!";
$config.can.getMessage("Kupplung_Calibration_Control").afterTask = "#error This should not be visible!";

$config.can.getMessage("CockpitBrightness").period = 1.0/3.0;
$config.can.getMessage("CockpitBrightness").getSignal("CockpitRPMBrightness").beforeReadValueTask = "//Test before read value";
$config.can.getMessage("CockpitBrightness").getSignal("CockpitRPMBrightness").readValueTask = "34";
$config.can.getMessage("CockpitBrightness").getSignal("CockpitRPMBrightness").afterReadValueTask = "//Test after read value";


# set expected factors to avoid warnings
can_config('signal(Temp_Wasser)', 'expected_factor', Rational(10, 1))
can_config('signal(Boardspannung)', 'expected_factor', Rational(10, 1))

  
ensure
  xmlfile.close
  xmlfile.unlink
end
