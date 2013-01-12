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
#include "gen/timer.h"

uint16_t counter1=0;
uint16_t counter2A=0;
uint16_t counter2B=0;

volatile uint8_t runTest = 1;
volatile uint8_t tmp1=3;
volatile uint8_t tmp2=3;


int main(void) {
	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting overhead test.\n");

	usart_send_str("Measuring performance without OS...\n");

	PORTA = 0x04;
	// Initialize the timer and enable interrupts
	timer_one_second_init();
	timer_one_second_start();
	sei();

	// The loop will run exactly 1 second and then the timer interrupt will set runTest to 0
	while(runTest) {
		for (uint8_t i=0; i<50; i++) {
			// Do something useless
			tmp1 *= 2;
		}

		//Increment counter, but do it in a "critical section", because it is a 16 bit operation
		cli();
		counter1++;
		sei();
	}

	PORTA = 0x08;
	// Disable interrupts and stop timer
	cli();
	timer_one_second_stop();

	usart_send_str("Value: ");
	usart_send_number(counter1, 10, 1);
	usart_send_str("\n");


	usart_send_str("Measuring performance with OS and two tasks...\n");

	PORTA = 0x10;

	runTest = 1;

	// Restart timer for OS test
	timer_one_second_init();
	timer_one_second_start();
	sei();

	StartOS();

	while(1);
}

ISR(SIG_OUTPUT_COMPARE3A) {
	runTest = 0;
}

TASK(Monitor) {
	OS_ENTER_CRITICAL();

	PORTA = 0x20;

	uint16_t sum = 0;

	usart_send_str("Value Task 1: ");
	usart_send_number(counter2A, 10, 1);
	usart_send_str("\nValue Task 2: ");
	usart_send_number(counter2B, 10, 1);
	usart_send_str("\n");

	sum = counter2A + counter2B;

	usart_send_str("Value OS: ");
	usart_send_number(sum, 10, 1);
	usart_send_str("\n");

	// Calculate overhead:
	uint16_t percent = ((float)(counter1-sum) * 1000.0) / (float)counter1;

	// Consider <10% successful
	if (percent < 100)
		usart_send_str("Test successful. ");
	else
		usart_send_str("Test failed. ");


	usart_send_str("OS Overhead: ");
	usart_send_number(percent / 10, 10, 1);
	usart_send_str(".");
	usart_send_number(percent % 10, 10, 1);
	usart_send_str("%\n");

	// Stop the OS...
	while(1);

	OS_EXIT_CRITICAL();

	TerminateTask();
}

TASK(Task1) {
	while(runTest) {
		for (uint8_t i=0; i<50; i++) {
			// Do something useless
			tmp1 *= 2;
		}

		//Increment counter, but do it in a "critical section", because it is a 16 bit operation
		cli();
		counter2A++;
		sei();
	}
	TerminateTask();
}
TASK(Task2) {
	while(runTest) {
		for (uint8_t i=0; i<50; i++) {
			// Do something useless
			tmp2 *= 2;
		}

		//Increment counter, but do it in a "critical section", because it is a 16 bit operation
		cli();
		counter2B++;
		sei();
	}
	TerminateTask();
}
