#DEPENDS ON:
this_file = "config.rb"

puts Dir.pwd

$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 640


$config.rtos.addTask("Monitor", SUSPENDED, 640)

100.times{|number| $config.rtos.addTask("Task#{number}", SUSPENDED, 20)}
