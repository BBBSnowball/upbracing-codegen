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
$helper.first_serial.expect_string "\nStarting CAN test.\n"
$helper.first_serial.expect_string "Initialize CAN with 500kbps.\n"
$helper.first_serial.expect_string "Initialize CAN mobs.\n"
$helper.first_serial.expect_string "Waiting for mode setup.\n"
puts "Send char M to board:" 
$helper.first_serial.getOutputStream().write(0x4D) # sends the char 'M' to set the board to master mode
puts "\n"
$helper.first_serial.expect_string "Set board to master mode.\n"
$helper.first_serial.expect_string "Waiting for slave board...\n"
$helper.first_serial.expect_string "Starting tests as master.\n"

# Test 1
test1 = $helper.start_test "Test 1: Simple 1 byte reply test"
begin
  $helper.first_serial.expect_string "\nTest 1/6: Simple 1 byte reply test\n"
  $helper.first_serial.expect_string "Test successful! Expected 0x55, received 0x55\n"
  test1.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test1.fail "Failed."
end
test1.pop

# Test 2
test2 = $helper.start_test "Test 2: Using the general MOB transmitter"
begin
  $helper.first_serial.expect_string "\nTest 2/6: Using the general MOB transmitter\n"
  $helper.first_serial.expect_string "Test successful! Expected (0xa1, 0xbe), received (0xa1, 0xbe)\n"
  test2.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test2.fail "Failed."
end
test2.pop

# Test 3
test3 = $helper.start_test "Test 3: Multiple messages in one MOB"
begin
  $helper.first_serial.expect_string "\nTest 3/6: Multiple messages in one MOB\n"
  $helper.first_serial.expect_string "Test successful! Expected (0x3434, 0xf24b), received (0x3434, 0xf24b)\n"
  test3.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test3.fail "Failed."
end
test3.pop

# Test 4
test4 = $helper.start_test "Test 4: Multiple signals and endianness test"
begin
  $helper.first_serial.expect_string "\nTest 4/6: Multiple signals and endianness test\n"
  $helper.first_serial.expect_string "Test successful! Expected (0x1337, 0x4242, 0x7fff), received (0x1337, 0x4242, 0x7fff)\n"
  test4.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test4.fail "Failed."
end
test4.pop

# Test 5
test5 = $helper.start_test "Test 5: Testing sending and receiving partly without generated code"
begin
  $helper.first_serial.expect_string "\nTest 5/6: Testing sending and receiving partly without generated code\n"
  $helper.first_serial.expect_string "Test successful! Expected (0xa1f9, 0x48a6), received (0xa1f9, 0x48a6)\n"
  test5.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test5.fail "Failed."
end
test5.pop


# Test 6
test6 = $helper.start_test "Test 6: Sending periodic messages with an OS task"
begin
  $helper.first_serial.expect_string "\nTest 6/6: Sending periodic messages with an OS task\n"
  $helper.first_serial.expect_string "Received periodic message 1 of 10\n", 5000
  t_start = Time.now

  for i in 2..10
    $helper.first_serial.expect_string "Received periodic message #{i} of 10\n", 5000
    
    #Measure time
    t = Time.now - t_start
    t_start = Time.now
    
    puts "Time for this message: #{t} seconds"
    if t < 0.9
      puts "Test failed. The message was to quick. It should not be quicker than 0.9 seconds."
      raise Java::de::upbracing::code_generation::tests::TestFailedException.new
    end
    if t > 1.1
      puts "Test failed. The message took to long. It should not take longer than 1.1 seconds"
      raise Java::de::upbracing::code_generation::tests::TestFailedException.new
    end
  end
 
  $helper.first_serial.expect_string "Test successful!\n"
  test6.succeed
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test6.fail "Failed."
end
test6.pop