#DEPENDS ON:
adc_statemachine = "adc.statemachine"

adc = $config.statemachines.load("adc_sm", adc_statemachine)
#enableTracing(counter, 50, "usart_send_str",
#  "#include \"../rs232.h\"")
