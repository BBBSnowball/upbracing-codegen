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
volatile TaskType os_nextTaskId = 0;
volatile uint16_t os_counter = 0;
volatile uint8_t os_isStarted = 0;

// Internal function prototypes:
void Os_TimerIncrement(void);
void TIMER1_COMPA_vect(void) __attribute__ ( (signal) );
TaskType _dec_wrap(TaskType nextTaskId);

// QUESTION(Peer): These conformance classes are defined in the OSEK standard.
//                 I don't think that I will implement them in near future.
//                 -> Remove those directives for better readability?
// Check for valid conformance class
#if OS_CFG_CC != BCC1 && OS_CFG_CC != BCC2 && \
		OS_CFG_CC != ECC1 && OS_CFG_CC != ECC2
#	error No valid Conformance Class specified
#endif

//// will be compiled in Os_application_dependent_code.c:
//#ifdef APPLICATION_DEPENDENT_CODE
//#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
///* Simple priority "queue":
 //* - Just an array of bools */
////QUESTION(Benjamin): Could we replace it by a bitfield?
////ANSWER(Peer): Yes. But are we that low on memory?
////              Isn't evaluating single bits quite time consuming?
//uint8_t os_ready_queue[OS_NUMBER_OF_TCBS_DEFINE];
//#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
//#	error Multiple activations for basic tasks, multiple tasks per priority
//#endif
//#endif	// end of APPLICATION_DEPENDENT_CODE

void Os_StartFirstTask(void)
{
    // mark OS as started
	os_isStarted = 1;
    
    // select first task to start
    Os_Schedule();
    
    // restore context of selected task
    // This will "return" to the task and
    // enable interrupts.
	OS_RESTORE_CONTEXT();
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

static void SwitchTask(void) __attribute__((naked, noinline));
static void SwitchTask(void) {
	OS_SAVE_CONTEXT();
	Os_Schedule();
	OS_RESTORE_CONTEXT();
}

// Interrupt routine for compare match of Timer1
//TODO do we have to make this a naked function
void TIMER1_COMPA_vect(void)
{
	Os_TimerIncrement();

	SwitchTask();
}

TaskType _dec_wrap(TaskType tempTaskId)
{
	tempTaskId--;
	if (tempTaskId == 0)
	{
		tempTaskId = OS_NUMBER_OF_TCBS - 1;
	}
	return tempTaskId;
}

StatusType Os_Schedule(void)
{	
	// Decide which task to run next...
	OS_ENTER_CRITICAL();
	
	// Switch this task to ready, if it was running before
	// NOTE: Do not switch WAITING TASKS!
	if (os_currentTcb->state == RUNNING)
		os_currentTcb->state = READY;
	
	// Work with a local copy of the volatile os_nextTaskId variable
	TaskType tempTaskId = os_nextTaskId;
	
	
	if (tempTaskId == 0)
		tempTaskId = OS_NUMBER_OF_TCBS - 1;
	
	for (uint8_t i = 0; i < OS_NUMBER_OF_TCBS; i++)
	{
		// Search first ready task beginning from os_lastTaskId downcounting
		if (os_tcbs[tempTaskId].state == READY)
		{
			// Found ready task.
			os_currentTcb = &os_tcbs[tempTaskId];
			// Continue with the next task
			tempTaskId = _dec_wrap(tempTaskId);
			os_nextTaskId = tempTaskId;
			break;
		}
		else
		{
			if (i == OS_NUMBER_OF_TCBS - 1)
			{
				// No ready task was found. Switching to IDLE
				os_currentTcb = &os_tcbs[0];
				// Continue with the next task
				os_nextTaskId = OS_NUMBER_OF_TCBS - 1;
				break;
			}
			tempTaskId = _dec_wrap(tempTaskId);
		}
	}
	
	// Sets the state of the current task to RUNNING
	os_currentTcb->state = RUNNING;

	OS_EXIT_CRITICAL();
	
	return E_OK;
}

TASK(Task_Idle)
{
	for (;;);
}
