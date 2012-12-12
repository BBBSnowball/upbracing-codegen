#DEPENDS ON:
this_file = "config.rb"

puts Dir.pwd

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

$config.rtos.addTask("Monitor", SUSPENDED, 2560)

$config.rtos.addTask("Critical", READY, 1280)
$config.rtos.addTask("Interfere", READY, 320)