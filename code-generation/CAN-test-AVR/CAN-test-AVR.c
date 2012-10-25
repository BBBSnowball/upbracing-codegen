/*
 * CAN-test-AVR.c
 *
 *  Created on: Oct 09, 2012
 *      Author: sven
 */

#include <util/delay.h>

#include <avr/io.h>
#include <avr/pgmspace.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>

#include "Os.h"
#include "drivers/Gpio.h"
#include "semaphores/semaphore.h"
#include "internal/Os_Error.h"

#include "gen/can.h"
#include "gen/global_variables.h"
#include "gen/Os_cfg_application.h"

#include "rs232.h"

int main(void) {
	// Init the USART (9600 8N1)
	usart_init();

	PORTA = 0x01;

	_delay_ms(1000);

	usart_send_str("Startup\n");

	PORTA = 0x02;

	//can_init_mobs();

	StartOS();

	while(1);
}

TASK(Task_Bla) {

	usart_send_str("Task Bla\n");

	TerminateTask();
}
