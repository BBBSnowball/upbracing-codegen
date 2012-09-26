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
	*sp--;
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

StatusType TerminateTask(void) 
{	
	// The termination is a critical process. See below.
	OS_ENTER_CRITICAL();

	// Reset/init task context memory
	// This is done in assembly
	// a) to make it fast (very few sram operations)
	// b) to prevent optimizations and/or memory corruption
	OS_RESET_CONTEXT();
	
	// Set the correct stack address in the TCB for later restore
	// -> This changes only global (volatile) data and is thus safe to do here.		
	os_currentTcb->currentBaseOfStack = os_currentTcb->topOfStack - 35;

	// The critical part is over now
	OS_EXIT_CRITICAL();

	// Switch to another task
	Os_Schedule();
	OS_RESTORE_CONTEXT();
	asm volatile("reti");
	
	// will never get here
	return E_OK;
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
	os_ready_queue[taskId] = READY;
	os_tcbs[taskId].state = READY;
	#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
	#error BCC2 and ECC2 are not yet supported!
	#endif

	//QUESTION(Benjamin): That reminds me of the "yield" function. Does OSEK have something like it?
	//ANSWER(Peer): What is the "yield" function?
	return E_OK;
}
