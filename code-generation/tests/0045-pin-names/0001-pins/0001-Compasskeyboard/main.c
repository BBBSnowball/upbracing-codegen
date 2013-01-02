#include<avr/wdt.h>
#include "rs232.h"
#include "rs232-helpers.h"
#include "gen/pins.h"
#include<Pins.h>
#include<avr/pgmspace.h>

int main() {
	usart_init();

	char ch = usart_recv();

#define REGISTER_DATA(register_name, number, base, min_places)	\
		usart_send_str_P(PSTR(register_name " :"));			\
		usart_send_number(number, base, min_places);        \
		usart_send_str_P(PSTR("\r\n"));

	if (ch == 'L') {
		//confirm the request
		usart_send_str("Compass keyboard test started\r\n");

		//begin the test
		char compass = usart_recv();
		while (compass != '0') {
			if (compass == 'a') {
				HIGH(LOW_FUEL);
				HIGH(ENGINE_FAILURE);
				HIGH(HEADLIGHT_NOT_WORKING);
				LOW(FAULTY_EXHAUST);
				LOW(HIGH_TEMP);
				LOW(CHANGE_GEAR);

				//if (PORTE == 0x34 && PORTD == 0x01) test passes
				REGISTER_DATA("PORTE", PORTE, 16, 2);
				REGISTER_DATA("PORTD", PORTD, 16, 2);
			}

			if (compass == 'b') {
				LOW(LOW_FUEL);
				LOW(ENGINE_FAILURE);
				LOW(HEADLIGHT_NOT_WORKING);
				HIGH(FAULTY_EXHAUST);
				HIGH(HIGH_TEMP);
				HIGH(CHANGE_GEAR);

				//if (PORTE == 0xc0 && PORTD == 0x00) test passes
				REGISTER_DATA("PORTE", PORTE, 16, 2);
				REGISTER_DATA("PORTD", PORTD, 16, 2);
			}

			if (compass == 'c') {
				TOGGLE(LOW_FUEL);
				TOGGLE(ENGINE_FAILURE);
				TOGGLE(HEADLIGHT_NOT_WORKING);
				TOGGLE(FAULTY_EXHAUST);
				TOGGLE(HIGH_TEMP);
				TOGGLE(CHANGE_GEAR);

				//if (PORTE == 0x34 && PORTD == 0x01) test passes
				REGISTER_DATA("PORTE", PORTE, 16, 2);
				REGISTER_DATA("PORTD", PORTD, 16, 2);
			}

			if (compass == 'd') {
				INPUT(LOW_FUEL);
				INPUT(ENGINE_FAILURE);
				INPUT(HEADLIGHT_NOT_WORKING);
				INPUT(FAULTY_EXHAUST);
				INPUT(HIGH_TEMP);
				INPUT(CHANGE_GEAR);

				//if (DDRE == 0x00 && DDRD == 0x00) test passes
				REGISTER_DATA("DDRE", DDRE, 16, 2);
				REGISTER_DATA("DDRD", DDRD, 16, 2);
			}

			if (compass == 'e') {
				PULLUP(LOW_FUEL);
				PULLUP(ENGINE_FAILURE);
				PULLUP(HEADLIGHT_NOT_WORKING);
				PULLUP(FAULTY_EXHAUST);
				PULLUP(HIGH_TEMP);
				PULLUP(CHANGE_GEAR);

				//if (PORTE == 0xf4 && PORTD == 0x01) test passes
				REGISTER_DATA("PORTE", PORTE, 16, 2);
				REGISTER_DATA("PORTD", PORTD, 16, 2);
			}

			if (compass == 'f') {
				NO_PULLUP(LOW_FUEL);
				NO_PULLUP(ENGINE_FAILURE);
				NO_PULLUP(HEADLIGHT_NOT_WORKING);
				NO_PULLUP(FAULTY_EXHAUST);
				NO_PULLUP(HIGH_TEMP);
				NO_PULLUP(CHANGE_GEAR);

				//if (PORTE == 0x00 && PORTD == 0x00) test passes
				REGISTER_DATA("PORTE", PORTE, 16, 2);
				REGISTER_DATA("PORTD", PORTD, 16, 2);
			}

			if (compass == 'g') {
				SET(LOW_FUEL, 1);
				SET(ENGINE_FAILURE, 1);
				SET(HEADLIGHT_NOT_WORKING, 1);
				SET(FAULTY_EXHAUST, 1);
				SET(HIGH_TEMP, 1);
				SET(CHANGE_GEAR, 1);

				//if (PORTE == 0xf4 && PORTD == 0x01) test passes
				REGISTER_DATA("PORTE", PORTE, 16, 2);
				REGISTER_DATA("PORTD", PORTD, 16, 2);
			}

			if (compass == 'h') {
				SET(LOW_FUEL, 0);
				SET(ENGINE_FAILURE, 0);
				SET(HEADLIGHT_NOT_WORKING, 0);
				SET(FAULTY_EXHAUST, 0);
				SET(HIGH_TEMP, 0);
				SET(CHANGE_GEAR, 0);

				//if (PORTE == 0x00 && PORTD == 0x00) test passes
				REGISTER_DATA("PORTE", PORTE, 16, 2);
				REGISTER_DATA("PORTD", PORTD, 16, 2);
			}

			if (compass == 'i') {
				DDRE = 0x00; //set compass board pins as input
				DDRD = 0x00;
				PORTE = 0xf4; //enable pull ups for
				PORTD = 0x00; //compass keyboard

				char is_set = usart_recv();

				while (is_set != '0') {
					if (is_set == 'a') {
						if (!IS_SET(LOW_FUEL))
							usart_send_str("\r\n");
					}

					if (is_set == 'b') {
						if (IS_SET(LOW_FUEL))
							usart_send_str("\r\n");
					}

					if (is_set == 'c') {
						if ((!IS_SET(ENGINE_FAILURE))
								&& (!IS_SET(HEADLIGHT_NOT_WORKING))
								&& (!IS_SET(FAULTY_EXHAUST)) && (!IS_SET(HIGH_TEMP))
								&& (!IS_SET(CHANGE_GEAR)))

							usart_send_str("\r\n");
					}

					is_set = usart_recv();
				}
			}
			compass = usart_recv();
		}
	} else
		usart_send_str("Unexpected character.\r\n");
}
