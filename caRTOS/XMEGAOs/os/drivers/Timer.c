/*
 * Timer.c
 *
 * Created: 21.12.2011 16:19:05
 *  Author: peer
 */ 

// will be compiled in Os_application_dependent_code.c:
#ifdef APPLICATION_DEPENDENT_CODE

#include "config/Os_config.h"
#include "drivers/Timer.h"

// Initializes Timer1 with a prescaling and a compare match value
void TimerInit(void)
{
	OCR1A = OS_TIMER_COMPARE_VALUE;
	TCNT1 = 0;
	TCCR1A = 0;
	TCCR1B = (1 << WGM12) //CTC
	       | OS_TIMER_PRESCALE;
	TIMSK1 = (1 << OCIE1A); // Enable Output Compare Interrupt Match for Timer1/ChannelA.
	
	//TCC0.CTRLA = OS_TIMER_PRESCALE;							// Prescale (user setting)
	//TCC0.CTRLB = TC0_CCAEN_bm;								// Compare or Capture enable for counter A
	//TCC0.INTCTRLB = TC0_CCAINTLVL0_bm | TC0_CCAINTLVL1_bm;	// High priority
	//TCC0.PER = OS_TIMER_COMPARE_VALUE;							// Compare value (user setting)
	//PMIC.CTRL |= (1<<PIN0)|(1<<PIN1)|(1<<PIN2);
}

#endif	// end of APPLICATION_DEPENDENT_CODE
