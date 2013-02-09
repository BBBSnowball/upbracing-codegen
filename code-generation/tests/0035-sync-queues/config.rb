$config.rtos.clock = 8000000
$config.rtos.tick_period = "4 ms"

$config.rtos.addTask("Writer1", SUSPENDED, 8)
$config.rtos.addTask("Writer2", SUSPENDED, 24)
$config.rtos.addTask("Reader1", SUSPENDED, 24)
$config.rtos.addTask("Reader2", SUSPENDED, 32)
$config.rtos.addTask("Reader3", SUSPENDED, 64)
