/*
 * OSEK.c
 *
 * Created: 22.12.2011 21:43:40
 *  Author: peer
 */ 

#include "Os_cfg_generated.h"
#include "OSEK.h"
#include "Timer.h"
#include <avr/interrupt.h>
#include <stdlib.h>
#include <avr/delay.h>

#ifndef OS_CFG_CC
#error No Conformance Class specified!
#endif

#if OS_CFG_CC != BCC1 && OS_CFG_CC != BCC2 && \
	OS_CFG_CC != ECC1 && OS_CFG_CC != ECC2
#error No valid Conformance Class specified
#endif


void StartOS(void) 
{
	uint8_t i = 0;
	// Init all tasks
	for (i = 0; i < OS_NUMBER_OF_TCBS; i++)
	{
		InitializeStackForTask(&os_tcbs[i]);
	}
	
	// Switch to idle task
	os_currentTcb = &os_tcbs[0];
	os_currentTcb->state = (TaskStateType) READY;
	
	// Init Timer
	TimerInit();
	
	// Start first task
	StartFirstTask();
	
	// Globally enable interrupts
	//QUESTION(Benjamin): I think the program will never get here. This line should be earlier,
	//                    but I think that is already the case - otherwise nothing would work ;-)
	sei();
}