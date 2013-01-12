/*
 * main.c
 *
 *  Created on: Dec 08, 2012
 *      Author: sven
 */

#include <util/delay.h>

#include <avr/io.h>

#include "Os.h"
#include "gen/Os_cfg_application.h"
#include "rs232.h"
#include "rs232-helpers.h"

int main(void) {

	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting preemption test.\n");

	StartOS();

	while(1);
}

TASK(Task1) {
	OS_ENTER_CRITICAL();
	usart_send_str("Start Task1\n");
	OS_EXIT_CRITICAL();

	// Wait, so the task has to be preempted by the OS
	_delay_ms(1000);

	OS_ENTER_CRITICAL();
	usart_send_str("Finish Task1\n");
	while(1); //Stop the program
	OS_EXIT_CRITICAL();

	TerminateTask();
}

TASK(Task2) {
	OS_ENTER_CRITICAL();
	usart_send_str("Task2\n");
	OS_EXIT_CRITICAL();

	TerminateTask();
}
