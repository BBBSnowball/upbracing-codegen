#include<avr/wdt.h>

int main() {
	//receive data from the PC for request of the test of the first gear
	if (usart_recv() == "First_gear") {
		//confirm the request
		usart_send_str("First gear test started");

		//start the testing
		HIGH(FIRST_GEAR);
		OUTPUT(FIRST_GEAR);

		//turn off the led after the mini test has finished
		if (usart_recv() == "turn_off")
			LOW(FIRST_GEAR);
	}

	//receive data from the PC for request of the test of the second gear
	if (usart_recv() == "Second_gear") {
		//confirm the request
		usart_send_str("Second gear test started");

		//start the testing
		HIGH(SECOND_GEAR);
		OUTPUT(SECOND_GEAR);

		//turn off the led after the the mini test has finished
		if (usart_recv() == "turn_off")
			LOW(SECOND_GEAR);
	}

	//receive data from the PC for request of the test of the third gear
	if (usart_recv() == "Third_gear") {
		//confirm the request
		usart_send_str("Third gear test started");

		//start the testing
		HIGH(THIRD_GEAR);
		OUTPUT(THIRD_GEAR);

		//turn off the led after the mini test has ended
		if(usart_recv() == "turn_off")
			LOW(THIRD_GEAR);
	}

	//receive data from the PC for request of the test of the fourth gear
	if (usart_recv() == "Fourth_gear") {
		//confirm  the request
		usart_send_str("Fouth gear test started");

		//start the testing
		HIGH(FOURTH_GEAR);
		OUTPUT(FOURTH_GEAR);

		//turn off the led after the mini test has ended
		if (usart_recv() == "turn_off")
			LOW(FOURTH_GEAR);
	}
}
