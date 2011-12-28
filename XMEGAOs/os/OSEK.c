/*
 * OSEK.c
 *
 * Created: 22.12.2011 21:43:40
 *  Author: peer
 */ 

#include "Os_cfg_generated.h"
#include "OSEK.h"
#include <avr/interrupt.h>

#ifndef OSEK_CONFORMANCE_CLASS
#error No Conformance Class specified!
#endif

#if OSEK_CONFORMANCE_CLASS != BCC1 && OSEK_CONFORMANCE_CLASS != BCC2 && \
	OSEK_CONFORMANCE_CLASS != ECC1 && OSEK_CONFORMANCE_CLASS != ECC2
#error No valid Conformance Class specified
#endif

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