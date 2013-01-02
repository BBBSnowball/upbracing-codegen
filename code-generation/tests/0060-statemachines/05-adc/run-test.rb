# open serial line
# We do that before flashing the processor to get
# all the output. 
$helper.first_serial.ensure_baudrate 9600

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

m = $helper.first_serial.expect_regex "0x([0-9a-fA-F]+)\r\n\s*[0-9]+\r\n0x([0-9a-fA-F]+)\r\n\s*[0-9]+\r\n"
raw_value_gnd = "#{m.group(1)}".to_i(16)
raw_value_ref = "#{m.group(2)}".to_i(16)

if raw_value_gnd > 10
  test.fail "ADC reports #{raw_value_gnd} for GND"
elsif (raw_value_ref - 1024 * 110/256).abs > 30
  test.fail "ADC reports #{raw_value_ref} for 1.1V, but should be near #{1024 * 110/256}"
else
  test.succeed
end
test.pop


# test with statemachine

test = $helper.start_test("test single conversion on channel 2, GND and 1.1V")

$helper.first_serial.write("2s")
sleep 0.1
$helper.first_serial.write("gs")
sleep 0.1
# we have to read it twice because the first measurement
# returns a wrong value
#TODO investigate!
$helper.first_serial.write("rss")

m = $helper.first_serial.expect_regex "S\s*0x([0-9a-fA-F]+) \\(0x02\\)\r\nS\s*0x([0-9a-fA-F]+) \\(0x1f\\)\r\nS\s*0x[0-9a-fA-F]+ \\(0x1e\\)\r\nS\s*0x([0-9a-fA-F]+) \\(0x1e\\)\r\n"
raw_value_ch2 = "#{m.group(1)}".to_i(16)
raw_value_gnd = "#{m.group(2)}".to_i(16)
raw_value_ref = "#{m.group(3)}".to_i(16)

if raw_value_gnd > 10
  test.fail "ADC reports #{raw_value_gnd} for GND"
elsif (raw_value_ref - 1024 * 110/256).abs > 30
  test.fail "ADC reports #{raw_value_ref} for 1.1V, but should be near #{1024 * 110/256}"
elsif (raw_value_ch2 - raw_value_channel2).abs > 50
  test.fail "ADC reports #{raw_value_ch2} for channel 2 (supply voltage), but previously we got #{raw_value_channel2}"
else
  test.succeed
end
test.pop


test = $helper.start_test("test continuous conversion on 1.1V")

begin
  # start continuous conversion on 1.1V
  $helper.first_serial.write("rS")
  
  # let them fill the buffer
  sleep 0.5
  
  # stop conversion and send a ping
  $helper.first_serial.write("dp")
  
  # we expect many conversion lines and a "pong"
  m = $helper.first_serial.expect_regex "(S\s*0x([0-9a-fA-F]+) \\(0x1e\\)\r\n)*pong\r\n", 4096*32
  
  value_count = 0
  fail = false
  m.group.scan(/S\s*0x([0-9a-fA-F]+) \(0x1e\)\r\n/).each do |value|
    value = $1.to_i(16)
    if  (value - 1024 * 110/256).abs > 40
      if not fail
        fail = true
        test.fail "ADC reports #{value} for 1.1V, but should be near #{1024 * 110/256}"
      end
    end
    
    value_count += 1
  end
  
  # with 9600 baud we can send about 1000 chars per second
  # 1000 chars/s * 0.5 s / (17 chars/line) = 29 lines
  if value_count < 25
    if not fail
      fail = true
      test.fail "ADC is too slow: in 0.5s we got only #{value_count} values (should be about 30)"
    end
  end

  test.succeed unless fail
ensure
  # stop output
  $helper.first_serial.write("dddd")
  
  test.pop
end
