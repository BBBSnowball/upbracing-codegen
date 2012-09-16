/*
 * statemachine-test-AVR.c
 *
 *  Created on: Sep 16, 2012
 *      Author: benny
 */

#include <avr/io.h>
#include <util/delay.h>
#include <avr/pgmspace.h>

#include "rs232.h"

#include "gen/statemachines.h"

void usart_send_number(int32_t number, uint8_t base, uint8_t min_places) {
	char chars[32 + 2 + 1];
	char* x = chars + sizeof(chars) / sizeof(*chars);
	*(--x) = 0;

	// special case for zero which would otherwise yield an empty string
	if (number == 0)
		*(--x) = '0';

	while (number || min_places) {
		uint8_t digit = number % base;
		if (digit < 10)
			digit += '0';
		else
			digit += 'a';
		*(--x) = digit;

		number /= base;
		if (min_places)
			--min_places;
	}

	switch (base) {
	case  2:	usart_send('0'); usart_send('b'); break;
	case  8:	usart_send('0');                  break;
	case 10:	                                  break;
	case 16:	usart_send('0'); usart_send('x'); break;
	default:
		usart_send_number(base, 10, 0);
		usart_send('#');
		break;
	}

	usart_send_str(x);
}

inline static void usart_send_number_binary(uint32_t number) {
	usart_send_number(number, 2, 0);
}


// hardware on the DVK90CAN1 eval board

// LEDs on port A, non-inverting (page 26)

// buttons on port E (page 25)
// (CENTER jumper in position 1-2)
#define BUTTON_MASK 0xf4
#define BUTTON_CENTER (1<<2)
#define BUTTON_NORTH  (1<<4)
#define BUTTON_EAST   (1<<5)
#define BUTTON_WEST   (1<<6)
#define BUTTON_SOUTH  (1<<7)

int main(void) {
	DDRA = 0xff;
	PORTA = 42;

	// switches
	DDRE  &= ~0xf4;
	PORTE |=  0xf4;
	//_delay_us(100);
	uint8_t switch_state = PINE & 0xf4;

	usart_init();
	usart_send_str("blub\n");
	usart_send_str_P(PSTR("blubP\n"));

	counter_init();
	usart_send('I');
	while (1) {
		counter_tick();

		uint8_t switch_state2 = PINE & 0xf4;
		uint8_t switch_events = switch_state ^ switch_state2;
		if (switch_events) {
			switch_state = switch_state2;

			usart_send_str("switches: changed = ");
			usart_send_number_binary(switch_events);
			usart_send_str(", new state = ");
			usart_send_number_binary(switch_state);
			usart_send_str("\n");

			// center switch pressed
			if ((switch_events & BUTTON_CENTER) && (switch_state & BUTTON_CENTER) == 0)
				counter_event_startstop_pressed();
			if ((switch_events & BUTTON_SOUTH) && (switch_state & BUTTON_SOUTH) == 0)
				counter_event_reset();
		}

		//TODO we should use a timer because with a delay we get a
		//      period of (1ms + execution time of counter_tick())
		//      instead of 1ms
		_delay_ms(1);
	}
}
