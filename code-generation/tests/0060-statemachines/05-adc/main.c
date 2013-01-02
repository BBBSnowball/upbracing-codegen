#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <avr/pgmspace.h>

#include "rs232.h"
#include "rs232-helpers.h"
#include "adc.h"


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

typedef enum {
	NO_ADC,
	BLOCKING_ADC,
	BLOCKING_ADC_ONCE,
	STATEMACHINE_ONCE,
	STATEMACHINE_CONTINUOUS,
} mode_t;

volatile mode_t send_adc = NO_ADC;

int main(void) {
	usart_init();

	usart_send_str_P(PSTR("\r\n\r\nADC test\r\n"));

	sei();

	uint8_t channel = 2;
	while (1) {
		if (usart_recv_char_available()) {
			uint8_t c = usart_recv();
			switch (c) {
				case 'e':	// enable continuous conversions in main loop
					// disable interrupts
					ADCSRA &= ~(1 << ADIE);

					send_adc = BLOCKING_ADC;
					break;
				case 'd':	// disable conversions
					send_adc = NO_ADC;
					break;
				case 'v':	// select channel 2 (VCC)
					channel = 2;
					break;
				case 'r':	// select 1.1V reference voltage
					channel = 0b11110;
					break;
				case 'g':	// select 0V reference voltage
					channel = 0b11111;
					break;
				case 'o':	// do one conversion in main loop
					send_adc = BLOCKING_ADC_ONCE;
					break;
				case 's':	// do one conversion with the statemachine
					send_adc = STATEMACHINE_ONCE;
					adc_start(channel);
					break;
				case 'S':	// do many conversion with the statemachine
					send_adc = STATEMACHINE_CONTINUOUS;
					adc_start(channel);
					break;
				case 'p':	// ping
					usart_send_str_P(PSTR("pong\r\n"));
					break;
				case ' ':	// ignore (used for timing)
					break;
				default:
					if ('0' <= c && c <= '7')
						// select channel according to send char
						// ('2' -> select channel 2)
						channel = c - '0';
					else
						// I don't understand the input :-(
						usart_send_str_P(PSTR("???\r\n"));
					break;
			}
		}

		// do a blocking conversion, if that is requested
		if (send_adc == BLOCKING_ADC || send_adc == BLOCKING_ADC_ONCE) {
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

			if (send_adc == BLOCKING_ADC_ONCE)
				// we only want one -> disable now
				send_adc = NO_ADC;
			else
				// don't send too many values
				_delay_ms(200);
		}
	}
}

void on_adc_finished(uint8_t channel, uint16_t value) {
	usart_send_str("S ");
	usart_send_number(value, 16, 4);
	usart_send_str(" (");
	usart_send_number(channel, 16, 2);
	usart_send_str(")\r\n");

	if (send_adc == STATEMACHINE_CONTINUOUS)
		adc_restart();
}
