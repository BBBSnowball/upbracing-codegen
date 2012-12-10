#DEPENDS ON:
this_file = "config.rb"

puts Dir.pwd

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

$config.rtos.addTask("Ready", READY, 50)
$config.rtos.addTask("Suspended", SUSPENDED, 500)