/*
 * Timer.c
 *
 * Created: 21.12.2011 16:19:05
 *  Author: peer
 */ 

#include "Timer.h"

// Initializes TCC0 with a prescaling and a compare match value
void TimerInit(uint8_t prescale, uint16_t compare)
{
	TCC0.CTRLA = prescale;									// Prescale (user setting)
	TCC0.CTRLB = TC0_CCAEN_bm;								// Compare or Capture enable for counter A
	TCC0.INTCTRLB = TC0_CCAINTLVL0_bm | TC0_CCAINTLVL1_bm;	// High priority
	TCC0.PER = compare;										// Compare value (user setting)
	PMIC.CTRL |= (1<<PIN0)|(1<<PIN1)|(1<<PIN2);
}