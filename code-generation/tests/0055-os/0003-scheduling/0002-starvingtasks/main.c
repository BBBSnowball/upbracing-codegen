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

#define NUMTASKS 5

int c[NUMTASKS];

int main(void) {
	//Init variables
	for(int i=0; i<NUMTASKS; i++) c[i] = 0;

	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting starving task test.\n");

	StartOS();

	while(1);
}

TASK(Monitor) {
	PORTA = 0x01;

	uint32_t sum = 0;

	usart_send_str("\nSum: ");
	OS_ENTER_CRITICAL();
	for (int i=0; i<NUMTASKS; i++) sum += c[i];
	usart_send_number(sum, 10, 3);
	usart_send_str("\n");

	for (int i=0; i<NUMTASKS; i++) {
		usart_send_str("Task ");
		usart_send_number(i+1, 10, 1);
		usart_send_str(": ");
		usart_send_number(c[i], 10, 3);
		usart_send_str("\n");
		c[i] = 0;
	}
	sum=0;
	OS_EXIT_CRITICAL();

	TerminateTask();
}

TASK(Task1) {
	while(1) {
		PORTA = 0x02;

		OS_ENTER_CRITICAL();
		c[0]++;
		OS_EXIT_CRITICAL();

		_delay_us(200);
	}
	TerminateTask();
}
TASK(Task2) {
	while(1) {
		PORTA = 0x04;

		OS_ENTER_CRITICAL();
		c[1]++;
		OS_EXIT_CRITICAL();

		_delay_us(200);
	}
	TerminateTask();
}
TASK(Task3) {
	while(1) {
		PORTA = 0x08;

		OS_ENTER_CRITICAL();
		c[2]++;
		OS_EXIT_CRITICAL();

		_delay_us(200);
	}
	TerminateTask();
}
TASK(Task4) {
	while(1) {
		PORTA = 0x10;

		OS_ENTER_CRITICAL();
		c[3]++;
		OS_EXIT_CRITICAL();

		_delay_us(200);
	}
	TerminateTask();
}
TASK(Task5) {
	while(1) {
		PORTA = 0x20;

		OS_ENTER_CRITICAL();
		c[4]++;
		OS_EXIT_CRITICAL();

		_delay_us(200);
	}
	TerminateTask();
}
