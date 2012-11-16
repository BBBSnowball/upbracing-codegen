puts $flash_cmdline
system $flash_cmdline

puts "You should have seen the test output on the serial console."

STDIN.readline
