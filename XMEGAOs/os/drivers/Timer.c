/*
 * Timer.c
 *
 * Created: 21.12.2011 16:19:05
 *  Author: peer
 */ 

#include "Os_config.h"
#include "Timer.h"

// Initializes Timer1 with a prescaling and a compare match value
void TimerInit(void)
{
	
	OCR1A = 3124; // SHORT - just for now...
	TCCR1B = (1 << WGM12) //CTC
	       | (1 << CS12); // Prescale 256
	TIMSK1 = (1 << OCIE1A); // Enable Output Compare Interrupt Match for Timer1/ChannelA.	   
	TCNT1 = 0;
	
	//TCC0.CTRLA = OS_TIMER_PRESCALE;							// Prescale (user setting)
	//TCC0.CTRLB = TC0_CCAEN_bm;								// Compare or Capture enable for counter A
	//TCC0.INTCTRLB = TC0_CCAINTLVL0_bm | TC0_CCAINTLVL1_bm;	// High priority
	//TCC0.PER = OS_TIMER_COMPARE_VALUE;							// Compare value (user setting)
	//PMIC.CTRL |= (1<<PIN0)|(1<<PIN1)|(1<<PIN2);
}
