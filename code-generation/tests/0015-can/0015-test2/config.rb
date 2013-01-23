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
$config.can.getMessage("TestMsg02").usingGeneralTransmitter = true

# messages 3 and 4 share a MOb (for rx and tx respectively)
$config.can.getMessage("TestMsg03").rxMob = "TestMsg3and4Rx"
$config.can.getMessage("TestMsg04").rxMob = "TestMsg3and4Rx"
$config.can.getMessage("TestMsg03").txMob = "TestMsg3and4Tx"
$config.can.getMessage("TestMsg04").txMob = "TestMsg3and4Tx"

# so do 10  and 11 (standard and extended IDs in shared MOb)
#NOTE We use the same tx mob as for 3 and 4 because we are
#     short on MObs and it really doesn't matter for tx.
$config.can.getMessage("TestMsg10").rxMob = "TestMsg10to12Rx"
$config.can.getMessage("TestMsg11").rxMob = "TestMsg10to12Rx"
$config.can.getMessage("TestMsg10").txMob = "TestMsg3and4Tx"
$config.can.getMessage("TestMsg11").txMob = "TestMsg3and4Tx"

# those ones share a MOb because we don't have enough left
$config.can.getMessage("TestMsg15").rxMob = "TestMsgEndianness"
$config.can.getMessage("TestMsg16").rxMob = "TestMsgEndianness"
$config.can.getMessage("TestMsg17").rxMob = "TestMsgEndianness"

# the other ones use the general transmitter for tx (we're out of MObs *g*)
not_general_transmitter = [1..4, 10..11]
#not_general_transmitter = [1..4]
not_general_transmitter.reduce(1..17) { |a,b| [*a]-[*b] }.each do |i|
  $config.can.getMessage(sprintf("TestMsg%02d", i)).usingGeneralTransmitter = true
end
