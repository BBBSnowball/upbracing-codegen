/*
 * Os_Task.c
 *
 * Created: 27.12.2011 21:38:27
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 

#include "config/Os_config.h"
#include "Os_Task.h"
#include "Os_Kernel.h"

//////////////////////////////////////////////////////////////////////////
// Function:  InitializeTaskContext                                     //
// Returns:   void                                                      //
// Parameter: Os_TcbType * tcb                                          //     
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Initializes the stack of a given TCB for first time use.             //
//////////////////////////////////////////////////////////////////////////
void InitializeTaskContext(Os_Tcb * tcb)
{
	// Local variables:
	StackPointerType *sp = tcb->topOfStack;
	
	/* Return address of this stack: init to function pointer */
	*sp = (StackPointerType) (((uint16_t) tcb->func) & (uint16_t) 0x00ff);
	sp--;
	*sp = (StackPointerType) ((((uint16_t) tcb->func) >> 8) & (uint16_t) 0x00ff);
	sp-=2;
	
	// R0 may contain anything (temp register)
	// Status register
	*sp = OS_STATUS_REG_INT_ENABLED;					// SREG with global interrupt enable flag
	sp--;
	
	// R1 always needs to be zero
	*sp = (uint8_t) 0x00;								// R1
	
	// R2-R25 are general purpose registers (no effect at the beginning of a task)
	// Same goes for X, Y and Z register at the beginning of a task (R25-R31)
	
	// Set the stack pointer in the tcb for restore
	tcb->currentBaseOfStack = tcb->topOfStack - 35;
}

//////////////////////////////////////////////////////////////////////////
// Function:  TerminateTask                                             //
// Returns:   void                                                      //
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Suspends the current task and cleans up stack space.                 //
//////////////////////////////////////////////////////////////////////////
StatusType TerminateTask(void) 
{	
	// Local variables:
	StackPointerType *sp = os_currentTcb->topOfStack;

	// The termination is a critical process. See below.
	OS_ENTER_CRITICAL();

	// Set state to suspended
	os_currentTcb->state = SUSPENDED;
	os_currentTcb->currentBaseOfStack = os_currentTcb->topOfStack;
	
	// Write the start address of the task function to the very top of this stack
	// -> This will make the processor return to that task when context is restored
	// -> These first 2 Bytes will not be altered by the inline asm below.
	*sp = (StackPointerType) (((uint16_t) os_currentTcb->func) & (uint16_t) 0x00ff);
	sp--;
	*sp = (StackPointerType) ((((uint16_t) os_currentTcb->func) >> 8) & (uint16_t) 0x00ff);
	sp--;
	
	// The next part resets the stack space for the registers to 0
	// This also means, that all local variables get invalid and
	// cannot be used for interating over the memory space.
	
	// Therefore, the next part is written in assembly.
	// 1) Load top stack pointer for this TCB
	// 2) Decrement stack pointer by 2
	// 3) Clear R1 (just to be sure)
	// 4) Fill R0 space with zero
	// 5) Push a valid SREG (init SREG value)
	// 6) Fill remaining register space with zeroes (R1-R31)
	
	// The rest is written in assembly to ensure, that no optimizations take place here.
	/* Set StackPointer to sp */
	asm volatile(	// 1) Load top stack pointer for this TCB
					"lds r26, os_currentTcb		\n\t"
					"lds r27, os_currentTcb + 1	\n\t"
					"ld r28, x+					\n\t"
					// 2) Decrement stack pointer by 2
					"sbiw r28, 2				\n\t"
					"out __SP_L__, r28			\n\t"
					"ld r29, x+					\n\t"
					"out __SP_H__, r29			\n\t"
					// 3) Clear R1 (just to be sure)
					"clr __zero_reg__			\n\t"
					// 4) Fill R0 space with zero
					"push __zero_reg__			\n\t"
					// 5) Push a valid SREG (init SREG value)
					"ldi r16, 128				\n\t"
					"push r16					\n\t"
					// 6) Fill remaining register space with zeroes (R1-R31)
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
					"push __zero_reg__			\n\t"
	);
	
	// Set the correct stack address in the TCB for later restore
	// -> This changes only global (volatile) data and is thus safe to do here.		
	os_currentTcb->currentBaseOfStack = os_currentTcb->topOfStack - 35;

	OS_EXIT_CRITICAL();

	// Switch to another task
	Os_Schedule();
	OS_RESTORE_CONTEXT();
	asm volatile("reti");
	
	// will never get here
	return E_OK;
}

//////////////////////////////////////////////////////////////////////////
// Function:  GetTaskID                                                 //
// Returns:   ID of the running task                                    //
// Parameter: Pointer for return value                                  //
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Returns the ID of the running task.                                  //
//////////////////////////////////////////////////////////////////////////
StatusType GetTaskID(TaskRefType taskId)
{
	*taskId = os_currentTcb->id;
	return E_OK;
}

//////////////////////////////////////////////////////////////////////////
// Function:  GetTaskState                                              //
// Returns:   State of the running task                                 //
// Parameter: Pointer for return value                                  //
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Returns the state of the running task.                               //
//////////////////////////////////////////////////////////////////////////
StatusType GetTaskState(TaskStateRefType state)
{		
	*state = os_currentTcb->state;
	return E_OK;
}

//////////////////////////////////////////////////////////////////////////
// Function:  ActivateTask                                              //
// Parameter: ID of the task to activate                                //
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Sets the state of a task to ready and marks this in the              //
// ready-flag array accordingly.                                        //
//////////////////////////////////////////////////////////////////////////
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
