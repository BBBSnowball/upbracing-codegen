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

int main(void) {
	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	_delay_ms(500);

	// Init usart
	usart_init();
	PORTA = 0x02;

	usart_send_str("\nStarting zero tasks test. This message must only show once.\n");
	PORTA = 0x18;

	StartOS();

	usart_send_str("ERROR: StartOS() method returned.\n");
	while(1);
}
