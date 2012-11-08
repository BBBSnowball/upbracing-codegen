puts "Hello from config.rb"

puts Dir.pwd

require 'parse-dbc.rb'
require 'parse-ecu-list.rb'

ecus = read_ecu_list("../../../ecu-list.xml")
$config.ecus = ecus

$config.can = parse_dbc("../../../can_final.dbc")

$config.selectEcu("Cockpit")

# Uncomment and specify path for Timer Configuration file
# $config.loadTimerConfiguration("/Volumes/Data/Peer/Documents/Uni/RacingCarIT/Program/runtime-EclipseApplication/Test/new_configuration.tcxml")

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 250
# $config.rtos.conformance_class = "BCC1"

task_update = $config.rtos.addTask("Update", SUSPENDED)
task_increment = $config.rtos.addTask("Increment", SUSPENDED)
task_shift = $config.rtos.addTask("Shift", SUSPENDED)
task_UsartTransmit = $config.rtos.addTask("UsartTransmit", SUSPENDED)
updateAlarm = task_update.setAlarm(8)
incrementAlarm = task_increment.setAlarm(128)
shiftAlarm = task_shift.setAlarm(24)
usartAlarm = task_UsartTransmit.setAlarm(1)

$config.rtos.getAlarms().add(updateAlarm)
$config.rtos.getAlarms().add(incrementAlarm)
$config.rtos.getAlarms().add(shiftAlarm)
$config.rtos.getAlarms().add(usartAlarm)