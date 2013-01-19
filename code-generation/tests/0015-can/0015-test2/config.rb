#DEPENDS ON:
ecu_list_file = "ecu-list-cantest.xml"
#DEPENDS ON:
dbc_file = "can_test.dbc"

puts Dir.pwd

ecus = read_ecu_list(ecu_list_file)
$config.ecus = ecus

$config.can = parse_dbc(dbc_file)

$config.selectEcu("TestBoard")

$config.can.addDeclarationsInCFile <<EOF
#include "main.h"
#include "can-helper-master.c.inc"
EOF
$config.can.addRxHandler(0, "can_helper_master_receive_relay();");

# message 1 is sent and received in its own MOb

# messages 2 uses the general transmitter MOb for tx
$config.can.getMessage("TestMsg2").usingGeneralTransmitter = true

# messages 3 and 4 share a MOb (for rx and tx respectively)
$config.can.getMessage("TestMsg3").rxMob = "TestMsg3and4Rx"
$config.can.getMessage("TestMsg4").rxMob = "TestMsg3and4Rx"
$config.can.getMessage("TestMsg3").txMob = "TestMsg3and4Tx"
$config.can.getMessage("TestMsg4").txMob = "TestMsg3and4Tx"

# so do 10, 11 and 12 (standard and extended IDs in shared MOb)
$config.can.getMessage("TestMsg10").rxMob = "TestMsg10to12Rx"
$config.can.getMessage("TestMsg11").rxMob = "TestMsg10to12Rx"
$config.can.getMessage("TestMsg12").rxMob = "TestMsg10to12Rx"
$config.can.getMessage("TestMsg10").txMob = "TestMsg10to12Tx"
$config.can.getMessage("TestMsg11").txMob = "TestMsg10to12Tx"
$config.can.getMessage("TestMsg12").txMob = "TestMsg10to12Tx"

# the other ones use the general transmitter for tx (we're out of MObs *g*)
not_general_transmitter = [1..4, 10..12]
#not_general_transmitter = [1..4]
not_general_transmitter.reduce(1..14) { |a,b| a.to_a-b.to_a }.each do |i|
  $config.can.getMessage("TestMsg#{i}").usingGeneralTransmitter = true
end

if false

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
$config.can.getMessage("InitTestphase").afterRx = "receiveFlags |= (1<<0);"
$config.can.getMessage("TestMessage1").afterRx = "receiveFlags |= (1<<1);"
$config.can.getMessage("TestMessage2A").afterRx = "receiveFlags |= (1<<2);"
$config.can.getMessage("TestMessage2B").afterRx = "receiveFlags |= (1<<3);"
$config.can.getMessage("TestMessage3A").afterRx = "receiveFlags |= (1<<4);"
$config.can.getMessage("TestMessage3B").afterRx = "receiveFlags |= (1<<5);"
$config.can.getMessage("TestMessage4C").afterRx = "receiveFlags |= (1<<6);"
$config.can.getMessage("TestMessage5C").afterRx = "receiveFlags |= (1<<7);"
$config.can.getMessage("TestMessage6A").afterRx = "TestMessage6A_onReceive();"
$config.can.getMessage("TestMessage6B").afterRx = "TestMessage6B_onReceive();"

end
