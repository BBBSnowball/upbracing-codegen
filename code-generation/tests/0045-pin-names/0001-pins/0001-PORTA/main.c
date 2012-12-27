#include<avr/wdt.h>
#include<rs232.h>
#include "gen/pins.h"
#include<Pins.h>

int main() {

	usart_init();

	//if the received data asks for starting the temperature test
	if (usart_recv() == 'T') {
		//confirm the request
		usart_send_str("Temperature test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			if (usart_recv() == 'a') {
				HIGH(ERROR_TEMPERATURE);
				OUTPUT(ERROR_TEMPERATURE);
			}

			if (usart_recv() == 'b') {
				LOW(ERROR_TEMPERATURE);
				OUTPUT(ERROR_TEMPERATURE);
			}

			if (usart_recv() == 'c') {
				TOGGLE(ERROR_TEMPERATURE);
				OUTPUT(ERROR_TEMPERATURE);
			}

			if (usart_recv() == 'd') {
				SET(ERROR_TEMPERATURE, 1);
				OUTPUT(ERROR_TEMPERATURE);
			}

			if (usart_recv() == 'e') {
				if (IS_SET(ERROR_TEMPERATURE))
					usart_send_str("yes\r\n");
			}
		}

		LOW(ERROR_TEMPERATURE);
		OUTPUT(ERROR_TEMPERATURE);
	}

	//if the received data asks for starting the battery test
	if (usart_recv() == 'B') {
		//confirm the request
		usart_send_str("Battery test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			if (usart_recv() == 'a') {
				HIGH(ERROR_BATTERY);
				OUTPUT(ERROR_BATTERY);
			}

			if (usart_recv() == 'b') {
				LOW(ERROR_BATTERY);
				OUTPUT(ERROR_BATTERY);
			}

			if (usart_recv() == 'c') {
				TOGGLE(ERROR_BATTERY);
				OUTPUT(ERROR_BATTERY);
			}

			if (usart_recv() == 'd') {
				SET(ERROR_BATTERY, 1);
				OUTPUT(ERROR_BATTERY);
			}

			if (usart_recv() == 'e') {
				if (IS_SET(ERROR_BATTERY))
					usart_send_str("yes\r\n");
			}
		}
		LOW(ERROR_BATTERY);
		OUTPUT(ERROR_BATTERY);
	}

	//if the received data asks for starting the oil pressure test
	if (usart_recv() == 'O') {
		//confirm the request
		usart_send_str("Oil level test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			if (usart_recv() == 'a') {
				HIGH(ERROR_OIL_LEVEL);
				OUTPUT(ERROR_OIL_LEVEL);
			}

			if (usart_recv() == 'b') {
				LOW(ERROR_OIL_LEVEL);
				OUTPUT(ERROR_OIL_LEVEL);
			}

			if (usart_recv() == 'c') {
				TOGGLE(ERROR_OIL_LEVEL);
				OUTPUT(ERROR_OIL_LEVEL);
			}

			if (usart_recv() == 'd') {
				SET(ERROR_OIL_LEVEL, 1);
				OUTPUT(ERROR_OIL_LEVEL);
			}

			if (usart_recv() == 'e') {
				if (IS_SET(ERROR_OIL_LEVEL))
					usart_send_str("yes\r\n");
			}
		}
		LOW(ERROR_OIL_LEVEL);
		OUTPUT(ERROR_OIL_LEVEL);
	}

	//if the received data asks for the first gear test
	if (usart_recv() == '1') {
		//confirm the request
		usart_send_str("First gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			if (usart_recv() == 'a') {
				HIGH(FIRST_GEAR);
				OUTPUT(FIRST_GEAR);
			}

			if (usart_recv() == 'b') {
				LOW(FIRST_GEAR);
				OUTPUT(FIRST_GEAR);
			}

			if (usart_recv() == 'c') {
				TOGGLE(FIRST_GEAR);
				OUTPUT(FIRST_GEAR);
			}

			if (usart_recv() == 'd') {
				SET(FIRST_GEAR, 1);
				OUTPUT(FIRST_GEAR);
			}

			if (usart_recv() == 'e') {
				if (IS_SET(FIRST_GEAR))
					usart_send_str("yes\r\n");
			}
		}
		LOW(FIRST_GEAR);
		OUTPUT(FIRST_GEAR);
	}

	//if the received data asks for the second gear test
	if (usart_recv() == '2') {
		//confirm the request
		usart_send_str("Second gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			if (usart_recv() == 'a') {
				HIGH(SECOND_GEAR);
				OUTPUT(SECOND_GEAR);
			}

			if (usart_recv() == 'b') {
				LOW(SECOND_GEAR);
				OUTPUT(SECOND_GEAR);
			}

			if (usart_recv() == 'c') {
				TOGGLE(SECOND_GEAR);
				OUTPUT(SECOND_GEAR);
			}

			if (usart_recv() == 'd') {
				SET(SECOND_GEAR, 1);
				OUTPUT(SECOND_GEAR);
			}

			if (usart_recv() == 'e') {
				if (IS_SET(SECOND_GEAR))
					usart_send_str("yes\r\n");
			}
		}
		LOW(SECOND_GEAR);
		OUTPUT(SECOND_GEAR);
	}

	//if the received data asks for the third gear test
	if (usart_recv() == '3') {
		//confirm the request
		usart_send_str("Third gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			if (usart_recv() == 'a') {
				HIGH(THIRD_GEAR);
				OUTPUT(THIRD_GEAR);
			}

			if (usart_recv() == 'b') {
				LOW(THIRD_GEAR);
				OUTPUT(THIRD_GEAR);
			}

			if (usart_recv() == 'c') {
				TOGGLE(THIRD_GEAR);
				OUTPUT(THIRD_GEAR);
			}

			if (usart_recv() == 'd') {
				SET(THIRD_GEAR, 1);
				OUTPUT(THIRD_GEAR);
			}

			if (usart_recv() == 'e') {
				if (IS_SET(THIRD_GEAR))
					usart_send_str("yes\r\n");
			}
		}
		LOW(THIRD_GEAR);
		OUTPUT(THIRD_GEAR);
	}

	//if the received data asks for the fourth gear test
	if (usart_recv() == '4') {
		//confirm the request
		usart_send_str("Fourth gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			if (usart_recv() == 'a') {
				HIGH(FOURTH_GEAR);
				OUTPUT(FOURTH_GEAR);
			}

			if (usart_recv() == 'b') {
				LOW(FOURTH_GEAR);
				OUTPUT(FOURTH_GEAR);
			}

			if (usart_recv() == 'c') {
				TOGGLE(FOURTH_GEAR);
				OUTPUT(FOURTH_GEAR);
			}

			if (usart_recv() == 'd') {
				SET(FOURTH_GEAR, 1);
				OUTPUT(FOURTH_GEAR);
			}

			if (usart_recv() == 'e') {
				if (IS_SET(FOURTH_GEAR))
					usart_send_str("yes\r\n");
			}
		}
		LOW(FOURTH_GEAR);
		OUTPUT(FOURTH_GEAR);
	}

	//if the received data asks for the fifth gear test
	if (usart_recv() == '5') {
		//confirm the request
		usart_send_str("Fifth gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			if (usart_recv() == 'a') {
				HIGH(FIFTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			if (usart_recv() == 'b') {
				LOW(FIFTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			if (usart_recv() == 'c') {
				TOGGLE(FIFTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			if (usart_recv() == 'd') {
				SET(FIFTH_GEAR, 1);
				OUTPUT(FIFTH_GEAR);
			}

			if (usart_recv() == 'e') {
				if (IS_SET(FIFTH_GEAR))
					usart_send_str("yes\r\n");
			}
		}
		LOW(FIFTH_GEAR);
		OUTPUT(FIFTH_GEAR);
	}
}
