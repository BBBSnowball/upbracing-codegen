/*
 * OSEK_Task.c
 *
 * Created: 27.12.2011 21:38:27
 *  Author: peer
 */ 

#include "config/Os_config.h"
#include "OSEK_Task.h"
#include "OSEK_Kernel.h"

// Terminates the running task
// Never returns.
StatusType volatile TerminateTask(void) 
{	
	//OS_ENTER_CRITICAL();
	
	//QUESTION(Benjamin): Why does a local variable have to be volatile?
	//Note(Peer): There was no good reason. Removed the keyword
	uint16_t taskAddress = 0;
	StackPointerType *sp = os_currentTcb->topOfStack;

	// Set state to suspended
	os_currentTcb->state = SUSPENDED;
	os_currentTcb->currentBaseOfStack = os_currentTcb->topOfStack;
	
	/* Reset return address to os_currentTcb->func */
	//QUESTION(Benjamin): What "return address" do we set here? Is it the instruction pointer
	//                    for the terminated task?
	//ANSWER(Peer): Yes, that's what I meant :)
	taskAddress = (uint16_t) os_currentTcb->func;
	*sp = (StackPointerType) ( taskAddress & ( uint16_t ) 0x00ff );
	sp--;
	taskAddress >>= 8;
	*sp = (StackPointerType) ( taskAddress & ( uint16_t ) 0x00ff );
	sp--;
	
	// The rest is written in assembly to ensure, that no optimizations take place here.
	/* Set StackPointer to sp */
	asm volatile(	"lds r26, os_currentTcb		\n\t"
					"lds r27, os_currentTcb + 1	\n\t"
					"ld r28, x+					\n\t"
					"dec r28					\n\t"
					"dec r28					\n\t"
					"out __SP_L__, r28			\n\t"
					"ld r29, x+					\n\t"
					"out __SP_H__, r29			\n\t"
	
	/* Clear R0, then reset the status register, then clear following registers */
		// Push Zero for R0
		"clr r1									\n\t"
		"push r1								\n\t"
		// Push init SREG
		"ldi r16, 128		\n\t"
		"push r16								\n\t"
		// Push Zero for R1-R31
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
		"push r1								\n\t"
	);
	
	// Set the correct stack address in the TCB for later restore		
	os_currentTcb->currentBaseOfStack = os_currentTcb->topOfStack - 35;

	// Switch to another task
	Os_Schedule();
	OS_RESTORE_CONTEXT();
	asm volatile("reti");
	
	// will never get here
	return E_OK;
}

StatusType GetTaskID(TaskRefType taskId)
{
	//QUESTION(Benjamin): Do we need a critical section to access the id?
	OS_ENTER_CRITICAL();
	*taskId = os_currentTcb->id;
	OS_EXIT_CRITICAL();
	return E_OK;
}

StatusType GetTaskState(TaskType taskId, TaskStateRefType state)
{
	OS_ENTER_CRITICAL();
	
	// Check, if the task id is valid
	if (taskId > (OS_NUMBER_OF_TCBS - 1))
	{
		OS_EXIT_CRITICAL();
		return E_OS_ID;
	}		
		
	// Return the state of current task
	//QUESTION(Benjamin): Why would we have the taskId parameter, if it returns the state for the current task?
	*state = os_currentTcb->state;
	OS_EXIT_CRITICAL();
	return E_OK;
}

//QUESTION(Benjamin): Why do we have to make the parameter volatile?
StatusType ActivateTask(volatile TaskType taskId)
{
	#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
	os_ready_queue[taskId] = READY;
	os_tcbs[taskId].state = READY;
	#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
	#error BCC2 and ECC2 are not yet supported!
	#endif

	//QUESTION(Benjamin): That reminds me of the "yield" function. Does OSEK have something like it?
	//OSEK_SAVE_CONTEXT();
	//Os_Schedule();
	//OSEK_RESTORE_CONTEXT();
	
	return E_OK;
}
