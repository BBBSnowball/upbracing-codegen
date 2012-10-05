/*
 * fake-avr.h
 *
 *  Created on: Sep 15, 2012
 *      Author: benny
 */

#ifndef FAKE_AVR_H_
#define FAKE_AVR_H_

typedef unsigned char uint8_t;

extern uint8_t DDRA, DDRB, DDRC, DDRD, DDRE, DDRF,
	PORTA, PORTB, PORTC, PORTD, PORTE, PORTF,
	PINA, PINB, PINC, PIND, PINE, PINF;

void wdt_reset();


// internal values - used to test effects of function calls etc.

// incremented each time wdt_reset() is called
extern int wdt_reset_counter;
// wdt_reset will copy wdt_reset_token to wdt_reset_token2
extern int wdt_reset_token1, wdt_reset_token2;

#endif /* FAKE_AVR_H_ */
