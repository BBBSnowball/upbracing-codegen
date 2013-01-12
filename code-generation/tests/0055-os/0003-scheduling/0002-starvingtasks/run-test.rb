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
$helper.first_serial.expect_string "\nStarting starving tasks test.\n"

# Test
test = $helper.start_test "Starving Tasks"
begin

  $helper.first_serial.expect_string "\nSum:", 5000
  sum = $helper.first_serial.expectInt

  $helper.first_serial.expect_string "Task 1:", 5000
  min = $helper.first_serial.expectInt
  max = min
  
  for j in 2..5
    $helper.first_serial.expect_string "Task #{j}:", 5000
    number = $helper.first_serial.expectInt
    if number > max
      max = number
    end
    if number < min
      min = number
    end
  end
  if max-min > min*0.05
    $toolkit.messages.error "Test failed. Task execution counters differ by more than five percent"
    raise Java::de::upbracing::code_generation::tests::TestFailedException.new
  end
 
  test.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test.fail "Failed."
end
test.pop