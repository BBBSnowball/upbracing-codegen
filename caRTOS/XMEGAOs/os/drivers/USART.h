/*
 * USART.h
 *
 * Created: 24.09.2012 23:49:24
 *  Author: peer
 */ 


#ifndef USART_H_
#define USART_H_

#include "IPC/queue.h"

// use usart 1 because usart 0 conflicts with the ISP interface
#define USE_USART_NUMBER 1

#if (USE_USART_NUMBER == 0)

#define UCSRxA UCSR0A
#define UDREx  UDRE0
#define RXCx   RXC0

#define UCSRxB UCSR0B
#define RXENx  RXEN0
#define TXENx  TXEN0

#define UCSRxC UCSR0C
#define UCSZx0 UCSZ00
#define UCSZx1 UCSZ01

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
#define UCSZx0 UCSZ10
#define UCSZx1 UCSZ11

#define UBRRxH UBRR1H
#define UBRRxL UBRR1L

#define UDRx   UDR1

#else
#	error Please select USART 0 or 1
#endif



void USARTInit(uint16_t ubrr_value);
void USARTEnqueue(uint8_t length, const char * text);


#endif /* USART_H_ */
