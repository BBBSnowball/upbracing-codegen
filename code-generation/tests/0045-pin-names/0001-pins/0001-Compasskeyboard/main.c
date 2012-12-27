#include<avr/wdt.h>

int main() {
	usart_init();

	if (usart_recv() == 'L') {
		//confirm the request
		usart_send_str("Low fuel test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			if (usart_recv() == '1') {
				PULLUP(LOW_FUEL);
				INPUT(LOW_FUEL);

				while (usart_recv() != 'd') {
					if (usart_recv() == 'p') {
						if (name != '0')
							usart_send_str("false\r\n");
					}

					if (usart_recv() == 'r') {
						if (name != '1')
							usart_send_str("false\r\n");
					}
				}
			}

			if (usart_recv() == '2') {
				HIGH(LOW_FUEL);
				INPUT(LOW_FUEL);

				while (usart_recv() != 'd') {
					if (usart_recv() == 'p') {
						if (name != '0')
							usart_send_str("false\r\n");
					}

					if (usart_recv() == 'r') {
						if (name != '1')
							usart_send_str("false\r\n");
					}
				}
			}

			if (usart_recv() == '3') {
				LOW(LOW_FUEL);
				INPUT(LOW_FUEL);

				while (usart_recv() != 'd') {
					if (usart_recv() == 'p') {
						if (name != '0')
							usart_send_str("false\r\n");
					}

					if (usart_recv() == 'r') {
						if (name != '0')
							usart_send_str("false\r\n");
					}
				}
			}

			if (usart_recv() == '4') {
				TOGGLE(LOW_FUEL);
				INPUT(LOW_FUEL);

				while (usart_recv() != 'd') {
					if (usart_recv() == 'p') {
						if (name != '0')
							usart_send_str("false\r\n");
					}

					if (usart_recv() != 'r') {
						if (name != '1')
							usart_send_str("false\r\n");
					}
				}
			}
		}
	}

	if (usart_recv() == 'E') {
		//confirm the request
		usart_send_str("Engine failure test started\r\n");

		//begin the test
		while (usart_recv() != '0')
			pinTests(ENGINE_FAILURE);
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

		while (usart_recv() != 'd') {
			if (usart_recv() == 'p') {
				if (name != '0')
					usart_send_str("false\r\n");
			}

			if (usart_recv() == 'r') {
				if (name != '1')
					usart_send_str("false\r\n");
			}
		}
	}

	if (usart_recv() == '2') {
		INPUT(name);
		HIGH(name);

		while (usart_recv() != 'd') {
			if (usart_recv() == 'p') {
				if (name != '0')
					usart_send_str("false\r\n");
			}

			if (usart_recv() == 'r') {
				if (name != '1')
					usart_send_str("false\r\n");
			}
		}
	}

	if (usart_recv() == '3') {
		INPUT(name);
		LOW(name);

		while (usart_recv() != 'd') {
			if (usart_recv() == 'p') {
				if (name != '0')
					usart_send_str("false\r\n");
			}

			if (usart_recv() == 'r') {
				if (name != '0')
					usart_send_str("false\r\n");
			}
		}
	}

	if (usart_recv() == '4') {
		INPUT(name);
		TOGGLE(name);

		while (usart_recv() != 'd') {
			if (usart_recv() == 'p') {
				if (name != '0')
					usart_send_str("false\r\n");
			}

			if (usart_recv() != 'r') {
				if (name != '1')
					usart_send_str("false\r\n");
			}
		}
	}
}
