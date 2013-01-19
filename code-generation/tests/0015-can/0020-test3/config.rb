#DEPENDS ON:
ecu_list_file = "ecu-list-cantest.xml"
#DEPENDS ON:
dbc_file = "can_test.dbc"

puts Dir.pwd

ecus = read_ecu_list(ecu_list_file)
$config.ecus = ecus

$config.can = parse_dbc(dbc_file)

$config.selectEcu("TestBoard")

# use the first user MOb to handle relayed messages
$config.can.addDeclarationsInCFile <<EOF
#include "main.h"
#include "can-helper-master.c.inc"
EOF
$config.can.addRxHandler(0, "can_helper_master_receive_relay();");


# For our hooks, we need a way to report that they have
# been run. We use a global counter that we store in a
# hook variable whenever the hook is executed.
$config.can.addDeclarations <<EOF
#include <stdint.h>
extern uint16_t hook_counter;
EOF
$config.can.addDeclarationsInCFile <<EOF
uint16_t hook_counter = 0;
EOF

$hook_vars = []
def create_hook(name)
  hook_var = "hook_var_#{name}"
  $config.can.addDeclarations         "extern uint16_t #{hook_var};"
  $config.can.addDeclarationsInCFile  "uint16_t #{hook_var} = 0;"
  $hook_vars << name
  return "#{hook_var} = ++hook_counter;"
end


# add a few hooks and stuff
$config.can.getMessage("TestMsg02").rx_handler = create_hook "TestMsg02_rx_handler"
$config.can.getMessage("TestMsg02").getSignal("Test1").no_global_var = true
$config.can.getMessage("TestMsg03").before_rx = create_hook "TestMsg03_before_rx"
$config.can.getMessage("TestMsg03").after_rx  = create_hook "TestMsg03_after_rx"
$config.can.getMessage("TestMsg03").getSignal("Test2").global_var_name = "TestMsg03_Test3"
$config.can.getMessage("TestMsg03").getSignal("Test1").before_rx = create_hook "TestMsg03_Test1_before_rx"
$config.can.getMessage("TestMsg03").getSignal("Test2").before_rx = create_hook "TestMsg03_Test2_before_rx"
$config.can.getMessage("TestMsg03").getSignal("Test1").after_rx  = create_hook "TestMsg03_Test1_after_rx"
$config.can.getMessage("TestMsg03").getSignal("Test2").after_rx  = create_hook "TestMsg03_Test2_after_rx"
$config.can.getMessage("TestMsg03").getSignal("Test1").imm_before_rx = create_hook "TestMsg03_Test1_imm_before_rx"
$config.can.getMessage("TestMsg03").getSignal("Test2").imm_before_rx = create_hook "TestMsg03_Test2_imm_before_rx"
$config.can.getMessage("TestMsg03").getSignal("Test1").imm_after_rx  = create_hook "TestMsg03_Test1_imm_after_rx"
$config.can.getMessage("TestMsg03").getSignal("Test2").imm_after_rx  = create_hook "TestMsg03_Test2_imm_after_rx"

$config.can.addDeclarations "extern uint8_t TestMsg03_Test2_put_value;"
$config.can.addDeclarationsInCFile "uint8_t TestMsg03_Test2_put_value = 0;"
$config.can.getMessage("TestMsg03").getSignal("Test2").put_value =
  create_hook("TestMsg03_Test2_put_value") + "\nTestMsg03_Test2_put_value = value;"

$config.can.getMessage("TestMsg07").before_tx = create_hook "TestMsg07_before_tx"
$config.can.getMessage("TestMsg07").after_tx  = create_hook "TestMsg07_after_tx"
$config.can.getMessage("TestMsg07").getSignal("Test1").before_tx = create_hook "TestMsg07_Test1_before_tx"
$config.can.getMessage("TestMsg07").getSignal("Test2").before_tx = create_hook "TestMsg07_Test2_before_tx"
$config.can.getMessage("TestMsg07").getSignal("Test1").after_tx  = create_hook "TestMsg07_Test1_after_tx"
$config.can.getMessage("TestMsg07").getSignal("Test2").after_tx  = create_hook "TestMsg07_Test2_after_tx"
$config.can.getMessage("TestMsg07").getSignal("Test1").imm_before_tx = create_hook "TestMsg07_Test1_imm_before_tx"
$config.can.getMessage("TestMsg07").getSignal("Test2").imm_before_tx = create_hook "TestMsg07_Test2_imm_before_tx"
$config.can.getMessage("TestMsg07").getSignal("Test1").imm_after_tx  = create_hook "TestMsg07_Test1_imm_after_tx"
$config.can.getMessage("TestMsg07").getSignal("Test2").imm_after_tx  = create_hook "TestMsg07_Test2_imm_after_tx"

$config.can.getMessage("TestMsg07").getSignal("Test1").get_value =
  create_hook("TestMsg07_Test1_get_value") + "\nvalue = 0x4255;"
$config.can.getMessage("TestMsg07").getSignal("Test2").param = "test3"

$config.can.getMessage("TestMsg04").tx_handler = <<EOF
#{create_hook "TestMsg04_some_tx_handler"}

can_mob_init_transmit2(MOB, CAN_TestMsg04, CAN_TestMsg04_IsExtended);

// The message has a length of 2, but we use 4 here - because we can ;-)
CANCDMOB = (CANCDMOB&0x30) | ((4&0xf)<<DLC0);

CANMSG = 0x12;
CANMSG = 0x34;
CANMSG = 0x56;
CANMSG = 0x78;

EOF

$config.can.getMessage("TestMsg09").tx_handler_all = <<EOF
#{create_hook "TestMsg09_some_tx_handler"}

// select MOB
CANPAGE = (MOB<<4);

// wait for an ongoing transmission to finish
can_mob_wait_for_transmission_of_current_mob();

// reset transmission status
CANSTMOB = 0;

can_mob_init_transmit2(MOB_GENERAL_MESSAGE_TRANSMITTER, CAN_TestMsg09, CAN_TestMsg09_IsExtended);

// The message has a length of 1, but we use 3 here - because we can ;-)
CANCDMOB = (CANCDMOB&0x30) | ((3&0xf)<<DLC0);

CANMSG = 0x34;
CANMSG = 0x56;
CANMSG = 0x78;

if (wait)
  can_mob_transmit_wait(MOB);
else
  can_mob_transmit_nowait(MOB);

EOF
  
$config.can.getMessage("TestMsg06").tx_handler_data = <<EOF
#{create_hook "TestMsg06_some_tx_handler"}

// The message has a length of 7 and we cannot change that.
// (Well, we could... but it has been set to 7.)

CANMSG = 0x56;
CANMSG = 0x78;
CANMSG = 0x12;
CANMSG = 0x34;
CANMSG = 0x12;
CANMSG = 0x34;
CANMSG = 0x42;
  
EOF

# Although we replace all the transmission code, the before and after
# hooks will be executed. However, the imm_before/imm_after hooks will
# be ignored.
$config.can.getMessage("TestMsg04").before_tx = create_hook "TestMsg04_before_tx"
$config.can.getMessage("TestMsg04").getSignal("Test1").before_tx = create_hook "TestMsg04_Test1_before_tx"
$config.can.getMessage("TestMsg09").before_tx = create_hook "TestMsg09_before_tx"
$config.can.getMessage("TestMsg09").getSignal("Test1").before_tx = create_hook "TestMsg09_Test1_before_tx"
$config.can.getMessage("TestMsg06").before_tx = create_hook "TestMsg06_before_tx"
$config.can.getMessage("TestMsg06").getSignal("Test1").before_tx = create_hook "TestMsg06_Test1_before_tx"
$config.can.getMessage("TestMsg04").after_tx = create_hook "TestMsg04_after_tx"
$config.can.getMessage("TestMsg04").getSignal("Test1").after_tx = create_hook "TestMsg04_Test1_after_tx"
$config.can.getMessage("TestMsg09").after_tx = create_hook "TestMsg09_after_tx"
$config.can.getMessage("TestMsg09").getSignal("Test1").after_tx = create_hook "TestMsg09_Test1_after_tx"
$config.can.getMessage("TestMsg06").after_tx = create_hook "TestMsg06_after_tx"
$config.can.getMessage("TestMsg06").getSignal("Test1").after_tx = create_hook "TestMsg06_Test1_after_tx"


# generate a function that prints the hook counters
$config.can.addDeclarations "void printHookCounters(void);"
printHookCounters = "\n\nvoid printHookCounters(void) {"
$hook_vars.sort.each do |var|
  printHookCounters += <<EOF.gsub(/^    /, "\t")

    usart_send_str("- #{var}: ");
    usart_send_number(hook_var_#{var}, 10, 0);
    usart_send_str("\\r\\n");
EOF
end
printHookCounters += "}"
$config.can.addDeclarationsInCFile printHookCounters


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
