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
#define TIMER_PRESCALE_1_bm		(TC0_CLKSEL0_bm)
#define TIMER_PRESCALE_2_bm		(TC0_CLKSEL1_bm)
#define TIMER_PRESCALE_4_bm		(TC0_CLKSEL1_bm | TC0_CLKSEL0_bm)
#define TIMER_PRESCALE_8_bm		(TC0_CLKSEL2_bm)
#define TIMER_PRESCALE_64_bm	(TC0_CLKSEL2_bm | TC0_CLKSEL0_bm)
#define TIMER_PRESCALE_256_bm	(TC0_CLKSEL2_bm | TC0_CLKSEL1_bm)
#define TIMER_PRESCALE_1024_bm	(TC0_CLKSEL2_bm | TC0_CLKSEL1_bm | TC0_CLKSEL0_bm)

// Initializes TCC0 with a prescaling and a compare match value
extern void TimerInit(void);

#endif /* TIMER_H_ */