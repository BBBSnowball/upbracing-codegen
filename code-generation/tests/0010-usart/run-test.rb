puts $flash_cmdline
system $flash_cmdline

puts "You should have seen some output on the serial console."

STDIN.readline
