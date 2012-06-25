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
StatusType TerminateTask(void) 
{	
	volatile uint8_t taskAddressLow = 0, taskAddressHigh = 0;
	volatile uint16_t taskAddress = 0;
	volatile StackPointerType *sp = os_currentTcb->baseOfStack;

	// Set state to suspended
	os_currentTcb->state = SUSPENDED;
	os_currentTcb->topOfStack = os_currentTcb->baseOfStack;
	
	//asm volatile(	"lds r26, os_currentTcb		\n\t"	\
					//"lds r27, os_currentTcb + 1	\n\t"	\
					//"ld r28, x+					\n\t"	\
					//"out __SP_L__, r28			\n\t"	\
					//"ld r29, x+					\n\t"	\
					//"out __SP_H__, r29			\n\t"	);
	
	/* Reset return address to os_currentTcb->func */
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
		
	Os_Schedule();
	OSEK_RESTORE_CONTEXT();
	asm volatile("reti");
	
	return E_OK;
}

StatusType GetTaskID(TaskRefType taskId)
{
	OSEK_ENTER_CRITICAL();
	*taskId = os_currentTcb->id;
	OSEK_EXIT_CRITICAL();
	return E_OK;
}

StatusType GetTaskState(TaskType taskId, TaskStateRefType state)
{
	OSEK_ENTER_CRITICAL();
	
	// Check, if the task id is valid
	if (taskId > (OS_NUMBER_OF_TCBS - 1))
	{
		OSEK_EXIT_CRITICAL();
		return E_OS_ID;
	}		
		
	// Return the state of current task
	*state = os_currentTcb->state;
	OSEK_EXIT_CRITICAL();
	return E_OK;
}

StatusType ActivateTask(volatile TaskType taskId)
{
	#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
	os_ready_queue[taskId] = 1;
	os_tcbs[taskId].state = READY;
	#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
	#error BCC2 and ECC2 are not yet supported!
	#endif
	
	//OSEK_SAVE_CONTEXT();
	//Os_Schedule();
	//OSEK_RESTORE_CONTEXT();
	
	return E_OK;
}