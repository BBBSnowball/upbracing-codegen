$config.rtos.clock = 8000000
$config.rtos.tick_period = "4 ms"

$config.rtos.addTask("Update", SUSPENDED, 8)
$config.rtos.addTask("Increment", SUSPENDED, 128)
$config.rtos.addTask("Shift", SUSPENDED, 24)
