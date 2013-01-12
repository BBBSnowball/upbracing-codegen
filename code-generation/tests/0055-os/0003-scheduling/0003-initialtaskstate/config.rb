#DEPENDS ON:
this_file = "config.rb"

puts Dir.pwd

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

$config.rtos.addTask("Ready", READY, 50)
t = $config.rtos.addTask("Suspended", SUSPENDED, 500)
# We have to set the phase of the alarm,
# otherwise it would be set to READY after one tick
t.alarm.phase = 499