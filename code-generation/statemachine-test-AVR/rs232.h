/*
 * rs232.h
 *
 *  Created on: Sep 16, 2012
 *      Author: benny
 */

#ifndef RS232_H_
#define RS232_H_

#include <avr/io.h>

// use usart 1 because usart 0 conflicts with the ISP interface
#define USE_USART_NUMBER 0

#if (USE_USART_NUMBER == 0)

#define UCSRxA UCSR0A
#define UDREx  UDRE0
#define RXCx   RXC0

#define UCSRxB UCSR0B
#define RXENx  RXEN0
#define TXENx  TXEN0

#define UCSRxC UCSR0C
#define UCSZx  UCSZ0

#define UBRRxH UBRR0H
#define UBRRxL UBRR0L

#define UDRx   UDR0

#elif (USE_USART_NUMBER == 1)

#define UCSRxA UCSR1A
#define UDREx  UDRE1
#define RXCx   RXC1

#define UCSRxB UCSR1B
#define RXENx  RXEN1
#define TXENx  TXEN1

#define UCSRxC UCSR1C
#define UCSZx  UCSZ1

#define UBRRxH UBRR1H
#define UBRRxL UBRR1L

#define UDRx   UDR1

#else
#	error Please select USART 0 or 1
#endif


void usart_init(void);

inline static void usart_send(uint8_t data) {
	while (!(UCSRxA & (1<<UDREx)))
		;
	UDRx = data;
}

inline static uint8_t usart_recv(void) {
	while (!(UCSRxA & (1<<RXCx)))
		;
	return UDRx;
}

void usart_send_str(const char* s);

void usart_send_str_P(const char* s);

uint8_t usart_wait_byte_timeout(uint32_t timeout_loops);

#endif /* RS232_H_ */
