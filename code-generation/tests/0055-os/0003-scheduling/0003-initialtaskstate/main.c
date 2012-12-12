/*
 * main.c
 *
 *  Created on: Dec 07, 2012
 *      Author: sven
 */

#include <util/delay.h>

#include <avr/io.h>

#include "Os.h"
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
	usart_send_str("\nStarting initial task state test.\n");

	StartOS();

	while(1);
}


// The earliest call time for the initially suspended task is 500.
// At this point, the ready task must have been called at least 10 times.
TASK(Suspended) {

	OS_ENTER_CRITICAL();

	if (readyCounter >= 10)
		usart_send_str("Test successful. Counter = ");
	else
		usart_send_str("Test failed. Counter = ");

	usart_send_number(readyCounter, 10, 1);
	usart_send_str(" (expected >= 10)\n");

	OS_EXIT_CRITICAL();

	while(1);

	TerminateTask();
}


TASK(Ready) {
	readyCounter++;
	TerminateTask();
}
