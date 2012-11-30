# We try calling some Java stuff in the tests-java-helpers
# project. It doesn't really belong here...
#TODO remove or put in a better place

# This should print 42
puts Java::blub::Blub::magic
# And so does this
puts Java::foo::Bar.new.getIt

puts Java::de::upbracing::code_generation::tests::serial::SerialHelper::DEFAULT_PORTS.inspect

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

puts "You should have seen some output on the serial console."

STDIN.readline
