/*
 * OSEK.c
 *
 * Created: 22.12.2011 21:43:40
 *  Author: peer
 */ 

#include "OSEK.h"
#include <avr/interrupt.h>

void StartOS(void) 
{
	uint8_t i = 0;
	for (i = 0; i < OSEK_NUMBER_OF_TCBS; i++)
	{
		InitializeStackForTask(&os_tcbs[i]);
	}
	
	// Start first task
	StartFirstTask();
	
	// Globally enable interrupts
	sei();
}