$helper.flash_processor

puts "This is a manual test. If you want to run it,"
puts "please press ENTER now. You can press 's' and"
puts "ENTER to skip it immediately."
$result = nil
t = Thread.new do
	$result = STDIN.readline
end
10.times do |i|
	printf "#{10-i} "
	break if t.join 1
end
t.kill if t.alive?

if $result == nil || $result.strip == "s"
	puts "skipping test"
	exit 0
end

puts "A simple test program is running on the hardware."
puts "Please make sure that the buttons are working and"
puts "the counter is running with a frequency of 1 Hz."
puts ""
puts "The LED layout is like this:"
puts "(x is the counter; S, W, E, N and C are the buttons)"
puts " 7  6  5  4  3  2  1  0 "
puts " S  W  E  N  C  x2 x1 x0"
puts ""
puts "The button layout:"
puts "       North       "
puts " West  Center  East"
puts "       South       "

#TODO ask user for test result and report it

puts ""
puts "Please press enter, if you have finished testing it"

STDIN.readline
