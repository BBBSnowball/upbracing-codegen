#include<avr/wdt.h>

int main() {
	usart_init();

	if (usart_recv() == "LOW_FUEL") {
		//confirm the request
		usart_send_str("Low fuel test started");
		//begin the test
		HIGH(LOW_FUEL);
		OUTPUT(LOW_FUEL);

		//if test passed turn off the LED
		if (usart_recv() == "turn_off")
			LOW(LOW_FUEL);
	}

	if (usart_recv() == "ENGINE_FAILURE") {
		//confirm the request
		usart_send_str("Engine failure test started");
		//begin the test
		HIGH(ENGINE_FAILURE);
		OUTPUT(ENGINE_FAILURE);

		//if test passes turn off the LED
		if (usart_recv() == "turn_off")
			LOW(ENGINE_FAILURE);
	}

	if (usart_recv() == "HEADLIGHT_NOT_WORKING") {
		//confirm the request
		usart_send_str("Headlight not working test started");

		//begin the test
		HIGH(HEADLIGHT_NOT_WORKING);
		OUTPUT(HEADLIGHT_NOT_WORKING);

		//if test passes turn off the LED
		if (usart_rec() == "turn_off")
			LOW(HEADLIGHT_NOT_WORKING);
	}

}
