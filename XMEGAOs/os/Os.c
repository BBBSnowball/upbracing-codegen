/*
 * Os.c
 *
 * Created: 22.12.2011 21:43:40
 *  Author: Peer Adelt
 */ 

#include "config/Os_config.h"
#include "Os.h"
#include "drivers/Timer.h"
#include <avr/interrupt.h>
#include <stdlib.h>

void StartOS(void) 
{
	uint8_t i = 0;
	// Init all tasks
	for (i = 0; i < OS_NUMBER_OF_TCBS; i++)
	{
		Os_InitializeTaskContext((Os_Tcb *) &os_tcbs[i]);
	}
	
	// Switch to idle task
	os_currentTcb = &os_tcbs[0];
	os_currentTcb->state = (TaskStateType) READY;
	
	// Init Timer
	TimerInit();
	
	// Start first task
	Os_StartFirstTask();
	
	// Globally enable interrupts
	//QUESTION(Benjamin): I think the program will never get here. This line should be earlier,
	//                    but I think that is already the case - otherwise nothing would work ;-)
	// NOTE(Peer): You're right. But this is kind of special. 
	//             Now, Os_StartFirstTask() will restore context of the idle task and start scheduler.             
}
