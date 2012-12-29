#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <avr/pgmspace.h>

#include "rs232.h"
#include "rs232-helpers.h"


uint16_t getADC(uint8_t channel) {
	cli();

	uint16_t result;

	// select channel and internal reference (2.56V)
	ADMUX = channel  |  (1 << REFS1) | (1 << REFS0);

	// enable ADC
	// prescaler: 8Mhz / 64 = 125kHz
	ADCSRA = (1 << ADEN) | (1 << ADPS2) | (1 << ADPS1) | (0 << ADPS0);

	// Start dummy readout
	ADCSRA |= (1 << ADSC);
	// wait for it to finish
	while (ADCSRA & (1 << ADSC)) {}
	// read result to reset ADC
	result = ADCW;

	// start measurement (we measure 5 times)
	result = 0;
	uint8_t i;
	for (i = 0; i < 5; i++) {
		// start measurement
		ADCSRA |= (1 << ADSC);
		// wait for it to finish
		while (ADCSRA & (1 << ADSC)) {}
		// accumulate results
		result += ADCW;
	}

	// disable ADC
	ADCSRA &= ~(1 << ADEN);

	// calculate average
	result /= 5;

	sei();
	
	return result;
}

int main(void) {
	usart_init();

	usart_send_str_P(PSTR("\r\n\r\nADC test\r\n"));

	uint8_t send_adc = 0;
	uint8_t channel = 2;
	while (1) {
		if (usart_recv_char_available()) {
			uint8_t c = usart_recv();
			switch (c) {
				case 'e':
					send_adc = 1;
					break;
				case 'd':
					send_adc = 0;
					break;
				case 'v':
					channel = 2;
					break;
				case 'r':	// 1.1V reference voltage
					channel = 0b11110;
					break;
				case 'g':	// 0V reference voltage
					channel = 0b11111;
					break;
				default:
					if ('0' <= c && c <= '7')
						channel = c - '0';
					else
						usart_send_str_P(PSTR("???\r\n"));
					break;
			}
		}

		if (send_adc) {
			uint16_t x = getADC(channel);

			usart_send_number(x, 16, 4); usart_send_str("\r\n");

			if (channel == 2) {	// VCC
				// 1024 <=> 2.56V
				// factor 5 due to voltage divider
				// VCC = x * 5 * 256/1024
				x = x * 5 / (1024/256);
				usart_send_number(x, 10, 4); usart_send_str("\r\n");
			} else if (channel >= 0b11110) {	// GND or 1.1V reference
				// 1024 <=> 2.56V
				// VCC = x * 256/1024
				x = x / (1024/256);
				usart_send_number(x, 10, 4); usart_send_str("\r\n");
			}

			_delay_ms(200);
		}
	}
}
