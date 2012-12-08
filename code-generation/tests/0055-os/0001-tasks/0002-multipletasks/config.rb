#DEPENDS ON:
this_file = "config.rb"

puts Dir.pwd

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

$config.rtos.addTask("Monitor", SUSPENDED, 640)

$config.rtos.addTask("Task1", SUSPENDED, 20)
$config.rtos.addTask("Task2", SUSPENDED, 20)
$config.rtos.addTask("Task3", SUSPENDED, 20)
$config.rtos.addTask("Task4", SUSPENDED, 20)
$config.rtos.addTask("Task5", SUSPENDED, 20)
$config.rtos.addTask("Task6", SUSPENDED, 20)
