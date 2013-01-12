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

uint32_t sharedValue;
uint32_t comparison;

int main(void) {
	//Init variables
	sharedValue = 34;
	comparison = sharedValue;

	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting simple critical sections test.\n");

	StartOS();

	while(1);
}

TASK(Monitor) {

	OS_ENTER_CRITICAL();

	if (comparison == sharedValue)
		usart_send_str("Test successful ");
	else
		usart_send_str("Test failed ");

	usart_send_str("value = ");
	usart_send_number(sharedValue, 10, 1);
	usart_send_str(", expected = ");
	usart_send_number(comparison, 10, 1);
	usart_send_str("\n");

	OS_EXIT_CRITICAL();

	TerminateTask();
}


TASK(Critical) {
	uint32_t tmp;

	OS_ENTER_CRITICAL();

	tmp = sharedValue;
	// Give the other task enough time to make sure it will interfere
	_delay_ms(750);
	// We don't modify the value at all
	sharedValue = tmp;

	OS_EXIT_CRITICAL();

	TerminateTask();
}

// The interfering task modifies the shared value and a compare value
TASK(Interfere) {
	sharedValue++;
	comparison++;
	TerminateTask();
}

