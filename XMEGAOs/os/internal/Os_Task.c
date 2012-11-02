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
	OS_RESTORE_CONTEXT();
	
	// The critical part is over now
	// NOTE: This will automatically re-enable interrupts.
	asm volatile("reti");
}

StatusType GetTaskID(TaskRefType taskId)
{
	*taskId = os_currentTcb->id;
	return E_OK;
}

StatusType GetTaskState(TaskStateRefType state)
{		
	*state = os_currentTcb->state;
	return E_OK;
}

StatusType ActivateTask(TaskType taskId)
{
	#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
	// Waiting tasks should be resumed differently (FCFS!)
	if (os_tcbs[taskId].state != WAITING) 
	{
		os_ready_queue[taskId] = READY;
		os_tcbs[taskId].state = READY;
	}	
	#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
	#error BCC2 and ECC2 are not yet supported!
	#endif

	//QUESTION(Benjamin): That reminds me of the "yield" function. Does OSEK have something like it?
	//ANSWER(Peer): What is the "yield" function?
	return E_OK;
}

static void SwitchTask() __attribute__((naked, noinline)) {
	OS_SAVE_CONTEXT();
	Os_Schedule();
	OS_RESTORE_CONTEXT();
}

StatusType WaitTask(TaskType taskId) 
{
	TaskType currentTask;

	#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
	os_ready_queue[taskId] = WAITING;
	os_tcbs[taskId].state = WAITING;
	
	// switch task, if taskId is the current task
	GetTaskID(&currentTask);
	if (taskId == currentTask) {
		SwitchTask();
	}
	#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
	#error BCC2 and ECC2 are not yet supported!
	#endif
	return E_OK;	
}

StatusType ResumeTask(TaskType taskId)
{
	#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
	if (os_tcbs[taskId].state == WAITING) {
		os_ready_queue[taskId] = READY;
		os_tcbs[taskId].state = READY;
	}
	#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
	#error BCC2 and ECC2 are not yet supported!
	#endif
	return E_OK;
}
