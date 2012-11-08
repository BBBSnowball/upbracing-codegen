$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 5 #250

task_update = $config.rtos.addTask("Update", SUSPENDED, 1)
task_increment = $config.rtos.addTask("Increment", SUSPENDED, 5)
task_shift = $config.rtos.addTask("Shift", SUSPENDED, 1)
