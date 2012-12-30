#include<avr/wdt.h>
#include<rs232.h>
#include "gen/pins.h"
#include<Pins.h>

int main() {
	usart_init();
	DDRA = 0xff; //set PORTA as output

	char ch = usart_recv();

	if (ch == 'L') {
		//confirm the request
		usart_send_str("Compass keyboard test started\r\n");

		//begin the test
		char compass = usart_recv();
		while (compass != '0') {
			if (compass == 'a') {
				PORTA = 0x00; //turn all leds off
				HIGH(LOW_FUEL);
				HIGH(ENGINE_FAILURE);
				HIGH(HEADLIGHT_NOT_WORKING);
				LOW(FAULTY_EXHAUST);
				LOW(HIGH_TEMP);
				LOW(CHANGE_GEAR);

				//display -------X pattern if test passes
				if (PORTE == 0x34 && PORTD == 0x01)
					PORTA = 0x01;
			}

			if (compass == 'b') {
				PORTA = 0x00;
				LOW(LOW_FUEL);
				LOW(ENGINE_FAILURE);
				LOW(HEADLIGHT_NOT_WORKING);
				HIGH(FAULTY_EXHAUST);
				HIGH(HIGH_TEMP);
				HIGH(CHANGE_GEAR);

				//display ------XX pattern if test passes
				if (PORTE == 0xc0 && PORTD == 0x00)
					PORTA = 0x03;
			}

			if (compass == 'c') {
				PORTA = 0x00;
				TOGGLE(LOW_FUEL);
				TOGGLE(ENGINE_FAILURE);
				TOGGLE(HEADLIGHT_NOT_WORKING);
				TOGGLE(FAULTY_EXHAUST);
				TOGGLE(HIGH_TEMP);
				TOGGLE(CHANGE_GEAR);

				//display ------XXX pattern if test passes
				if (PORTE == 0x34 && PORTD == 0x01)
					PORTA = 0x07;
			}

			if (compass == 'd') {
				PORTA = 0x00;
				INPUT(LOW_FUEL);
				INPUT(ENGINE_FAILURE);
				INPUT(HEADLIGHT_NOT_WORKING);
				INPUT(FAULTY_EXHAUST);
				INPUT(HIGH_TEMP);
				INPUT(CHANGE_GEAR);

				//display ---XXXXX pattern if test passes
				if (DDRE == 0x00 && DDRD == 0x00)
					PORTA = 0x1f;
			}

			if (compass == 'e') {
				PORTA = 0x00;
				PULLUP(LOW_FUEL);
				PULLUP(ENGINE_FAILURE);
				PULLUP(HEADLIGHT_NOT_WORKING);
				PULLUP(FAULTY_EXHAUST);
				PULLUP(HIGH_TEMP);
				PULLUP(CHANGE_GEAR);

				//display --XXXXXX pattern if the test passes
				if (PORTE == 0xf4 && PORTD == 0x01)
					PORTA = 0x3f;
			}

			if (compass == 'f') {
				PORTA = 0x00;
				NO_PULLUP(LOW_FUEL);
				NO_PULLUP(ENGINE_FAILURE);
				NO_PULLUP(HEADLIGHT_NOT_WORKING);
				NO_PULLUP(FAULTY_EXHAUST);
				NO_PULLUP(HIGH_TEMP);
				NO_PULLUP(CHANGE_GEAR);

				//display -XXXXXXX pattern if the test passes
				if (PORTE == 0x00 && PORTD == 0x00)
					PORTA = 0x7f;
			}

			if (compass == 'g') {
				PORTA = 0x00;
				SET(LOW_FUEL, 1);
				SET(ENGINE_FAILURE, 1);
				SET(HEADLIGHT_NOT_WORKING, 1);
				SET(FAULTY_EXHAUST, 1);
				SET(HIGH_TEMP, 1);
				SET(CHANGE_GEAR, 1);

				//display XXXXXXX pattern if the test passes
				if (PORTE == 0xf4 && PORTD == 0x01)
					PORTA = 0xff;
			}

			if (compass == 'h') {
				PORTA = 0x00;
				SET(LOW_FUEL, 0);
				SET(ENGINE_FAILURE, 0);
				SET(HEADLIGHT_NOT_WORKING, 0);
				SET(FAULTY_EXHAUST, 0);
				SET(HIGH_TEMP, 0);
				SET(CHANGE_GEAR, 0);

				//display ------X- pattern if the test passes
				if (PORTE == 0x00 && PORTD == 0x00)
					PORTA = 0x02;
			}

			//display -X-XXXXX pattern if the test passes
			if (compass == 'i') {
				PORTA = 0x00;
				if (!IS_SET(LOW_FUEL))
					HIGH(LED_1);
				if (!IS_SET(ENGINE_FAILURE))
					HIGH(LED_2);
				if (!IS_SET(HEADLIGHT_NOT_WORKING))
					HIGH(LED_3);
				if (!IS_SET(FAULTY_EXHAUST))
					HIGH(LED_4);
				if (!IS_SET(HIGH_TEMP))
					HIGH(LED_5);
				if (!IS_SET(CHANGE_GEAR))
					HIGH(LED_7);
			}
		}
	}
}
