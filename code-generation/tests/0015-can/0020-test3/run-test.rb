$helper.first_serial.ensure_baudrate 9600

$helper.flash_processor

# startup
# At some point the board started to send a stray 0xff...
$helper.first_serial.expect_regex "[^\r\n]?\r\n"
$helper.first_serial.expect_string <<EOF.gsub("\n", "\r\n")
Starting CAN master (test3).
Initialize CAN with 500kbps.
Initialize CAN mobs.
Ready.
EOF

# test B
# We send some messages and look at the corresponding variables.

# request variable values and check some of them
# vals: map from name to value, e.g. "TestMsg2:Test1" => 0x2a
# All values that aren't present in the map can be arbitrary hex numbers. Values
# from previous runs will be remembered.
$vals = {}
def testB(new_vals = {})
  # how many signals are there in the message TestMsg#?
  #test_vals = { 1 => 0, 3 => 2, 5 => 0, 7 => 2, 8 => 2 }
  #test_vals.default = 1
  #vars = (1..14).to_a.flat_map { |i| (1..test_vals[i]).to_a.map { |j| "TestMsg#{i}:Test#{j}" } }
  # Well, we could do it like that ^^, but a list is easier to understand ;-)
  vars = [
    "TestMsg03:Test1",
    "TestMsg03:Test3",
    "TestMsg04:Test1",
    "TestMsg06:Test1",
    "TestMsg07:Test1",
    "TestMsg07:Test2",
    "TestMsg08:Test1",
    "TestMsg08:Test2",
    "TestMsg09:Test1",
    "TestMsg10:Test1",
    "TestMsg11:Test1",
    "TestMsg12:Test1",
    "TestMsg13:Test1",
    "TestMsg14:Test1",
    "TestMsg15:Test1",
    "TestMsg16:Test1",
    "TestMsg17:Test1",
    "TestMsg17:Test2",
    "TestMsg17:Test3" ]
    
  unknown_vars = new_vals.keys - vars
  unless unknown_vars.empty?
    raise "unknown variable: " + unknown_vars.join(", ")
  end
  
  $vals.merge! new_vals
  
  regex = "Running test B\r\n"
  vars.each do |var|
    regex += "- #{var} = 0x" + ($vals[var] ? $vals[var].to_s(16) : "-?[0-9a-f]+") + "\r\n"
  end
  regex += "\r\n"
  
  # minus should be before '0x', so we fix that
  # (optional '?' after the minus, so we fix the regex as well)
  regex = regex.gsub /0x(-\??)/, "\\10x"

  $helper.first_serial.write "B\n"
  $helper.first_serial.expect_regex regex
end

# test C
# check value of hook variables
# hooks: list of hooks that should have been executed, in order
# All values that aren't present in the map are assumed to be 0. Values
# from previous runs will be remembered.
$hook_var_vals = {}
$hook_counter = 0
def testC(*hooks)
  hooks = hooks.flatten
  # This was a long list, but now we simply extract it from the config.rb file
  hook_vars = [
      "TestMsg02_rx_handler",
      "TestMsg03_after_rx",
      "TestMsg03_before_rx"
      # ...
    ].sort
  hook_vars = File.read("config.rb").scan(/create_hook\(?\s*"([^"]+)"\)?/).flatten.sort
  
  unknown_vars = hooks - hook_vars
  unless unknown_vars.empty?
    raise "unknown hook: " + unknown_vars.join(", ")
  end
  
  hooks.each do |hook|
    $hook_counter += 1
    $hook_var_vals[hook] = $hook_counter
  end
  
  expected_value = "Running test C\r\n"
  hook_vars.each do |var|
    expected_value += "- #{var}: " + ($hook_var_vals[var] ? $hook_var_vals[var].to_s(10) : "0") + "\r\n"
  end
  expected_value += "\r\n"
  
  $helper.first_serial.write "C\n"
  $helper.first_serial.expect_string expected_value
end


# send TestMsg02 and see whether its receive handler is invoked
$helper.first_serial.write "\n~t 012 1 55\n"
testC "TestMsg02_rx_handler"

# send TestMsg03 and check before/after hooks on the message and signals
$helper.first_serial.write "\n~t 013 2 2a 07\n"
hooks = [
  "before_rx", "Test1_before_rx", "Test2_before_rx",
  "Test1_imm_before_rx", "Test1_imm_after_rx",
  "Test2_imm_before_rx", "Test2_put_value", "Test2_imm_after_rx",
  "Test1_after_rx", "Test2_after_rx", "after_rx"
]
testC(hooks.map { |x| "TestMsg03_#{x}" })

# check the value that has been passed to the 'get_value' hook
$helper.first_serial.write("D\n")
$helper.first_serial.expect_string <<EOF.gsub("\n", "\r\n")
Running test D
TestMsg03_Test2_put_value = 0x07

EOF


# transmit message TestMsg07 (manually) and check hooks
$helper.first_serial.write("A\n")
$helper.first_serial.expect_regex <<EOF.gsub("\n", "\r\n")
Running test A
~t 017 8 .. .. .. 55 42 fa .. ..
~t 017 8 .. .. .. 55 42 ae .. ..
EOF
hooks = [
  "before_tx", "Test1_before_tx", "Test2_before_tx",
  "Test1_imm_before_tx", "Test1_get_value", "Test1_imm_after_tx",
  "Test2_imm_before_tx", "Test2_imm_after_tx",
  "Test1_after_tx", "Test2_after_tx", "after_tx"
]
# '*2' because we send the message two times
testC(hooks.map { |x| "TestMsg07_#{x}" } * 2)


# transmit some messages which have tx_handlers
$helper.first_serial.write("E\n")
$helper.first_serial.expect_string <<EOF.gsub("\n", "\r\n")
Running test E
~t 014 4 12 34 56 78
~T 00000042 3 34 56 78
~t 016 7 56 78 12 34 12 34 42
EOF
hooks = ["TestMsg04", "TestMsg09", "TestMsg06"].product(["before_tx", "Test1_before_tx", "some_tx_handler", "Test1_after_tx", "after_tx"]).map { |msg,hook| "#{msg}_#{hook}" }
testC hooks
