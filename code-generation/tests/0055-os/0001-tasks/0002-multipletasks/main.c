/*
 * main.c
 *
 *  Created on: Dec 07, 2012
 *      Author: sven
 */

#include <avr/io.h>

#include "Os.h"
#include "gen/Os_cfg_application.h"
#include "rs232.h"
#include "rs232-helpers.h"

#define NUMTASKS 6

int c[NUMTASKS];
int outputs = 0;

int main(void) {
	//Init variables
	for(int i=0; i<NUMTASKS; i++) c[i] = 0;

	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting multiple tasks test.\n");

	StartOS();

	while(1);
}

TASK(Monitor) {

	uint16_t sum = 0;

	usart_send_str("\nSum: ");
	OS_ENTER_CRITICAL();
	for (int i=0; i<NUMTASKS; i++) sum += c[i];
	usart_send_number(sum, 10, 3);
	usart_send_str("\n");

	for (int i=0; i<NUMTASKS; i++) {
		usart_send_str("Task ");
		usart_send_number(i, 10, 1);
		usart_send_str(": ");
		usart_send_number(c[i], 10, 3);
		usart_send_str("\n");
		c[i] = 0;
	}

	// Only output 5 times
	outputs++;
	if (outputs == 5) while(1);

	OS_EXIT_CRITICAL();

	TerminateTask();
}

TASK(Task1) {
	c[0]++;
	TerminateTask();
}
TASK(Task2) {
	c[1]++;
	TerminateTask();
}
TASK(Task3) {
	c[2]++;
	TerminateTask();
}
TASK(Task4) {
	c[3]++;
	TerminateTask();
}
TASK(Task5) {
	c[4]++;
	TerminateTask();
}
TASK(Task6) {
	c[5]++;
	TerminateTask();
}
