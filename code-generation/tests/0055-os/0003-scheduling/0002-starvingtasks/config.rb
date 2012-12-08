#DEPENDS ON:
this_file = "config.rb"

puts Dir.pwd

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

$config.rtos.addTask("Monitor", SUSPENDED, 640)

#The periods are not important as the tasks are always running
$config.rtos.addTask("Task1", READY, 1337)
$config.rtos.addTask("Task2", READY, 1337)
$config.rtos.addTask("Task3", READY, 1337)
$config.rtos.addTask("Task4", READY, 1337)
$config.rtos.addTask("Task5", READY, 1337)
