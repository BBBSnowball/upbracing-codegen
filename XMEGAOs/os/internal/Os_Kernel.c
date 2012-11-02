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

// Internal function prototypes:
void Os_TimerIncrement(void);
void TIMER1_COMPA_vect(void) __attribute__ ( (signal) );
TaskType _dec_wrap(TaskType nextTaskId);

void Os_StartFirstTask(void)
{
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
		if (alarm->tick == alarm->max)
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

TASK(Idle)
{
	for (;;);
}
