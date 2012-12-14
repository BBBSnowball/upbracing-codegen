#DEPENDS ON:
this_file = "config.rb"

puts Dir.pwd

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

$config.rtos.addTask("Print1", READY, 320)
$config.rtos.addTask("Print2", READY, 1280)

$config.rtos.addTask("UsartTransmit", READY, 1)
