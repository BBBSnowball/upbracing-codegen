$helper.first_serial.ensure_baudrate 9600

$helper.flash_processor

# startup
$helper.first_serial.expect_string <<EOF.gsub("\n", "\r\n")

Starting CAN master.
Initialize CAN with 500kbps.
Initialize CAN mobs.
Ready.
EOF

# test A
# The MCU sends some messages.
#NOTE Some parts of the messages are not defined in the
#     DBC file, so we use a regex to allow any data in
#     those parts of the messages.
$helper.first_serial.write "A"
$helper.first_serial.expect_regex <<EOF.gsub("\n", "\r\n")
^Running test A
~t0110
~t0110
~t0110
~t01212a
~t012155
~t012142
~t01322a07
~t01423412
~t0152....
~t0167........3412..
~t0167........7856..
~t0178......3017fa....
~t0178......3122ae....
~t0188......3017fa....
~t0188......3122ae....
EOF

# test B
# We send some messages and look at the corresponding variables.
$helper.first_serial.write "\n~t01212a\n"
$helper.first_serial.write "B"
$helper.first_serial.expect_regex <<EOF.gsub("\n", "\r\n")
Running test B
- TestTransmit2:Test1 = 0x2a
- TestTransmit3:Test1 = 0x[0-9a-f]+
- TestTransmit3:Test2 = 0x[0-9a-f]+
- TestTransmit4:Test1 = 0x[0-9a-f]+
- TestTransmit6:Test1 = 0x[0-9a-f]+
- TestTransmit7:Test1 = 0x[0-9a-f]+
- TestTransmit7:Test2 = 0x[0-9a-f]+
- TestTransmit8:Test1 = 0x[0-9a-f]+
- TestTransmit8:Test2 = 0x[0-9a-f]+

EOF


$helper.first_serial.write "\n~t012142\n"
$helper.first_serial.write "B"
$helper.first_serial.expect_regex <<EOF.gsub("\n", "\r\n")
Running test B
- TestTransmit2:Test1 = 0x42
- TestTransmit3:Test1 = 0x[0-9a-f]+
- TestTransmit3:Test2 = 0x[0-9a-f]+
- TestTransmit4:Test1 = 0x[0-9a-f]+
- TestTransmit6:Test1 = 0x[0-9a-f]+
- TestTransmit7:Test1 = 0x[0-9a-f]+
- TestTransmit7:Test2 = 0x[0-9a-f]+
- TestTransmit8:Test1 = 0x[0-9a-f]+
- TestTransmit8:Test2 = 0x[0-9a-f]+

EOF
