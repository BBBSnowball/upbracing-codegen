/*
 * Os_Kernel.c
 *
 * Created: 22.12.2011 20:55:46
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 

#include "config/Os_config.h"
#include "Os_Kernel.h"
#include "Os_Task.h"
#include <avr/interrupt.h>

volatile Os_Tcb * os_currentTcb = &os_tcbs[0];
volatile uint16_t os_counter = 0;
volatile uint8_t os_isStarted = 0;

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
uint8_t os_ready_queue[OS_NUMBER_OF_TCBS_DEFINE];
#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
#	error Multiple activations for basic tasks, multiple tasks per priority
#endif
#endif	// end of APPLICATION_DEPENDENT_CODE


void StartFirstTask(void);
void StartFirstTask(void)
{
	OS_RESTORE_CONTEXT();
	os_isStarted = 1;
	
	//QUESTION(Benjamin): Do we need this "ret" here? Won't the compiler
	//                    generate one itself (although the function is naked)?
	//ANSWER(Peer): Since I removed the "naked" keyword from this function definition,
	//				this is not necessary anymore. So I removed it.
}

void Os_TimerIncrement(void);
void Os_TimerIncrement(void) 
{
	/* Increment os counter */
	os_counter++;
	
	/* Run Os Alarms */
	for (uint8_t i = 0; i < OS_NUMBER_OF_ALARMS; i++)
	{
		Os_Alarm * base = (Os_Alarm *) &os_alarms[i];
		base->tick++;
		if (base->tick == base->ticksperbase)
		{
			RunAlarm(base);
			base->tick = 0;
		}
	}
}

// Interrupt routine for compare match of Timer1
void TIMER1_COMPA_vect(void) __attribute__ ( (signal, naked) );
void TIMER1_COMPA_vect(void)
{
	OS_SAVE_CONTEXT();
	Os_TimerIncrement();	
	Os_Schedule();
	OS_RESTORE_CONTEXT();
	
	asm volatile("reti");
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

//StatusType Schedule(void)
//{
	//// Decide which task to run next...
	//#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
	//if (os_currentTcb->preempt == PREEMPTABLE
		//|| os_currentTcb->state == SUSPENDED) 
	//{
		//for (int8_t i = OS_NUMBER_OF_TCBS - 1; i >= 0; i--)
		//{
			//if (os_ready_queue[i] == READY)
			//{
				//os_currentTcb = &os_tcbs[i];
				//os_ready_queue[i] = RUNNING;
				//break;
			//}
		//}
	//}
	//#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
	//#error Multiple activations for basic tasks, multiple tasks per priority
	//#endif
//
	//// Should never get here...
	//return E_OK;	
//}

TASK(Task_Idle)
{
	for (;;)
	{
		//Os_Schedule();
	}
}
