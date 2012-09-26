/*
 * USART.c
 *
 * Created: 24.09.2012 23:49:44
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 

#include <avr/interrupt.h>
#include "USART.h"

// This is the queue for the USART Transmit buffer
// Name: usart
// Capacity: 10 Bytes
// Receivers: 1 (This driver)
// Senders: arbitrary number (2 in this TEST! case. How can we automate this?)
QUEUE(usart,10,1,2);

// Static initialization with 9600 Baud and 1 Stop Bit
void USARTInit(uint16_t ubrr_value)
{
	//Set Baud rate
	UBRR0L = ubrr_value;
	UBRR0H = (ubrr_value>>8);

	UCSR0C = (1<<UCSZ01) | (1<<UCSZ00); // 8data, no parity & 1 stop bit

	//Enable The receiver and transmitter
	UCSR0B=(1<<RXEN0)|(1<<TXEN0);
	
	// Enable Transmit Complete interrupt
	UCSR0B |= (1<<TXCIE0);
}

// This function enqueues <length> bytes for transmit
void USARTEnqueue(uint8_t length, const char * text) 
{
	// TODO: This should block, if there is not enough space available!
	
	//sem_wait(QUEUE_SEM_REF(usart));
	//sem_wait_n(QUEUE_PROD_REF(usart),3);
	queue_enqueue2(usart, length, text);
	//sem_signal_n(QUEUE_PROD_REF(usart),3);
	//sem_signal(QUEUE_SEM_REF(usart));
	
	// Send first char, if USART is ready!
	// - let the Send-Complete Interrupt do the rest
	// -> if it already sending, this will be auto-sent later 
	OS_ENTER_CRITICAL();
	if(UCSR0A & (1<<UDRE0))
	{
		char c = queue_dequeue(usart);
		UDR0 = c;
	}
	OS_EXIT_CRITICAL();
}

ISR(USART0_TX_vect)
{
	// Only do this, if there is something in the queue!
	if (queue_is_data_available(usart, 1)) 
	{
		char c = queue_dequeue(usart);
		UDR0 = c;
	}	
}