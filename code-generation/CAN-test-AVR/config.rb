puts "Hello from config.rb"

puts Dir.pwd

ecus = read_ecu_list("ecu-list-cantest.xml")
$config.ecus = ecus

$config.can = parse_dbc("can_test.dbc")

$config.selectEcu("TestBoard")

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

$config.rtos.addTask("Bla", SUSPENDED, 640)

# Message 3 shares a MOB
$config.can.getMessage("TestMessage3A").rxMob = "TestMessage3Shared"
$config.can.getMessage("TestMessage3B").rxMob = "TestMessage3Shared"

$config.can.getMessage("TestMessage1").period = "10ms"
# $config.can.getMessage("TestMessage3").period = "100ms"