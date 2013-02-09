strings = ["Update", "Increment", "Shift"]
period  = [32, 512, 96]
frequency = period.map { |p| period.max / p }
min_count = 5 # we want each string to occur at least 5 times
wait_for_n_strings = min_count * frequency.reduce { |a,b| a+b }

# slowest task will need (min_count * period.max) milliseconds
# The other tasks need about the same time (faster, but need
# to do more).
# We give them a bit more time (25%).
#NOTE: It will be much faster because the tasks terminate
#      themselves before their time is up.
timeout = min_count * period.max * period.length * 5/4
puts "timeout: #{timeout} ms"

$helper.first_serial.ensure_baudrate 9600

$helper.flash_processor

$helper.first_serial.expect_regex ".?async-n-semaphore test\\r\\n"

# start it
$helper.first_serial.write "s"

x = $helper.first_serial.expect_regex "^(async-n-semaphore test\\r\\n)?(Update|Increment|Shift\\r\\n){#{wait_for_n_strings}}", timeout
text = x.group.to_s

counts = strings.map { |str| text.scan(str).length }

puts   # print newline, so our output isn't appended to a line of serial output
puts "got " + strings.zip(counts).map { |str,count| "#{count}x #{str}" }.join(", ")

puts "expected about " + strings.zip(frequency).map { |str,count| "#{count*min_count}x #{str}" }.join(", ")

ok = true
strings.zip(frequency, counts) do |string, freq, count|
  #TODO We have to allow for a very high error because we have 7x Increment instead of 5x.
  accepted_error = 0.20  # value can be off by 20% + 1
  freq *= min_count     # normalize freq with the amount of data we have read
  unless freq * (1-accepted_error) - 1 <= count and count <= freq * (1+accepted_error) + 1
    $helper.messages.error "Expected about #{freq}x #{string}, but got #{count}"
    ok = false
  end
end
$helper.messages.info "The counts are within the accepted bounds (+/- 20%%)." if ok

# stop it
$helper.first_serial.write "e"
$helper.first_serial.expect_regex ".{0,300}stopped"
