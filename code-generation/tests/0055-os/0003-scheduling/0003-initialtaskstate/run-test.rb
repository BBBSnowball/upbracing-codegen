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
$helper.first_serial.expect_string "\nStarting initial task state test.\n"

# Test
test = $helper.start_test "Initial Task State"
begin
  
  $helper.first_serial.expect_string "Test successful. Counter = ", 5000
  $helper.first_serial.expectInt
  $helper.first_serial.expect_string "(expected >= 10)\n", 5000
  
  test.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test.fail "Failed."
end
test.pop