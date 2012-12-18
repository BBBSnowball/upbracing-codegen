#include<avr/wdt.h>

int main() {

	usart_init();

	//if the received data asks for starting the temperature test
	if (usart_recv() == "Temperature") {
		//confirm the request
		usart_send_str("Temperature test started");
		//begin the test
		HIGH(ERROR_TEMPERATURE);
		OUTPUT(ERROR_TEMPERATURE);

		//turn off the led after the mini test has finished
		if (usart_recv() == "turn_off")
			LOW(ERROR_TEMPERATURE);
	}

	//if the received data asks for starting the battery test
	if (usart_recv() == "Battery") {
		//confirm the request
		usart_send_str("Battery test started");

		//begin the test
		HIGH(ERROR_BATTERY);
		OUTPUT(ERROR_BATTERY);

		//turn off the led after the mini test has finished
		if (usart_recv() == "turn_off")
			LOW(ERROR_BATTERY);
	}

	//if the received data asks for starting the oil pressure test
	if (usart_recv() == "Oil pressure") {
		//confirm the request
		usart_send_str("Oil pressure test started");

		//begin the test
		HIGH(ERROR_OIL_PRESSURE);
		OUTPUT(ERROR_OIL_PRSSURE);

		//turn off the led after the mini test has finished
		if (usart_recv == "turn_off")
			LOW(ERROR_OIL_PRESSURE);
	}
}
