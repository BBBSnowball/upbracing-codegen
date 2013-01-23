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
$helper.first_serial.expect_string "\nStarting overhead test.\n"

# Test
test = $helper.start_test "Overhead"
begin
  
  $helper.first_serial.expect_string "Measuring performance without OS...\n"
  $helper.first_serial.expect_string "Value: "
  raw_value = $helper.first_serial.expectInt
  
  $helper.first_serial.expect_string "Measuring performance with OS and two tasks...\n"
  $helper.first_serial.expect_string "Value Task 1: "
  os_task1 = $helper.first_serial.expectInt
  $helper.first_serial.expect_string "Value Task 2: "
  os_task2 = $helper.first_serial.expectInt
  $helper.first_serial.expect_string "Value OS: "
  os_value = $helper.first_serial.expectInt
  
  percent = (((raw_value-os_value) * 1000.0) / raw_value).floor;
  percent_str = "#{percent/10}.#{percent%10}"
  
  $toolkit.messages.info "OS Overhead: " + percent_str + " percent"
  
  if percent >= 100
    $toolkit.messages.error "Test failed. The OS overhead should be less than 10 percent"
    raise Java::de::upbracing::code_generation::tests::TestFailedException.new
  end
  $helper.first_serial.expect_string "Test successful. OS Overhead: " + percent_str, 5000  
  
  test.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test.fail "Failed."
end
test.pop