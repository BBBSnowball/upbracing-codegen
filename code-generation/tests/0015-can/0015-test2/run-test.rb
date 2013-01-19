$helper.first_serial.ensure_baudrate 9600

$helper.flash_processor

# startup
# At some point the board started to send a stray 0xff...
$helper.first_serial.expect_regex "[^\r\n]?\r\n"
$helper.first_serial.expect_string <<EOF.gsub("\n", "\r\n")
Starting CAN master.
Initialize CAN with 500kbps.
Initialize CAN mobs.
Ready.
EOF

# test A
# The MCU sends some messages.
#NOTE Some parts of the messages are not defined in the
#     DBC file, so we use a regex to allow any data in
#     those parts of the messages.
$helper.first_serial.write "A"
$helper.first_serial.expect_regex <<EOF.gsub("\n", "\r\n").gsub(" ", "[ \\t]*")
^Running test A
~t 011 0
~t 011 0
~t 011 0
~t 012 1 2a
~t 012 1 55
~t 012 1 42
~t 013 2 2a 07
~t 014 2 34 12
~t 015 2 .. ..
~t 016 7 .. .. .. .. 34 12 ..
~t 016 7 .. .. .. .. 78 56 ..
~t 017 8 .. .. .. 30 17 fa .. ..
~t 017 8 .. .. .. 31 22 ae .. ..
~t 018 8 .. .. .. 30 17 fa .. ..
~t 018 8 .. .. .. 31 22 ae .. ..
~T 00000042 1 37
~T 00000043 1 38
~T 00000044 1 39
~t      039 1 40
~T 00000000 1 41
~T 1fffffff 1 42
EOF

# test B
# We send some messages and look at the corresponding variables.

# request variable values and check some of them
# vals: map from name to value, e.g. "TestMsg2:Test1" => 0x2a
# All values that aren't present in the map can be arbitrary hex numbers.
$vals = {}
def testB(new_vals = {})
  # how many signals are there in the message TestMsg#?
  #test_vals = { 1 => 0, 3 => 2, 5 => 0, 7 => 2, 8 => 2 }
  #test_vals.default = 1
  #vars = (1..14).to_a.flat_map { |i| (1..test_vals[i]).to_a.map { |j| "TestMsg#{i}:Test#{j}" } }
  # Well, we could do it like that ^^, but a list is easier to understand ;-)
  vars = [
    "TestMsg02:Test1",
    "TestMsg03:Test1",
    "TestMsg03:Test2",
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
    "TestMsg14:Test1" ]
    
  unknown_vars = new_vals.keys - vars
  unless unknown_vars.empty?
    raise "unknown variable: " + unknown_vars.join(", ")
  end
  
  $vals.merge! new_vals
  
  regex = "Running test B\r\n"
  vars.each do |var|
    regex += "- #{var} = 0x" + ($vals[var] ? $vals[var].to_s(16) : "[0-9a-f]+") + "\r\n"
  end
  regex += "\r\n"

  $helper.first_serial.write "B\n"
  $helper.first_serial.expect_regex regex
end

$helper.first_serial.write "\n~t01212a\n"
testB "TestMsg02:Test1" => 0x2a

$helper.first_serial.write "\n~t012142\n"
testB "TestMsg02:Test1" => 0x42

$helper.first_serial.write "\n~t01322a07\n"
testB "TestMsg03:Test1" => 42,
      "TestMsg03:Test2" =>  7

$helper.first_serial.write "\n~t01322355\n"
testB "TestMsg03:Test1" => 0x23,
      "TestMsg03:Test2" => 0x55


# test some of them in a row
$helper.first_serial.write <<EOF

~t 014 2 3412
~t 016 7 00000000 35 12 72
~t 017 8 000000 30 17 fa 7272
~t 018 8 000000 20 16 fb 7272

EOF
testB "TestMsg04:Test1" => 0x1234,
      "TestMsg06:Test1" => 0x1235,
      "TestMsg07:Test1" => 0x1730,
      "TestMsg07:Test2" => 0xfa,
      "TestMsg08:Test1" => 0x1620,
      "TestMsg08:Test2" => 0xfb

      
$helper.first_serial.write <<EOF

~t 014 2 78 56
~t 016 7 dddddddd  79 56  72
~t 017 8 eeeeee  31 22 ae  7272
~t 018 8 ffffff  31 23 af  7272

EOF
testB "TestMsg04:Test1" => 0x5678,
      "TestMsg06:Test1" => 0x5679,
      "TestMsg07:Test1" => 0x2231,
      "TestMsg07:Test2" => 0xae,
      "TestMsg08:Test1" => 0x2331,
      "TestMsg08:Test2" => 0xaf


# test extended IDs
$helper.first_serial.write <<EOF

~T 00000042 1 57
~T 00000043 1 58
~T 00000044 1 59
~t      039 1 50
~T 00000000 1 51
~T 1fffffff 1 52

EOF
testB "TestMsg09:Test1" => 0x57,
      "TestMsg10:Test1" => 0x58,
      "TestMsg11:Test1" => 0x59,
      "TestMsg12:Test1" => 0x50,
      "TestMsg13:Test1" => 0x51,
      "TestMsg14:Test1" => 0x52

# and a few more of them
$helper.first_serial.write <<EOF

~T 00000042 1 31
~T 00000043 1 32
~T 00000044 1 33
~t      039 1 34
~T 00000000 1 35
~T 1fffffff 1 36

EOF
testB "TestMsg09:Test1" => 0x31,
      "TestMsg10:Test1" => 0x32,
      "TestMsg11:Test1" => 0x33,
      "TestMsg12:Test1" => 0x34,
      "TestMsg13:Test1" => 0x35,
      "TestMsg14:Test1" => 0x36

# Those messages trigger the shared MOb (or may
# call mailfunction in another way), but
# they shouldn't change anything.
$helper.first_serial.write <<EOF

~T 00000041 1 77
~t      043 1 78
~T 00000045 1 99
~T 00000001 1 70
~T 00000010 1 71
~T 00000100 1 72
~T 00001000 1 73
~T 00002000 1 74
~T 00040000 1 75
~T 00800000 1 76
~T 01000000 1 77
~T 08000000 1 78
~T 10000000 1 79
~T 0fffffff 1 80
~T 17ffffff 1 80
~T 1f7fffff 1 81
~T 1fefffff 1 82
~T 1ffdffff 1 83
~T 1fffbfff 1 84
~T 1ffff7ff 1 85
~T 1fffffef 1 86
~T 1ffffffe 1 87

EOF
testB # nothing changed

# make sure everything is fine
$helper.first_serial.write <<EOF

~T 00000042 1 17
~T 00000043 1 18
~T 00000044 1 19
~t      039 1 20
~T 00000000 1 21
~T 1fffffff 1 22

EOF
testB "TestMsg09:Test1" => 0x17,
      "TestMsg10:Test1" => 0x18,
      "TestMsg11:Test1" => 0x19,
      "TestMsg12:Test1" => 0x20,
      "TestMsg13:Test1" => 0x21,
      "TestMsg14:Test1" => 0x22
