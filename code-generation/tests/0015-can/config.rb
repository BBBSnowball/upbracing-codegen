#DEPENDS ON:
ecu_list_file = "ecu-list-cantest.xml"
#DEPENDS ON:
dbc_file = "can_test.dbc"

puts Dir.pwd

ecus = read_ecu_list(ecu_list_file)
$config.ecus = ecus

$config.can = parse_dbc(dbc_file)

$config.selectEcu("TestBoard")

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

$config.rtos.addTask("Bla", SUSPENDED, 640)

# Message 3 shares a MOB
$config.can.getMessage("TestMessage3A").rxMob = "TestMessage3Shared"
$config.can.getMessage("TestMessage3B").rxMob = "TestMessage3Shared"

# Message 4 uses general transmitter
$config.can.getMessage("TestMessage4A").usingGeneralTransmitter = true
$config.can.getMessage("TestMessage4B").usingGeneralTransmitter = true

$config.can.getMessage("TestMessage1").period = "10ms"
# $config.can.getMessage("TestMessage3").period = "100ms"

# Set up receive handlers
$config.can.getMessage("TestMessage1").afterRx = "onReceive_TestMessage1();"
$config.can.getMessage("TestMessage2C").afterRx = "onReceive_TestMessage2C();"
$config.can.getMessage("TestMessage3A").afterRx = "onReceive_TestMessage3A();"
$config.can.getMessage("TestMessage3B").afterRx = "onReceive_TestMessage3B();"
$config.can.getMessage("TestMessage4A").afterRx = "onReceive_TestMessage4A();"
$config.can.getMessage("TestMessage4B").afterRx = "onReceive_TestMessage4B();"