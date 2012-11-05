puts AVRDUDE
puts $flash_cmdline
system $flash_cmdline

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

#TODO wait for user