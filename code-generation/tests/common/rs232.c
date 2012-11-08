/*
 * rs232.c
 *
 *  Created on: Sep 16, 2012
 *      Author: benny
 */

#include "rs232.h"
#include <avr/pgmspace.h>
#include <util/delay.h>

// UBRR = F_CPU/16/BAUD - 1
// 9600 Baud, 8MHz
#define UBRR_VALUE 51

void usart_init(void) {
	UBRRxH = (UBRR_VALUE >> 8);
	UBRRxL = (UBRR_VALUE & 0xff);
	// normal mode
	UCSRxA = 0;
	// set frame format 8N1
	UCSRxC = (3<<UCSZx);
	// enable RX and TX
	UCSRxB = (1<<RXENx) | (1<<TXENx);

	// The level at reset is sort of undefined and
	// after the reset it will be set to zero. We
	// need it at a high level for some time, so the
	// PC will get the first bit right.
	// Well, that doesn't solve the problem :-(
	_delay_us(1000);
}

void usart_send_str(const char* s) {
	while (*s) {
		usart_send(*s);
		s++;
	}
}

void usart_send_str_P(const char* s) {
	while (1) {
		char c = pgm_read_byte(s);
		if (!c)
			return;
		usart_send(c);
		++s;
	}
}

void usart_send_many(const char* s, uint8_t count) {
	for (;count>0;count--) {
		usart_send(*s);
		s++;
	}
}
