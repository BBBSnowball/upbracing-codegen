#DEPENDS ON:
counter_statechart = "counter.statecharts"

counter = $config.statemachines.load("counter", counter_statechart)
enableTracing(counter, 50, "usart_send_str",
  "#include \"../rs232.h\"")
