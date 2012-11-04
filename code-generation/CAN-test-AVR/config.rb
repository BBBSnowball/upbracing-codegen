puts "Hello from config.rb"

puts Dir.pwd

ecus = read_ecu_list("ecu-list-cantest.xml")
$config.ecus = ecus

# $config.can = parse_dbc("can_test.dbc")

$config.selectEcu("Cockpit")

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640
#$config.rtos.conformance_class = "BCC1"

$config.rtos.addTask("Bla", SUSPENDED, 640)

# $config.canConfig.getMessage("Logger").period = "10ms"
# $config.canConfig.getMessage("Ignition").period = 0.01
# $config.canConfig.getMessage("Start").period = "0.5s"
