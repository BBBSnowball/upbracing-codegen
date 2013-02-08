/*
 * USART.c
 *
 * Created: 24.09.2012 23:49:44
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 

#include <avr/interrupt.h>
#include "internal/Os_Task.h"
#include "USART.h"

// This is the queue for the USART Transmit buffer
// Name: usart
// Capacity: 10 Bytes
// Receivers: 1 (This driver)
// Senders: number of tasks

// will be compiled in Os_application_dependent_code.c:
#ifdef APPLICATION_DEPENDENT_CODE
#include "IPC/queue.h"

#ifndef USART_TRANSMIT_QUEUE_LENGTH
#	warning USART_TRANSMIT_QUEUE_LENGTH not set, using default value of 10
#	define USART_TRANSMIT_QUEUE_LENGTH 10
#endif

// reserve waiting places for "writers" for all
// tasks except the USART transmitter
QUEUE(usart,USART_TRANSMIT_QUEUE_LENGTH,1,OS_NUMBER_OF_TCBS_DEFINE-1);
#else	// end of APPLICATION_DEPENDENT_CODE
QUEUE_EXTERNAL(usart);
#endif

// UBRR = F_CPU/16/BAUD - 1
// 9600 Baud, 8MHz
//#define UBRR_VALUE 51

#define UBRR_VALUE ((F_CPU) / 16 / 9600 - 1)


// Static initialization with 9600 Baud and 1 Stop Bit
void USARTInit(uint16_t ubrr_value)
{
	UBRRxH = (UBRR_VALUE >> 8);
	UBRRxL = (UBRR_VALUE & 0xff);
	// normal mode
	UCSRxA = 0;
	// set frame format 8N1
	UCSRxC = (1<<UCSZx0) | (1<<UCSZx1);
	// enable RX and TX
	UCSRxB = (1<<RXENx) | (1<<TXENx);
}

// This function enqueues <length> bytes for transmit
void USARTEnqueue(uint8_t length, const char * text) 
{
	// This blocks, if there is not enough space available!
	queue_enqueue_many(usart, length, (const uint8_t *) text);
}

TASK(UsartTransmit)
{
	// Check, if Transmitter is ready:
	if (UCSRxA & (1<<UDREx))
	{
		char c;
		// This blocks, if there is no char ready to read!
		c = queue_dequeue(usart);
		UDRx = c;
	}
	TerminateTask();
}
