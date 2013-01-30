# configure RTOS
$config.rtos.clock = 8000000
$config.rtos.tick_frequency = 100

$config.rtos.addTask("Task1", SUSPENDED, 10)

#DEPENDS ON:
usart_recv_statemachine = "usart_recv.statemachine"
usart_recv = $config.statemachines.load("usart_recv_sm", usart_recv_statemachine)

#D EPENDS ON:
#usart_send_statemachine = "usart_send.statemachine"
#usart_send = $config.statemachines.load("usart_send_sm", usart_send_statemachine)
