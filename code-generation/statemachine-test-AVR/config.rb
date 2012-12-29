#DEPENDS ON:
counter_statemachine = "counter.statemachine"

counter = $config.statemachines.load("counter", counter_statemachine)
enableTracing(counter, 50, "usart_send_str",
  "#include \"../rs232.h\"")
