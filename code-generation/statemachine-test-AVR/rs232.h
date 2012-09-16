/*
 * rs232.h
 *
 *  Created on: Sep 16, 2012
 *      Author: benny
 */

#ifndef RS232_H_
#define RS232_H_

#include <avr/io.h>

void usart_init(void);

inline static void usart_send(uint8_t data) {
	while (!(UCSR0A & (1<<UDRE0)))
		;
	UDR0 = data;
}

inline static uint8_t usart_recv(void) {
	while (!(UCSR0A & (1<<RXC0)))
		;
	return UDR0;
}

void usart_send_str(const char* s);

void usart_send_str_P(const char* s);

uint8_t usart_wait_byte_timeout(uint32_t timeout_loops);

#endif /* RS232_H_ */
