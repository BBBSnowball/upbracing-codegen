
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
$helper.first_serial.expect_string "\r\n\r\nADC test\r\n"

# let's first make sure that the ADC is fine

test = $helper.start_test("test ADC channel 2")

# one conversion on channel 2
# (processor supply voltage with voltage divider)
$helper.first_serial.write("2o")

m = $helper.first_serial.expect_regex "0x([0-9a-fA-F]+)\r\n\s*([0-9]+)\r\n"
raw_value_channel2 = "#{m.group(1)}".to_i(16)

# The value depends on the supply voltage of the processor, so
# we have to accept a lot of values. This is fine because we
# mainly need this as a reference for the statemachine tests.
# factor 5 due to voltage divider on the board and 1024 would mean 2.56V
supply_voltage = raw_value_channel2 * 5 * 256 / 1024
# we accept 3V to 6V
if supply_voltage >= 300 and supply_voltage <= 600
  test.succeed
else
  test.fail "ADC reports a supply voltage of #{supply_voltage / 100.0}V (raw value 0x#{raw_value_channel2.to_s(16)}, but it should be between 3V and 6V"
end
test.pop

# also test reference voltages: 0V ('g' - gnd) and 1.1V ('r' - reference)
test = $helper.start_test("test GND and 1.1V reference")

$helper.first_serial.write("goro")

m = $helper.first_serial.expect_regex "0x([0-9a-fA-F]+)\r\n0x([0-9a-fA-F]+)\r\n"
raw_value_gnd = "#{m.group(1)}".to_i(16)
raw_value_ref = "#{m.group(1)}".to_i(16)

if raw_value_gnd > 10
  test.fail "ADC reports #{raw_value_gnd} for GND"
elsif (raw_value_ref - 1024 * 110/256).abs > 30
  test.fail "ADC reports #{raw_value_ref} for 1.1V, but should be near #{1024 * 110/256}"
end
test.pop

#TODO test statemachine
