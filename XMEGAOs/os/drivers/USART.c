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
// Senders: arbitrary number (2 in this TEST! case. How can we automate this?)
//TODO use a number that works in all cases -> number of tasks
QUEUE(usart,10,1,3);

// Static initialization with 9600 Baud and 1 Stop Bit
void USARTInit(uint16_t ubrr_value)
{
	// Set baud rate
	UBRR0L = ubrr_value;
	UBRR0H = (ubrr_value>>8);

	// Flow control: 8 Bits, 1 Stop Bit, No Parity
	UCSR0C = (1<<UCSZ01) | (1<<UCSZ00);

	// Enable the receiver and transmitter
	UCSR0B=(1<<RXEN0)|(1<<TXEN0);
}

// This function enqueues <length> bytes for transmit
void USARTEnqueue(uint8_t length, const char * text) 
{
	// This blocks, if there is not enough space available!
	queue_enqueue_many(usart, length, (const uint8_t *) text);
}

TASK(Task_UsartTransmit)
{
	// Check, if Transmitter is ready:
	if (UCSR0A & (1<<UDRE0))
	{
		char c;
		// This blocks, if there is no char ready to read!
		c = queue_dequeue(usart);
		UDR0 = c;
	}
	TerminateTask();
}
