/*
 * CAN-test-AVR.c
 *
 *  Created on: Oct 09, 2012
 *      Author: sven
 */

#include <avr/io.h>
#include <util/delay.h>
#include <avr/pgmspace.h>
#include <avr/interrupt.h>
#include "gen/global_variables.h"

#include "Os.h"
#include "drivers/USART.h"
#include "drivers/Gpio.h"
#include "semaphores/semaphore.h"

#include "gen/can.h"
#include "gen/Os_cfg_application.h"

int main(void) {
	// OS things
	PORTA = 0x01;

	// Init GPIO: (demo: DDRA = 0xFF)
	GpioInit();

	PORTA = 0x02;

	// Init the USART (9600 8N1)
	USARTInit(51);

	PORTA = 0x04;

	USARTEnqueue(6,"Hallo\n");

	PORTA = 0x08;

	can_init_mobs();

	PORTA = 0x10;

	StartOS();

	while(1);
}
