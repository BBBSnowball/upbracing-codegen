# We try calling some Java stuff in the tests-java-helpers
# project. It doesn't really belong here...
#TODO remove or put in a better place

# This should print 42
puts Java::blub::Blub::magic
# And so does this
puts Java::foo::Bar.new.getIt

puts Java::de::upbracing::code_generation::tests::serial::SerialHelper.DEFAULT_PORTS.inspect

# erase processor to make sure we get no further
# stuff on the serial line
$helper.erase_processor

# open serial line
# We do that before flashing the processor to get
# all the output. 
$helper.first_serial.ensure_baudrate 9600

# write the program on the processor
$helper.flash_processor

# check output on serial console
$helper.first_serial.expect_string "\r\n\r\nUSART test\r\n"

$helper.first_serial.expect_string <<EOF.gsub("\n", "\r\n")
string from program memory
testing usart_send_number:
0b11001010 -> 0b11001010
0b1011 -> 0b1011
0b101100001111 -> 0b101100001111
42 -> 42
0 -> 0
-10 -> -10
0xf123a -> 0xf123a
01234567 -> 01234567
-10 -> -10
0xf123a -> 0xf123a
01234567 -> 01234567
-  10 -> -  10
0x0f123a -> 0x0f123a
0001234567 -> 0001234567
36#az -> 36#az
36#az -> 36#az
36#0az -> 36#0az
done
EOF

#$helper.messages.info "We have the next check in wrong case, so it\n" + 
#  "should fail. This is expected."
test = $helper.start_test "expect_string with wrong string should fail"
begin
  $helper.first_serial.expect_string "TEST"
  $helper.messages.error "The test succeeded although it should fail."
  test.fail "The test succeeded although it should fail."
rescue Java::de::upbracing::code_generation::tests::TestFailedException
  test.succeed
  $helper.messages.info "expect_string fails, if we give the wrong string - great :-)"
end
test.pop
