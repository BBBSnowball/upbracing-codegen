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

#endif /* FAKE_AVR_H_ */
