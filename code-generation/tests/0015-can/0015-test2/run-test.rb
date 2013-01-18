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

# request variable values and check some of them
# vals: map from name to value, e.g. "TestMsg2:Test1" => 0x2a
# All values that aren't present in the map can be arbitrary hex numbers.
def testB(vals)
  vars = [
    "TestMsg2:Test1",
    "TestMsg3:Test1",
    "TestMsg3:Test2",
    "TestMsg4:Test1",
    "TestMsg6:Test1",
    "TestMsg7:Test1",
    "TestMsg7:Test2",
    "TestMsg8:Test1",
    "TestMsg8:Test2" ]
  
  regex = "Running test B\r\n"
  vars.each do |var|
    regex += "- #{var} = 0x" + (vals[var] ? vals[var].to_s(16) : "[0-9a-f]+") + "\r\n"
  end
  regex += "\r\n"

  $helper.first_serial.write "B\n"
  $helper.first_serial.expect_regex regex
end

vals = {}

$helper.first_serial.write "\n~t01212a\n"
testB "TestMsg2:Test1" => 0x2a

$helper.first_serial.write "\n~t012142\n"
vals["TestMsg2:Test1"] = 0x42
testB vals

$helper.first_serial.write "\n~t01322a07\n"
vals["TestMsg3:Test1"] = 42
vals["TestMsg3:Test2"] =  7
testB vals

$helper.first_serial.write "\n~t01322355\n"
vals["TestMsg3:Test1"] = 0x23
vals["TestMsg3:Test2"] = 0x55
testB vals

# test some of them in a row
$helper.first_serial.write <<EOF

~t01423412
~t016700000000351272
~t01780000003017fa7272
~t01880000002016fb7272

EOF
vals["TestMsg4:Test1"] = 0x1234
vals["TestMsg6:Test1"] = 0x1235
vals["TestMsg7:Test1"] = 0x1730
vals["TestMsg7:Test2"] = 0xfa
vals["TestMsg8:Test1"] = 0x1620
vals["TestMsg8:Test2"] = 0xfb
testB vals

$helper.first_serial.write <<EOF

~t01427856
~t0167dddddddd795672
~t0178eeeeee3122ae7272
~t0188ffffff3123af7272

EOF
vals["TestMsg4:Test1"] = 0x5678
vals["TestMsg6:Test1"] = 0x5679
vals["TestMsg7:Test1"] = 0x2231
vals["TestMsg7:Test2"] = 0xae
vals["TestMsg8:Test1"] = 0x2331
vals["TestMsg8:Test2"] = 0xaf
testB vals
