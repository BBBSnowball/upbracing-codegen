puts "Hello from config.rb"

puts Dir.pwd

require 'parse-dbc.rb'
require 'parse-ecu-list.rb'

ecus = read_ecu_list("../../../ecu-list.xml")
$config.ecus = ecus

$config.can = parse_dbc("../../../can_final.dbc")

$config.selectEcu("Cockpit")

# Uncomment and specify path for Timer Configuration file
$config.loadTimerConfiguration("/Volumes/Data/Peer/Documents/Uni/RacingCarIT/Program/runtime-EclipseApplication/Test/new_configuration.tcxml")

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 250
$config.rtos.conformance_class = "BCC1"

task_idle = $config.rtos.addTask("Idle", READY)
task_update = $config.rtos.addTask("Update", SUSPENDED)
task_increment = $config.rtos.addTask("Increment", SUSPENDED)
task_shift = $config.rtos.addTask("Shift", SUSPENDED)
