/*
 * Timer.c
 *
 * Created: 21.12.2011 16:19:05
 *  Author: peer
 */ 

#include "Os_cfg_generated.h"
#include "Timer.h"

// Initializes TCC0 with a prescaling and a compare match value
void TimerInit(void)
{
	TCC0.CTRLA = OS_TIMER_PRESCALE;							// Prescale (user setting)
	TCC0.CTRLB = TC0_CCAEN_bm;								// Compare or Capture enable for counter A
	TCC0.INTCTRLB = TC0_CCAINTLVL0_bm | TC0_CCAINTLVL1_bm;	// High priority
	TCC0.PER = OS_TIMER_COMPARE_VALUE;							// Compare value (user setting)
	PMIC.CTRL |= (1<<PIN0)|(1<<PIN1)|(1<<PIN2);
}