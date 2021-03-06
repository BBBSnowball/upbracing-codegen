/*
 * rs232.c
 *
 *  Created on: Sep 16, 2012
 *      Author: benny
 */

#include "rs232.h"
#include <avr/pgmspace.h>
#include <util/delay.h>

// If the functions are marked static with RS232_SPEC,
// the compiler print a warning for the ones that aren't
// used. We suppress this warning.
#ifdef __GNUC__
#	define PROBABLY_UNUSED __attribute__ ((unused))
#else
#	define PROBABLY_UNUSED
#endif

RS232_SPEC void usart_init(void) PROBABLY_UNUSED;
RS232_SPEC void usart_send_str(const char* s) PROBABLY_UNUSED;
RS232_SPEC void usart_send_str_P(const char* s) PROBABLY_UNUSED;
RS232_SPEC void usart_send_many(const char* s, uint8_t count) PROBABLY_UNUSED;

// UBRR = F_CPU/16/BAUD - 1
// 9600 Baud, 8MHz
//#define UBRR_VALUE 51

#define UBRR_VALUE ((F_CPU) / 16 / 9600 - 1)

RS232_SPEC void usart_init(void) {
	UBRRxH = (UBRR_VALUE >> 8);
	UBRRxL = (UBRR_VALUE & 0xff);
	// normal mode
	UCSRxA = 0;
	// set frame format 8N1
	UCSRxC = (1<<UCSZx0) | (1<<UCSZx1);
	// enable RX and TX
	UCSRxB = (1<<RXENx) | (1<<TXENx);
}

RS232_SPEC void usart_send_str(const char* s) {
	while (*s) {
		usart_send(*s);
		s++;
	}
}

RS232_SPEC void usart_send_str_P(const char* s) {
	while (1) {
		char c = pgm_read_byte(s);
		if (!c)
			return;
		usart_send(c);
		++s;
	}
}

RS232_SPEC void usart_send_many(const char* s, uint8_t count) {
	for (;count>0;count--) {
		usart_send(*s);
		s++;
	}
}
