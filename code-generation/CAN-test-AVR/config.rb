puts "Hello from config.rb"

puts Dir.pwd

require 'parse-dbc.rb'
require 'parse-ecu-list.rb'

ecus = read_ecu_list("ecu-list-cantest.xml")
$config.ecus = ecus

$config.can = parse_dbc("can_test.dbc")

$config.selectEcu("Cockpit")

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 250
$config.rtos.conformance_class = "BCC1"

task_usart = $config.rtos.addTask("UsartTransmit", SUSPENDED, 5)

$config.canConfig.getMessage("Logger").period = "10ms"
$config.canConfig.getMessage("Ignition").period = 0.01
$config.canConfig.getMessage("Start").period = "0.5s"
