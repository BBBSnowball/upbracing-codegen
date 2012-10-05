/*
 * fake-avr.c
 *
 *  Created on: Oct 5, 2012
 *      Author: benny
 */

#include "fake-avr.h"

uint8_t DDRA, DDRB, DDRC, DDRD, DDRE, DDRF,
	PORTA, PORTB, PORTC, PORTD, PORTE, PORTF,
	PINA, PINB, PINC, PIND, PINE, PINF;

int wdt_reset_counter = 0;
int wdt_reset_token1, wdt_reset_token2;
void wdt_reset() { wdt_reset_counter++; wdt_reset_token2 = wdt_reset_token1; }
