# erase processor to make sure we get no further
# stuff on the serial line
$helper.erase_processor

# open serial line
# We do that before flashing the processor to get
# all the output. 
$helper.first_serial.ensure_baudrate 9600

# we need some delay between invocations of avrdude
sleep 3

# write the program on the processor
$helper.flash_processor

# check output on serial console
$helper.first_serial.expect_string "Starting CAN test.\n"
$helper.first_serial.expect_string "Initialize CAN mobs.\n"
$helper.first_serial.expect_string "Waiting for mode setup.\n"
$helper.first_serial.getOutputStream().write('M')
