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
#include "rs232-helpers.h"


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
	// show some pattern on LEDs
	DDRA = 0xff;
	PORTA = 42;

	// buttons: input, pullup
	DDRE  &= ~0xf4;
	PORTE |=  0xf4;
	//_delay_us(100);
	uint8_t switch_state = PINE & 0xf4;

	usart_init();
	usart_send_str("USART test\r\n");
	usart_send_str_P(PSTR("string from program memory\r\n"));

	usart_send_str_P(PSTR("testing usart_send_number:\r\n"));

#define TEST_NUMBER(number, base, min_places)			\
		usart_send_str_P(PSTR(#number " -> "));			\
		usart_send_number(number, base, min_places);	\
		usart_send_str_P(PSTR("\r\n"));
#define TEST_NUMBER2(result, number, base, min_places)	\
		usart_send_str_P(PSTR(result " -> "));			\
		usart_send_number(number, base, min_places);	\
		usart_send_str_P(PSTR("\r\n"));
#define TEST_BINARY(number)								\
		usart_send_str_P(PSTR(#number " -> "));			\
		usart_send_number_binary(number);				\
		usart_send_str_P(PSTR("\r\n"));

	usart_send_str_P(PSTR("0b11001010 -> "));
	usart_send_number_binary(0b11001010);
	usart_send_str_P(PSTR("\r\n"));

	usart_send_str_P(PSTR("0b1011 -> "));
	usart_send_number_binary(0b1011);
	usart_send_str_P(PSTR("\r\n"));

	usart_send_str_P(PSTR("0b101100001111 -> "));
	usart_send_number_binary(0b101100001111);
	usart_send_str_P(PSTR("\r\n"));

	usart_send_str_P(PSTR("42 -> "));
	usart_send_number(42, 10, 0);
	usart_send_str_P(PSTR("\r\n"));

	usart_send_str_P(PSTR("0 -> "));
	usart_send_number(0, 10, 0);
	usart_send_str_P(PSTR("\r\n"));

	TEST_NUMBER(-10, 10, 0);
	TEST_NUMBER(0xf123a, 16, 0);
	TEST_NUMBER(01234567, 8, 0);

	TEST_NUMBER(-10, 10, 2);
	TEST_NUMBER(0xf123a, 16, 5);
	TEST_NUMBER(01234567, 8, 7);

	TEST_NUMBER2("-  10", -10, 10, 4);
	TEST_NUMBER(0x0f123a, 16, 6);
	TEST_NUMBER(0001234567, 8, 9);

	TEST_NUMBER2("36#az", 10*36 + 35, 36, 0);
	TEST_NUMBER2("36#az", 10*36 + 35, 36, 2);
	TEST_NUMBER2("36#0az", 10*36 + 35, 36, 3);

	usart_send_str_P(PSTR("done\r\n\r\n"));
}
