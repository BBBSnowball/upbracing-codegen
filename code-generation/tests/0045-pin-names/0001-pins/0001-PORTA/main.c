#include<avr/wdt.h>
#include<rs232.h>
#include "gen/pins.h"
#include<Pins.h>

int main() {
	usart_init();

	char ch = usart_recv();

	//if the received data asks for starting the temperature test
	if (ch == 'L') {
		//confirm the request
		usart_send_str("LED pattern tests started\r\n");
		_delay_ms(100);

		//begin the test
		char led_pattern = usart_recv();
		while (led_pattern != '0') {
			//led pattern -----XXX
			if (led_pattern == 'a') {
				HIGH(ERROR_TEMPERATURE);
				HIGH(ERROR_BATTERY);
				HIGH(ERROR_OIL_LEVEL);
				LOW(FIRST_GEAR);
				LOW(SECOND_GEAR);
				LOW(THIRD_GEAR);
				LOW(FOURTH_GEAR);
				LOW(FIFTH_GEAR);
				OUTPUT(ERROR_TEMPERATURE);
				OUTPUT(ERROR_BATTERY);
				OUTPUT(ERROR_OIL_LEVEL);
				OUTPUT(FIRST_GEAR);
				OUTPUT(SECOND_GEAR);
				OUTPUT(THIRD_GEAR);
				OUTPUT(FOURTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			//blink led pattern XXXXX---
			if (led_pattern == 'b') {
				LOW(ERROR_TEMPERATURE);
				LOW(ERROR_BATTERY);
				LOW(ERROR_OIL_LEVEL);
				HIGH(FIRST_GEAR);
				HIGH(SECOND_GEAR);
				HIGH(THIRD_GEAR);
				HIGH(FOURTH_GEAR);
				HIGH(FIFTH_GEAR);
				OUTPUT(ERROR_TEMPERATURE);
				OUTPUT(ERROR_BATTERY);
				OUTPUT(ERROR_OIL_LEVEL);
				OUTPUT(FIRST_GEAR);
				OUTPUT(SECOND_GEAR);
				OUTPUT(THIRD_GEAR);
				OUTPUT(FOURTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			//blink led pattern XXXXXXXX
			if (led_pattern == 'c') {
				PORTA = 0x00;
				TOGGLE(ERROR_TEMPERATURE);
				TOGGLE(ERROR_BATTERY);
				TOGGLE(ERROR_OIL_LEVEL);
				TOGGLE(FIRST_GEAR);
				TOGGLE(SECOND_GEAR);
				TOGGLE(THIRD_GEAR);
				TOGGLE(FOURTH_GEAR);
				TOGGLE(FIFTH_GEAR);
				OUTPUT(ERROR_TEMPERATURE);
				OUTPUT(ERROR_BATTERY);
				OUTPUT(ERROR_OIL_LEVEL);
				OUTPUT(FIRST_GEAR);
				OUTPUT(SECOND_GEAR);
				OUTPUT(THIRD_GEAR);
				OUTPUT(FOURTH_GEAR);
				OUTPUT(FIFTH_GEAR);

			}

			//blink led pattern X-------
			if (led_pattern == 'd') {
				PORTA = 0x00;
				SET(ERROR_TEMPERATURE, 0);
				SET(ERROR_BATTERY, 0);
				SET(ERROR_OIL_LEVEL, 0);
				SET(FIRST_GEAR, 0);
				SET(SECOND_GEAR, 0);
				SET(THIRD_GEAR, 0);
				SET(FOURTH_GEAR, 0);
				SET(FIFTH_GEAR, 1);
				OUTPUT(ERROR_TEMPERATURE);
				OUTPUT(ERROR_BATTERY);
				OUTPUT(ERROR_OIL_LEVEL);
				OUTPUT(FIRST_GEAR);
				OUTPUT(SECOND_GEAR);
				OUTPUT(THIRD_GEAR);
				OUTPUT(FOURTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			//blink led pattern -XXXXXXX
			if (led_pattern == 'e') {
				SET(ERROR_TEMPERATURE, 1);
				SET(ERROR_BATTERY, 1);
				SET(ERROR_OIL_LEVEL, 1);
				SET(FIRST_GEAR, 1);
				SET(SECOND_GEAR, 1);
				SET(THIRD_GEAR, 1);
				SET(FOURTH_GEAR, 1);
				SET(FIFTH_GEAR, 0);
				OUTPUT(ERROR_TEMPERATURE);
				OUTPUT(ERROR_BATTERY);
				OUTPUT(ERROR_OIL_LEVEL);
				OUTPUT(FIRST_GEAR);
				OUTPUT(SECOND_GEAR);
				OUTPUT(THIRD_GEAR);
				OUTPUT(FOURTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			//blink led pattern XXXX----
			if (led_pattern == 'f') {
				PORTA = 0x00;
				NO_PULLUP(ERROR_TEMPERATURE);
				NO_PULLUP(ERROR_BATTERY);
				NO_PULLUP(ERROR_OIL_LEVEL);
				NO_PULLUP(FIRST_GEAR);
				PULLUP(SECOND_GEAR);
				PULLUP(THIRD_GEAR);
				PULLUP(FOURTH_GEAR);
				PULLUP(FIFTH_GEAR);
				OUTPUT(ERROR_TEMPERATURE);
				OUTPUT(ERROR_BATTERY);
				OUTPUT(ERROR_OIL_LEVEL);
				OUTPUT(FIRST_GEAR);
				OUTPUT(SECOND_GEAR);
				OUTPUT(THIRD_GEAR);
				OUTPUT(FOURTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			//blink led pattern ----XXXX
			if (led_pattern == 'g') {
				PULLUP(ERROR_TEMPERATURE);
				PULLUP(ERROR_BATTERY);
				PULLUP(ERROR_OIL_LEVEL);
				PULLUP(FIRST_GEAR);
				NO_PULLUP(SECOND_GEAR);
				NO_PULLUP(THIRD_GEAR);
				NO_PULLUP(FOURTH_GEAR);
				NO_PULLUP(FIFTH_GEAR);
				OUTPUT(ERROR_TEMPERATURE);
				OUTPUT(ERROR_BATTERY);
				OUTPUT(ERROR_OIL_LEVEL);
				OUTPUT(FIRST_GEAR);
				OUTPUT(SECOND_GEAR);
				OUTPUT(THIRD_GEAR);
				OUTPUT(FOURTH_GEAR);
				OUTPUT(FIFTH_GEAR);
			}

			//blink led pattern ------XX
			if (led_pattern == 'h') {
				if (IS_SET(ERROR_TEMPERATURE))
					OUTPUT(ERROR_TEMPERATURE);
				if (IS_SET(ERROR_BATTERY))
					OUTPUT(ERROR_BATTERY);
				if (IS_SET(ERROR_OIL_LEVEL)) {
					LOW(ERROR_OIL_LEVEL);
					OUTPUT(ERROR_OIL_LEVEL);
				}
				if (IS_SET(FIRST_GEAR)) {
					LOW(FIRST_GEAR);
					OUTPUT(FIRST_GEAR);
				}
				if (!IS_SET(SECOND_GEAR))
					OUTPUT(SECOND_GEAR);
				if (!IS_SET(THIRD_GEAR))
					OUTPUT(THIRD_GEAR);
				if (!IS_SET(FOURTH_GEAR))
					OUTPUT(FOURTH_GEAR);
				if (!IS_SET(FIFTH_GEAR))
					OUTPUT(FIFTH_GEAR);

			}
			led_pattern = usart_recv();
		}
	} else
		usart_send_str("Unexpected character\r\n");
}

