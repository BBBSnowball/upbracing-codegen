#DEPENDS ON:
this_file = "config.rb"

puts Dir.pwd

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640

tm = $config.rtos.addTask("Monitor", SUSPENDED, 640)
t1 = $config.rtos.addTask("Task1", SUSPENDED, 20)
t2 = $config.rtos.addTask("Task2", SUSPENDED, 20)
t3 = $config.rtos.addTask("Task3", SUSPENDED, 20)
t4 = $config.rtos.addTask("Task4", SUSPENDED, 20)
t5 = $config.rtos.addTask("Task5", SUSPENDED, 20)
t6 = $config.rtos.addTask("Task6", SUSPENDED, 20)

tm.stackSize = 384
t1.stackSize = 384
t2.stackSize = 384
t3.stackSize = 384
t4.stackSize = 384
t5.stackSize = 384
t6.stackSize = 384