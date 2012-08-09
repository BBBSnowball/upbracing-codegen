/*
 * Timer.h
 *
 * Created: 21.12.2011 16:17:55
 *  Author: peer
 */ 


#ifndef TIMER_H_
#define TIMER_H_

#include <avr/io.h>
#include <avr/interrupt.h>

// Prescaling: 64
/*#define TIMER_PRESCALE_1_bm		(TC0_CLKSEL0_bm)
#define TIMER_PRESCALE_2_bm		(TC0_CLKSEL1_bm)
#define TIMER_PRESCALE_4_bm		(TC0_CLKSEL1_bm | TC0_CLKSEL0_bm)
#define TIMER_PRESCALE_8_bm		(TC0_CLKSEL2_bm)
#define TIMER_PRESCALE_64_bm	(TC0_CLKSEL2_bm | TC0_CLKSEL0_bm)
#define TIMER_PRESCALE_256_bm	(TC0_CLKSEL2_bm | TC0_CLKSEL1_bm)
#define TIMER_PRESCALE_1024_bm	(TC0_CLKSEL2_bm | TC0_CLKSEL1_bm | TC0_CLKSEL0_bm)*/

#ifdef __AVR_AT90CAN128__
#define TIMER_PRESCALE_1_bm		(1 << CS10)
#define TIMER_PRESCALE_8_bm		(1 << CS11)
#define TIMER_PRESCALE_64_bm	((1 << CS11) | (1 << CS10))
#define TIMER_PRESCALE_256_bm	(1 << CS12)
#define TIMER_PRESCALE_1024_bm	((1 << CS12) | (1 << CS10))
#endif

// Initializes timer1 with a prescaling and a compare match value
extern void TimerInit(void);

#endif /* TIMER_H_ */
