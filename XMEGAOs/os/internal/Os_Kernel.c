/*
 * Os_Kernel.c
 *
 * Created: 22.12.2011 20:55:46
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 

#include "config/Os_config.h"
#include "Os_Kernel.h"
#include <avr/interrupt.h>

// Global Os variables
volatile Os_Tcb * os_currentTcb = &os_tcbs[0];
volatile uint16_t os_counter = 0;
volatile uint8_t os_isStarted = 0;

// Internal function prototypes:
void Os_TimerIncrement(void);
void TIMER1_COMPA_vect(void) __attribute__ ( (signal, naked) );

// QUESTION(Peer): These conformance classes are defined in the OSEK standard.
//                 I don't think that I will implement them in near future.
//                 -> Remove those directives for better readability?
// Check for valid conformance class
#if OS_CFG_CC != BCC1 && OS_CFG_CC != BCC2 && \
		OS_CFG_CC != ECC1 && OS_CFG_CC != ECC2
#	error No valid Conformance Class specified
#endif

// will be compiled in Os_application_dependent_code.c:
#ifdef APPLICATION_DEPENDENT_CODE
#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
/* Simple priority "queue":
 * - Just an array of bools */
//QUESTION(Benjamin): Could we replace it by a bitfield?
//ANSWER(Peer): Yes. But are we that low on memory?
//              Isn't evaluating single bits quite time consuming?
uint8_t os_ready_queue[OS_NUMBER_OF_TCBS_DEFINE];
#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
#	error Multiple activations for basic tasks, multiple tasks per priority
#endif
#endif	// end of APPLICATION_DEPENDENT_CODE

void Os_StartFirstTask(void)
{
    // mark OS as started
	os_isStarted = 1;
    
    // select first task to start
    Os_Schedule();
    
    // restore context of selected task
	OS_RESTORE_CONTEXT();
	
	// enable interrupts
    // -> scheduler is active
	sei();
	
    // finish this task and "return" to
    // selected task (probably Idle)
	asm volatile("reti");
}

void Os_TimerIncrement(void) 
{
	/* Increment Os counter */
	os_counter++;
	
	/* Run Os Alarms */
	for (uint8_t i = 0; i < OS_NUMBER_OF_ALARMS; i++)
	{
		Os_Alarm * alarm = (Os_Alarm *) &os_alarms[i];
		alarm->tick++;
		if (alarm->tick == alarm->ticksperbase)
		{
			RunAlarm(alarm);
		}
	}
}

// Interrupt routine for compare match of Timer1
//TODO do we have to make this a naked function
void TIMER1_COMPA_vect(void)
{
	// save context, return value will be that of the timer interrupt
	//NOTE: We assume that this function doesn't put anything on the
	//      stack. Otherwise, the return value will be in the wrong
	//      position.
	//TODO I think this assumption is false... :-(
	OS_SAVE_CONTEXT();
	Os_TimerIncrement();	
	Os_Schedule();
	OS_RESTORE_CONTEXT();
}

StatusType Os_Schedule(void)
{	
	// Decide which task to run next...
	#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
	OS_ENTER_CRITICAL();
	os_currentTcb = &os_tcbs[0];
	if (os_currentTcb->preempt == PREEMPTABLE
		|| os_currentTcb->state == SUSPENDED) 
	{
		for (uint8_t i = OS_NUMBER_OF_TCBS - 1; i > 0; i--)
		{
			if (os_ready_queue[i] == READY)
			{
				os_currentTcb = &(os_tcbs[i]);
				os_ready_queue[i] = RUNNING;
				break;
			}
		}
	}

	OS_EXIT_CRITICAL();
	#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
	#error Multiple activations for basic tasks, multiple tasks per priority
	#endif
	
	return E_OK;
}

TASK(Task_Idle)
{
	for (;;);
}
