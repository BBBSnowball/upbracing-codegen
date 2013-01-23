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
$helper.first_serial.expect_string "\nStarting simple critical sections test.\n"

# Test
test = $helper.start_test "Simple Critical Section"
begin

  # The test outputs a summary 3 times
  for i in 1..3
    $helper.first_serial.expect_string "Test successful value = ", 10000
    value = $helper.first_serial.expectInt

    $helper.first_serial.expect_string " expected = ", 1000
    expected = $helper.first_serial.expectInt

    if value != expected
      $toolkit.messages.error "Test failed. Value does not equal expected value"
      raise Java::de::upbracing::code_generation::tests::TestFailedException.new
    end
    
  end
 
  test.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test.fail "Failed."
end
test.pop