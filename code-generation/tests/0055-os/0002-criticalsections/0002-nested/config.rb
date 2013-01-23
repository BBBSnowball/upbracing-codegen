#DEPENDS ON:
this_file = "config.rb"

puts Dir.pwd

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

t = $config.rtos.addTask("Monitor", SUSPENDED, 2560)
t.alarm.phase = 2559

$config.rtos.addTask("Critical", READY, 1280)
$config.rtos.addTask("Interfere", SUSPENDED, 160)