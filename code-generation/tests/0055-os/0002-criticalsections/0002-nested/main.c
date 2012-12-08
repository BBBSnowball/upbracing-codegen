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

#define INITVALUE1 34
#define INITVALUE2 666
#define INITVALUE3 1337

uint32_t sharedValue;
uint32_t sharedValue2;
uint32_t sharedValue3;

uint32_t comparison;
uint32_t comparison2;
uint32_t comparison3;


uint8_t firstTime;

int main(void) {
	//Init variables
	sharedValue = INITVALUE1;
	comparison = sharedValue;
	sharedValue2 = INITVALUE2;
	comparison2 = sharedValue2;
	sharedValue3 = INITVALUE3;
	comparison3 = sharedValue3;

	firstTime = 1;

	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting nested critical sections test.\n");

	StartOS();

	while(1);
}

TASK(Monitor) {

	// Work around for a bug in which SUSPEND mode doesn't work,
	// and this task is run to early to compare the values and
	// could possibly falsely claim the test was successful
	if (firstTime) {
		firstTime = 0;
	} else {
		OS_ENTER_CRITICAL();

		if (comparison == sharedValue &&
			comparison2 == sharedValue2 &&
			comparison3 != sharedValue3)
			usart_send_str("Test successful ");
		else
			usart_send_str("Test failed ");

		usart_send_str("value1 = ");
		usart_send_number(sharedValue, 10, 1);
		usart_send_str(", expected1 = ");
		usart_send_number(comparison, 10, 1);
		usart_send_str(", value2 = ");
		usart_send_number(sharedValue2, 10, 1);
		usart_send_str(", expected2 = ");
		usart_send_number(comparison2, 10, 1);
		usart_send_str(", value3 = ");
		usart_send_number(sharedValue3, 10, 1);
		usart_send_str(", notexpected3 = ");
		usart_send_number(comparison3, 10, 1);
		usart_send_str("\n");
		OS_EXIT_CRITICAL();
	}
	TerminateTask();
}


TASK(Critical) {
	uint32_t tmp;

	OS_ENTER_CRITICAL();

	// Nested critical section
	OS_ENTER_CRITICAL();

	tmp = sharedValue2;
	// Give the other task enough time to make sure it will interfere
	_delay_ms(450);
	// We don't modify the value at all
	sharedValue2 = tmp;

	OS_EXIT_CRITICAL();

	// Left inner critical section, but still inside of outer critical section!

	tmp = sharedValue;
	// Give the other task enough time to make sure it will interfere
	_delay_ms(450);
	// We don't modify the value at all
	sharedValue = tmp;

	OS_EXIT_CRITICAL();

	// Outside of all critical sections, now the task must be preemptable again

	tmp = sharedValue3;
	// Give the other task enough time to make sure it will interfere
	_delay_ms(450);
	// We don't modify the value at all
	sharedValue3 = tmp;

	TerminateTask();
}

// The interfering task modifies the shared value and a compare value
TASK(Interfere) {
	sharedValue++;
	comparison++;
	sharedValue2++;
	comparison2++;
	sharedValue3++;
	comparison3++;
	TerminateTask();
}
