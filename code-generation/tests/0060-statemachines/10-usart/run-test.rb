# open serial line
# We do that before flashing the processor to get
# all the output. 
$helper.first_serial.ensure_baudrate 9600

# write the program on the processor
$helper.flash_processor

# Task1 will reply with (c+1), so we send (c-1)
#NOTE It doesn't work for 'special' chars - ",!- " are all special in that regards ;-)
#TODO investigate ^^ I guess this is some charset issue (Java/our helpers vs. JRuby)
#$helper.first_serial.write("some!string\r\n".each_char.map { |c| (c.ord-1).chr }.join)
string = "someD_string\r\n"
string.each_char do |c|
  $helper.first_serial.write (c.ord-1).chr
  
  # don't send it too fast
  sleep 0.05
end
$helper.first_serial.expect_regex ".?" + string
