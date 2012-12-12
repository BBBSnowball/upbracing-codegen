/*
 * main.c
 *
 *  Created on: Dec 07, 2012
 *      Author: sven
 */

#include <util/delay.h>

#include <avr/io.h>

#include "Os.h"
#include "drivers/USART.h"
#include "gen/Os_cfg_application.h"
#include "rs232.h"
#include "rs232-helpers.h"

uint32_t readyCounter;

int main(void) {
	//Init variables
	readyCounter = 0;

	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting USART driver test. (This line is not printed by the USART driver!)\n");

	_delay_ms(10);

	PORTA = 0x04;
	// Reset the USART registers
	UBRR0H = 0x00;
	UBRR0L = 0x00;
	UCSR0A = 0x00;
	UCSR0C = 0x00;
	UCSR0B = 0x00;
	UBRR1H = 0x00;
	UBRR1L = 0x00;
	UCSR1A = 0x00;
	UCSR1C = 0x00;
	UCSR1B = 0x00;

	_delay_ms(10);

	PORTA = 0x08;
	// Initialize with the driver function
	USARTInit(51);

	StartOS();

	while(1);
}


TASK(Print1) {
	PORTA = 0x10;

	// Print some words that each fit into the USART buffer but not both together
	USARTEnqueue(7, "Hello1\n");
	USARTEnqueue(7, "Hello2\n");

	TerminateTask();
}

TASK(Print2) {
	PORTA = 0x20;

	// Print one line that doesn't fit into the buffer...
	USARTEnqueue(67, "If this line is displayed correctly, the USART test is successful!\n");

	TerminateTask();
}

void OS_error() {
	OS_ENTER_CRITICAL();
	usart_init();
	usart_send_str("OS_error() called\n");

	while(1);

	OS_EXIT_CRITICAL();
}
