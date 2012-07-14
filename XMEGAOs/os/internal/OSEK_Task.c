/*
 * OSEK_Task.c
 *
 * Created: 27.12.2011 21:38:27
 *  Author: peer
 */ 

#include "Os_cfg_generated.h"
#include "OSEK_Task.h"
#include "OSEK_Kernel.h"

// Terminates the running task
// Never returns.
StatusType TerminateTask(void) 
{	
	//QUESTION(Benjamin): Why does a local variable have to be volatile?
	volatile uint8_t taskAddressLow = 0, taskAddressHigh = 0;
	volatile uint16_t taskAddress = 0;
	volatile StackPointerType *sp = os_currentTcb->baseOfStack;

	// Set state to suspended
	os_currentTcb->state = SUSPENDED;
	os_currentTcb->topOfStack = os_currentTcb->baseOfStack;
	
	//QUESTION(Benjamin): The compiler may be using r26-r29. Can we overwrite
	//                    them without further precautions? You could let the
	//                    compiler choose the registers.
	//QUESTION(Benjamin): Why to you use assembly code? Is it because we have to change SP?	
	//asm volatile(	"lds r26, os_currentTcb		\n\t"	\
					//"lds r27, os_currentTcb + 1	\n\t"	\
					//"ld r28, x+					\n\t"	\
					//"out __SP_L__, r28			\n\t"	\
					//"ld r29, x+					\n\t"	\
					//"out __SP_H__, r29			\n\t"	);
	
	/* Reset return address to os_currentTcb->func */
	//QUESTION(Benjamin): What "return address" do we set here? Is it the instruction pointer
	//                    for the terminated task?
	taskAddress = (uint16_t) os_currentTcb->func;
	*sp = (StackPointerType) ( taskAddress & ( uint16_t ) 0x00ff );
	sp--;
	taskAddress >>= 8;
	*sp = (StackPointerType) ( taskAddress & ( uint16_t ) 0x00ff );
	sp--;
	
	os_currentTcb->topOfStack = os_currentTcb->baseOfStack - 35;
	
	asm volatile(	"lds r26, os_currentTcb		\n\t"	\
					"lds r27, os_currentTcb + 1	\n\t"	\
					"ld r28, x+					\n\t"	\
					"out __SP_L__, r28			\n\t"	\
					"ld r29, x+					\n\t"	\
					"out __SP_H__, r29			\n\t"	);

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
	os_ready_queue[taskId] = 1;
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