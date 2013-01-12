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

int counter = 0;

int main(void) {
	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting single task test.\n");

	// Wait some time to give the test program some time to start, so it won't miss the first task instances
	_delay_ms(2000);

	StartOS();

	while(1);
}

TASK(One_Second) {
	usart_send_str("One second\n");
	counter++;

	// Stop the program after 10 instances
	if (counter == 10) while(1);

	TerminateTask();
}
