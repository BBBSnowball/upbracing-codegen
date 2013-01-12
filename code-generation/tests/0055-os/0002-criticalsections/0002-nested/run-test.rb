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

# Initialization
$helper.first_serial.expect_string "\nStarting nested critical sections test.\n"

# Test
test = $helper.start_test "Nested Critical Sections"
begin

  # The test outputs a summary 3 times
  for i in 1..3
    $helper.first_serial.expect_string "Test successful value1 = ", 10000
    value1 = $helper.first_serial.expectInt
    $helper.first_serial.expect_string " expected1 = ", 1000
    expected1 = $helper.first_serial.expectInt

    $helper.first_serial.expect_string " value2 = ", 1000
    value2 = $helper.first_serial.expectInt
    $helper.first_serial.expect_string " expected2 = ", 1000
    expected2 = $helper.first_serial.expectInt
    
    $helper.first_serial.expect_string " value3 = ", 1000
    value3 = $helper.first_serial.expectInt
    $helper.first_serial.expect_string " notexpected3 = ", 1000
    notexpected3 = $helper.first_serial.expectInt
    
    if value1 != expected1 || value2 != expected2 || value3 == notexpected3
      $toolkit.messages.error "Test failed. Values do not equal expected values"
      raise Java::de::upbracing::code_generation::tests::TestFailedException.new
    end
    
  end
 
  test.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test.fail "Failed."
end
test.pop