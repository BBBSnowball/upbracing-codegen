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

$config.can.addDeclarations("#include \"../main.h\"")

# Message 3A/B shares a MOB
$config.can.getMessage("TestMessage3A").rxMob = "TestMessage3Shared"
$config.can.getMessage("TestMessage3B").rxMob = "TestMessage3Shared"
# The rest uses shared MOBs to save MOBs
$config.can.getMessage("TestMessage4C").rxMob = "TestMessage4Shared"
$config.can.getMessage("TestMessage4R").rxMob = "TestMessage4Shared"
$config.can.getMessage("TestMessage6A").rxMob = "TestMessage6Shared"
$config.can.getMessage("TestMessage6B").rxMob = "TestMessage6Shared"

# Message 2A/B uses general transmitter as a test
$config.can.getMessage("TestMessage2A").usingGeneralTransmitter = true
$config.can.getMessage("TestMessage2B").usingGeneralTransmitter = true
# The rest uses general transmitter to save MOBs
$config.can.getMessage("TestMessage3A").usingGeneralTransmitter = true
$config.can.getMessage("TestMessage3B").usingGeneralTransmitter = true
$config.can.getMessage("TestMessage4C").usingGeneralTransmitter = true
$config.can.getMessage("TestMessage4R").usingGeneralTransmitter = true
$config.can.getMessage("TestMessage5A").usingGeneralTransmitter = true
$config.can.getMessage("TestMessage5D").usingGeneralTransmitter = true
$config.can.getMessage("TestMessage6A").usingGeneralTransmitter = true
$config.can.getMessage("TestMessage6B").usingGeneralTransmitter = true

# Messages 5A/D are received manually, and 5B/C are send manually
$config.can.getMessage("TestMessage5A").rxHandler = "TestMessage5A_receiveHandler();"
$config.can.getMessage("TestMessage5B").noSendMessage = true # don't generate send methods
$config.can.getMessage("TestMessage5B").usingGeneralTransmitter = true # don't assign a txMob
$config.can.getMessage("TestMessage5C").noSendMessage = true
$config.can.getMessage("TestMessage5C").usingGeneralTransmitter = true
$config.can.getMessage("TestMessage5D").rxHandler = "TestMessage5D_receiveHandler();"

# Disable dummy message
$config.can.getMessage("DummyMessage").mobDisabled = true

# The periodic message is send every second for 10 times with an incrementing counter
# The counter is incremented at reception
$config.can.getMessage("TestMessage6A").period = "1000ms"
$config.can.getMessage("TestMessage6A").beforeTask = "if(counter < 10) {"
$config.can.getMessage("TestMessage6A").afterTask = "}"
$config.can.getMessage("TestMessage6A").getSignal("TestSignal6A").readValueTask = "getTestSignal6A()+counter"


# Set up receive handlers
$config.can.getMessage("TestMessage1").afterRx = "TestMessage1_onReceive();"
$config.can.getMessage("TestMessage2A").afterRx = "TestMessage2A_onReceive();"
$config.can.getMessage("TestMessage2B").afterRx = "TestMessage2B_onReceive();"
$config.can.getMessage("TestMessage3A").afterRx = "TestMessage3A_onReceive();"
$config.can.getMessage("TestMessage3B").afterRx = "TestMessage3B_onReceive();"
$config.can.getMessage("TestMessage4C").afterRx = "TestMessage4C_onReceive();"
$config.can.getMessage("TestMessage5C").afterRx = "TestMessage5C_onReceive();"
$config.can.getMessage("TestMessage6A").afterRx = "TestMessage6A_onReceive();"
$config.can.getMessage("TestMessage6B").afterRx = "TestMessage6B_onReceive();"

