/*
 * Os_Task.c
 *
 * Created: 27.12.2011 21:38:27
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 

#include "config/Os_config.h"
#include "Os_Task.h"
#include "Os_Kernel.h"

void Os_InitializeTaskContext(Os_Tcb * tcb)
{
	// Local variables:
	StackPointerType *sp = tcb->topOfStack;
		
	/* Return address of this stack: init to function pointer */
	*sp = (StackPointerType) (((uint16_t) tcb->func) & (uint16_t) 0x00ff);
	sp--;
	*sp = (StackPointerType) ((((uint16_t) tcb->func) >> 8) & (uint16_t) 0x00ff);
	sp--;
	
	// Reset R0
	*sp = (uint8_t) 0x00;
	sp--;
	// Status register
	*sp = OS_STATUS_REG_INT_ENABLED;					// SREG with global interrupt enable flag
		
	// Reset remaining registers
	for (uint8_t i = 0; i < 31; i++) 
	{
		sp--;
		*sp = (uint8_t) 0x00;
	}
	
	// Set the stack pointer in the tcb for restore
	tcb->currentBaseOfStack = tcb->topOfStack - 35;
}

void TerminateTask(void) 
{	
	// The termination is a critical process. See below.
	// NOTE: Since we destroy the current SREG here, we must use sei/cli here!
	cli();

	// Mark this task SUSPENDED
	os_currentTcb->state = SUSPENDED;

	// Reset/init task context memory
	// This is done in assembly
	// a) to make it fast (very few sram operations)
	// b) to prevent optimizations and/or memory corruption
	OS_RESET_CONTEXT();
	
	// Set the correct stack address in the TCB for later restore
	// -> This changes only global (volatile) data and is thus safe to do here.		
	os_currentTcb->currentBaseOfStack = os_currentTcb->topOfStack - 35;

	// Switch to another task
	Os_Schedule();
	// Restore context will take care of the I-Flag in SREG
	OS_RESTORE_CONTEXT();
}

StatusType GetTaskID(TaskType * taskId)
{
	OS_ENTER_CRITICAL();
	*taskId = os_currentTcb->id;
	OS_EXIT_CRITICAL();
	return E_OK;
}

StatusType GetTaskState(TaskStateType * state)
{		
	OS_ENTER_CRITICAL();
	*state = os_currentTcb->state;
	OS_EXIT_CRITICAL();
	return E_OK;
}

StatusType ActivateTask(TaskType taskId)
{
	OS_ENTER_CRITICAL();
	// Only SUSPENDED tasks may be activated by this function
	if (os_tcbs[taskId].state == SUSPENDED) 
	{
		os_tcbs[taskId].state = READY;
	}
	OS_EXIT_CRITICAL();
	
	//QUESTION(Benjamin): That reminds me of the "yield" function. Does OSEK have something like it?
	//ANSWER(Peer): What is the "yield" function?
	return E_OK;
}

static void SwitchTask(void) __attribute__((naked, noinline));
static void SwitchTask(void) {
	OS_SAVE_CONTEXT();
	Os_Schedule();
	OS_RESTORE_CONTEXT();
}

StatusType WaitTask(void)
{
	OS_ENTER_CRITICAL();
	
	// Puts the currently running task into WAITING state
	os_currentTcb->state = WAITING;
	
	SwitchTask();
	
	OS_EXIT_CRITICAL();
	
	return E_OK;	
}

StatusType ResumeTask(TaskType taskId)
{
	OS_ENTER_CRITICAL();
	// Only WAITING tasks may be activated by this function
	if (os_tcbs[taskId].state == WAITING) {
		os_tcbs[taskId].state = READY;
	}
	OS_EXIT_CRITICAL();
	return E_OK;
}
