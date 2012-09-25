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
StatusType TerminateTask(void) 
{	
	//QUESTION(Benjamin): Why does a local variable have to be volatile?
	//volatile uint8_t taskAddressLow = 0, taskAddressHigh = 0;
	volatile uint16_t taskAddress = 0;
	volatile StackPointerType *sp = os_currentTcb->topOfStack;

	// Set state to suspended
	os_currentTcb->state = SUSPENDED;
	os_currentTcb->currentBaseOfStack = os_currentTcb->topOfStack;
	
	//QUESTION(Benjamin): The compiler may be using r26-r29. Can we overwrite
	//                    them without further precautions? You could let the
	//                    compiler choose the registers.
	//QUESTION(Benjamin): Why to you use assembly code? Is it because we have to change SP?	
	/*asm volatile(	"lds r26, os_currentTcb		\n\t"	\
					//"lds r27, os_currentTcb + 1	\n\t"	\
					//"ld r28, x+					\n\t"	\
					//"out __SP_L__, r28			\n\t"	\
					//"ld r29, x+					\n\t"	\
					//"out __SP_H__, r29			\n\t"	);*/
	
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
	
	/* R0, then the status register */
	*sp = (uint8_t) 0x00;								// R0
	sp--;
	*sp = Os_STATUS_REG_INT_ENABLED;					// SREG with global interrupt enable flag
	sp--;
	
	/* R1 up to R31 */
	//QUESTION(Benjamin): Are those the initial values for the registers? Can't we just set them to zero?
	*sp = (uint8_t) 0x00;								// R1
	sp--;
	*sp = (uint8_t) 0x02;								// R2
	sp--;
	*sp = (uint8_t) 0x03;								// R3
	sp--;
	*sp = (uint8_t) 0x04;								// R4
	sp--;
	*sp = (uint8_t) 0x05;								// R5
	sp--;
	*sp = (uint8_t) 0x06;								// R6
	sp--;
	*sp = (uint8_t) 0x07;								// R7
	sp--;
	*sp = (uint8_t) 0x08;								// R8
	sp--;
	*sp = (uint8_t) 0x09;								// R9
	sp--;
	*sp = (uint8_t) 0x10;								// R10
	sp--;
	*sp = (uint8_t) 0x11;								// R11
	sp--;
	*sp = (uint8_t) 0x12;								// R12
	sp--;
	*sp = (uint8_t) 0x13;								// R13
	sp--;
	*sp = (uint8_t) 0x14;								// R14
	sp--;
	*sp = (uint8_t) 0x15;								// R15
	sp--;
	*sp = (uint8_t) 0x16;								// R16
	sp--;
	*sp = (uint8_t) 0x17;								// R17
	sp--;
	*sp = (uint8_t) 0x18;								// R18
	sp--;
	*sp = (uint8_t) 0x19;								// R19
	sp--;
	*sp = (uint8_t) 0x20;								// R20
	sp--;
	*sp = (uint8_t) 0x21;								// R21
	sp--;
	*sp = (uint8_t) 0x22;								// R22
	sp--;
	*sp = (uint8_t) 0x23;								// R23
	sp--;
	*sp = (uint8_t) 0x00;								// R24
	sp--;
	*sp = (uint8_t) 0x00;								// R25
	sp--;
	*sp = (uint8_t) 0x26;								// R26
	sp--;
	*sp = (uint8_t) 0x27;								// R27
	sp--;
	*sp = (uint8_t) 0x28;								// R28
	sp--;
	*sp = (uint8_t) 0x29;								// R29
	sp--;
	*sp = (uint8_t) 0x30;								// R30
	sp--;
	*sp = (uint8_t) 0x31;								// R31
	sp--;
		
	os_currentTcb->currentBaseOfStack = os_currentTcb->topOfStack - 35;
	
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
