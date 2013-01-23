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
$helper.first_serial.expect_string "\nStarting single task test.\n"


# Test
test = $helper.start_test "Single task"
begin
  
  # Don't measure the first task...
  $helper.first_serial.expect_string "One second\n", 5000

  # The task on the board runs 10 times
  t_start = Time.now
  for i in 2..10
    $helper.first_serial.expect_string "One second\n", 5000
    
    #Measure time
    t = Time.now - t_start
    t_start = Time.now

    $toolkit.messages.info "Time since last activation: #{t} seconds"
    if t < 0.9
      $toolkit.messages.error "Test failed. The task was to quick. It should not be quicker than 0.9 seconds."
      raise Java::de::upbracing::code_generation::tests::TestFailedException.new
    end
    if t > 1.1
      $toolkit.messages.error "Test failed. The task took to long. It should not take longer than 1.1 seconds"
      raise Java::de::upbracing::code_generation::tests::TestFailedException.new
    end
  end
 
  test.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test.fail "Failed."
end
test.pop