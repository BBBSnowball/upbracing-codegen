#include<avr/wdt.h>

int main() {

	usart_init();

	//if the received data asks for starting the temperature test
	if (usart_recv() == 'T') {
		//confirm the request
		usart_send_str("Temperature test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			pinTests(ERROR_TEMPERATURE);
		}
	}

	//if the received data asks for starting the battery test
	if (usart_recv() == 'B') {
		//confirm the request
		usart_send_str("Battery test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			pinTests(ERROR_BATTERY);
		}
	}

	//if the received data asks for starting the oil pressure test
	if (usart_recv() == 'O') {
		//confirm the request
		usart_send_str("Oil level test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			pinTests(ERROR_OIL_PRESSURE);
		}
	}

	//if the received data asks for the first gear test
	if (usart_recv() == '1') {
		//confirm the request
		usart_send_str("First gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			pinTests(FIRST_GEAR);
		}
	}

	//if the received data asks for the second gear test
	if (usart_recv() == '2') {
		//confirm the request
		usart_send_str("Second gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			pinTests(SECOND_GEAR);
		}
	}

	//if the received data asks for the third gear test
	if (usart_recv() == '3') {
		//confirm the request
		usart_send_str("Third gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			pinTests(THIRD_GEAR);
		}
	}

	//if the received data asks for the fourth gear test
	if (usart_recv() == '4') {
		//confirm the request
		usart_send_str("Fourth gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			pinTests(FOURTH_GEAR);
		}
	}

	//if the received data asks for the fifth gear test
	if (usart_recv() == '5') {
		//confirm the request
		usart_send_str("Fifth gear test started\r\n");

		//begin the test
		while (usart_recv() != '0') {
			pinTests(FIFTH_GEAR);
		}
	}
}

void pinTests(String name) {
	if (usart_recv() == 'a') {
		OUTPUT(name);
		HIGH(name);
	}

	if (usart_recv() == 'b') {
		OUTPUT(name);
		LOW(name);
	}

	if (usart_recv() == 'c') {
		OUTPUT(name);
		TOGGLE(name);
	}

	if (usart_recv() == 'd') {
		OUTPUT(name);
		SET(name, 1);
	}

	if (usart_recv() == 'e') {
		OUTPUT(name);
		IS_SET(name);
	}
}
