#DEPENDS ON:
this_file = "config.rb"
#DEPENDS ON:
timerconfig = "timer.tcxml"

puts Dir.pwd

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

$config.loadTimerConfiguration(timerconfig)

tm = $config.rtos.addTask("Monitor", SUSPENDED, 666) # This value should be slightly more than one second 
t1 = $config.rtos.addTask("Task1", READY, 2)
t2 = $config.rtos.addTask("Task2", READY, 2)