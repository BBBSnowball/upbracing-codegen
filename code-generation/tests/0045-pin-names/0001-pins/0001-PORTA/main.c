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
			//if received char is 'a' subtest for high temperature
			if (usart_recv() == 'a') {
				HIGH(ERROR_TEMPERATURE);
				OUTPUT(ERROR_TEMPERATURE);
			}

			//if received char is 'b' subtest for low temperature
			if (usart_recv() == 'b') {
				LOW(ERROR_TEMPERATURE);
				OUTPUT(ERROR_TEMPERATURE);
			}

			//if received char is 'c' subtest for toggle temperature
			if (usart_recv() == 'c') {
				TOGGLE(ERROR_TEMPERATURE);
				OUTPUT(ERROR_TEMPERATURE);
			}

			//if received char is 'd' subtest for set temperature
			if (usart_recv() == 'd') {
				SET(ERROR_TEMPERATURE, 1);
				OUTPUT(ERROR_TEMPERATURE);
			}

			//if received char is 'e' subtest for is_set temperature
			if (usart_recv() == 'e') {
				if (IS_SET(ERROR_TEMPERATURE))
					usart_send_str("yes\r\n");
			}
		}

		//turn off the led in case it is on
		LOW(ERROR_TEMPERATURE);
		OUTPUT(ERROR_TEMPERATURE);
	}

	//if the received data asks for starting the battery test
	if (usart_recv() == 'B') {
		//confirm the request
		usart_send_str("Battery test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			//if received char is 'a' subtest for high battery
			if (usart_recv() == 'a') {
				HIGH(ERROR_BATTERY);
				OUTPUT(ERROR_BATTERY);
			}

			//if received char is 'b' subtest for low battery
			if (usart_recv() == 'b') {
				LOW(ERROR_BATTERY);
				OUTPUT(ERROR_BATTERY);
			}

			//if received char is 'c' subtest for toggle battery
			if (usart_recv() == 'c') {
				TOGGLE(ERROR_BATTERY);
				OUTPUT(ERROR_BATTERY);
			}

			//if received char is 'd' subtest for set battery
			if (usart_recv() == 'd') {
				SET(ERROR_BATTERY, 1);
				OUTPUT(ERROR_BATTERY);
			}

			//if received char is 'e' subtest for is_set battery
			if (usart_recv() == 'e') {
				if (IS_SET(ERROR_BATTERY))
					usart_send_str("yes\r\n");
			}
		}

		//turn off the led in case it is on
		LOW(ERROR_BATTERY);
		OUTPUT(ERROR_BATTERY);
	}

	//if the received data asks for starting the oil pressure test
	if (usart_recv() == 'O') {
		//confirm the request
		usart_send_str("Oil level test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			//if received char is 'a' subtest for high oil
			if (usart_recv() == 'a') {
				HIGH(ERROR_OIL_LEVEL);
				OUTPUT(ERROR_OIL_LEVEL);
			}

			//if received char is 'b' subtest for low oil
			if (usart_recv() == 'b') {
				LOW(ERROR_OIL_LEVEL);
				OUTPUT(ERROR_OIL_LEVEL);
			}

			//if received char is 'c' subtest for toggle oil
			if (usart_recv() == 'c') {
				TOGGLE(ERROR_OIL_LEVEL);
				OUTPUT(ERROR_OIL_LEVEL);
			}

			//if received char is 'd' subtest for set oil
			if (usart_recv() == 'd') {
				SET(ERROR_OIL_LEVEL, 1);
				OUTPUT(ERROR_OIL_LEVEL);
			}

			//if received char is 'e' subtest for is_set oil
			if (usart_recv() == 'e') {
				if (IS_SET(ERROR_OIL_LEVEL))
					usart_send_str("yes\r\n");
			}
		}

		//turn off the led in case it is on
		LOW(ERROR_OIL_LEVEL);
		OUTPUT(ERROR_OIL_LEVEL);
	}

	//if the received data asks for the first gear test
	if (usart_recv() == '1') {
		//confirm the request
		usart_send_str("First gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			//if received char is 'a' subtest for high first gear
			if (usart_recv() == 'a') {
				HIGH(FIRST_GEAR);
				OUTPUT(FIRST_GEAR);
			}

			//if received char is 'b' subtest for low first gear
			if (usart_recv() == 'b') {
				LOW(FIRST_GEAR);
				OUTPUT(FIRST_GEAR);
			}

			//if received char is 'c' subtest for toggle first gear
			if (usart_recv() == 'c') {
				TOGGLE(FIRST_GEAR);
				OUTPUT(FIRST_GEAR);
			}

			//if received char is 'd' subtest for set first gear
			if (usart_recv() == 'd') {
				SET(FIRST_GEAR, 1);
				OUTPUT(FIRST_GEAR);
			}

			//if received char is 'e' subtest for is_set first gear
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
			//if received char is 'a' subtest for high second gear
			if (usart_recv() == 'a') {
				HIGH(SECOND_GEAR);
				OUTPUT(SECOND_GEAR);
			}

			//if received char is 'b' subtest for low second gear
			if (usart_recv() == 'b') {
				LOW(SECOND_GEAR);
				OUTPUT(SECOND_GEAR);
			}

			//if received char is 'c' subtest for toggle second gear
			if (usart_recv() == 'c') {
				TOGGLE(SECOND_GEAR);
				OUTPUT(SECOND_GEAR);
			}

			//if received char is 'd' subtest for set second gear
			if (usart_recv() == 'd') {
				SET(SECOND_GEAR, 1);
				OUTPUT(SECOND_GEAR);
			}

			//if received char is 'e' subtest for is_set second gear
			if (usart_recv() == 'e') {
				if (IS_SET(SECOND_GEAR))
					usart_send_str("yes\r\n");
			}
		}

		//turn off the LED in case it is on
		LOW(SECOND_GEAR);
		OUTPUT(SECOND_GEAR);
	}

	//if the received data asks for the third gear test
	if (usart_recv() == '3') {
		//confirm the request
		usart_send_str("Third gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			//if received char is 'a' subtest for high third gear
			if (usart_recv() == 'a') {
				HIGH(THIRD_GEAR);
				OUTPUT(THIRD_GEAR);
			}

			//if received char is 'b' subtest for low third gear
			if (usart_recv() == 'b') {
				LOW(THIRD_GEAR);
				OUTPUT(THIRD_GEAR);
			}

			//if received char is 'c' subtest for toggle third gear
			if (usart_recv() == 'c') {
				TOGGLE(THIRD_GEAR);
				OUTPUT(THIRD_GEAR);
			}

			//if received char is 'd' subtest for set third gear
			if (usart_recv() == 'd') {
				SET(THIRD_GEAR, 1);
				OUTPUT(THIRD_GEAR);
			}

			//if received char is 'e' subtest for is_set third gear
			if (usart_recv() == 'e') {
				if (IS_SET(THIRD_GEAR))
					usart_send_str("yes\r\n");
			}
		}

		//turn off the led in case it is on
		LOW(THIRD_GEAR);
		OUTPUT(THIRD_GEAR);
	}

	//if the received data asks for the fourth gear test
	if (usart_recv() == '4') {
		//confirm the request
		usart_send_str("Fourth gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			//if received char is 'a' subtest for high fourth gear
			if (usart_recv() == 'a') {
				HIGH(FOURTH_GEAR);
				OUTPUT(FOURTH_GEAR);
			}

			//if received char is 'b' subtest for low fourth gear
			if (usart_recv() == 'b') {
				LOW(FOURTH_GEAR);
				OUTPUT(FOURTH_GEAR);
			}

			//if received char is 'c' subtest for toggle fourth gear
			if (usart_recv() == 'c') {
				TOGGLE(FOURTH_GEAR);
				OUTPUT(FOURTH_GEAR);
			}

			//if received char is 'd' subtest for set fourth gear
			if (usart_recv() == 'd') {
				SET(FOURTH_GEAR, 1);
				OUTPUT(FOURTH_GEAR);
			}

			//if received char is 'e' subtest for is_set fourth gear
			if (usart_recv() == 'e') {
				if (IS_SET(FOURTH_GEAR))
					usart_send_str("yes\r\n");
			}
		}

		//turn off the led in case it is on
		LOW(FOURTH_GEAR);
		OUTPUT(FOURTH_GEAR);
	}

	//if the received data asks for the fifth gear test
	if (usart_recv() == '5') {
		//confirm the request
		usart_send_str("Fifth gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			//if received char is 'a' subtest for high fifth gear
			if (usart_recv() == 'a') {
				HIGH(FIFTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			//if received char is 'b' subtest for low fifth gear
			if (usart_recv() == 'b') {
				LOW(FIFTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			//if received char is 'c' subtest for toggle fifth gear
			if (usart_recv() == 'c') {
				TOGGLE(FIFTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			//if received char is 'd' subtest for set fifth gear
			if (usart_recv() == 'd') {
				SET(FIFTH_GEAR, 1);
				OUTPUT(FIFTH_GEAR);
			}

			//if received char is 'e' subtest for is_set fifth gear
			if (usart_recv() == 'e') {
				if (IS_SET(FIFTH_GEAR))
					usart_send_str("yes\r\n");
			}
		}

		//turn off the led in case it is on
		LOW(FIFTH_GEAR);
		OUTPUT(FIFTH_GEAR);
	}
}
