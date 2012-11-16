# This should print 42
puts Java::blub::Blub::magic
# And so does this
puts Java::foo::Bar.new.getIt

$helper.flash_processor

puts "You should have seen some output on the serial console."

STDIN.readline
