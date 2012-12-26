#include<avr/wdt.h>

int main() {
	usart_init();

	if (usart_recv() == 'L') {
		//confirm the request
		usart_send_str("Low fuel test started\r\n");
		//begin the test

		while (usart_recv() != '0') {
			if (usart_recv() == '1') {
				INPUT(LOW_FUEL);
				PULLUP(LOW_FUEL);

				while(usart_recv() !='d') {
					if(usart_recv() == 'p') {
						if (LOW_FUEL != '0')
							usart_send_str("false\r\n");
					}

					if(usart_recv() == 'r') {
						if(LOW_FUEL != '1')
							usart_send_str("false\r\n");
					}
				}
			}

			if (usart_recv() == '2') {
				INPUT(LOW_FUEL);
				HIGH(LOW_FUEL);
			}
		}
		INPUT(name);
		PULLUP(name);

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
		usart_send_str("Headlight not working test started\r\n");

		//begin the test
		HIGH(HEADLIGHT_NOT_WORKING);
		OUTPUT(HEADLIGHT_NOT_WORKING);

		//if test passes turn off the LED
		if (usart_rec() == "turn_off")
			LOW(HEADLIGHT_NOT_WORKING);
	}

}

void pinTests(String name) {
	if (usart_recv() == '1') {
		INPUT(name);
		PULLUP(name);

		while(usart_recv() !='d') {
			if(usart_recv() == 'p') {
				if (name != '0')
					usart_send_str("false\r\n");
				}

			if(usart_recv() == 'r') {
				if(name != '1')
					usart_send_str("false\r\n");
					}
				}
			}

	if (usart_recv() == '2') {
		INPUT(name);
		HIGH(name);

		while(usart_recv() != 'd') {
			if(usart_recv() == 'p') {
				if (name != '0')
					usart_send_str("false\r\n");
			}

			if(usart_recv() == 'r') {
				if (name != '1')
					usart_send_str("false\r\n");
			}
		}
	}
}
