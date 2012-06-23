/*
 * OSEK_Scheduler.c
 *
 * Created: 22.12.2011 20:55:46
 *  Author: peer
 */ 

#include "Os_cfg_generated.h"
#include "OSEK_Kernel.h"
#include "OSEK_Task.h"
#include <avr/interrupt.h>

//uint8_t i = 0;
//uint8_t os_currentTaskId = 0;
volatile Os_Tcb * os_currentTcb = &os_tcbs[0];
volatile uint16_t os_counter = 0;
volatile uint8_t os_isStarted = 0;

#if OS_CFG_CC != BCC1 && OS_CFG_CC != BCC2 && \
	OS_CFG_CC != ECC1 && OS_CFG_CC != ECC2
#error No valid Conformance Class specified
#endif

#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
/* Simple priority "queue":
 * - Just an array of bools */
//QUESTION(Benjamin): Could we replace it by a bitfield?
uint8_t os_ready_queue[OS_NUMBER_OF_TCBS];
#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
#error Multiple activations for basic tasks, multiple tasks per priority
#endif




// Initializes the stack of a given TCB for first time use
//************************************
// Method:    Os_InitializeStackForTask
// FullName:  Os_InitializeStackForTask
// Access:    public 
// Returns:   void
// Qualifier:
// Parameter: Os_TcbType * tcb
//************************************
void InitializeStackForTask(volatile Os_Tcb * tcb)
{
	volatile uint8_t taskAddressLow = 0, taskAddressHigh = 0;
	volatile uint16_t taskAddress = 0;
	volatile StackPointerType *sp = tcb->baseOfStack;
	
	/* Return address of this stack: init to function pointer */
	taskAddress = (uint16_t) tcb->func;
	taskAddressLow = (uint8_t) (((uint16_t) tcb->func) & 0x00FF);
	taskAddressHigh = (uint8_t) (((uint16_t)tcb->func) >> 8) & 0xFF;
	*sp = (uint8_t) taskAddressLow;						// Low byte
	sp--;
	*sp = (uint8_t) taskAddressHigh;					// High byte
	sp--;
	*sp = (uint8_t) 0x00;								// 3 byte address?
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
	
	tcb->topOfStack = sp;
}

//QUESTION(Benjamin): Do you mean to make StartFirstTask the naked function?
void TCC0_CCA_vect(void) __attribute__ ( (naked) );
void StartFirstTask(void)
{
	OSEK_RESTORE_CONTEXT();
	os_isStarted = 1;
	
	//QUESTION(Benjamin): Do we need this "ret" here? Won't the compiler
	//                    generate one itself (although the function is naked)?
	asm volatile("ret");
}

// Called regularly by a timer interrupt
void Os_TimerIncrement(void) __attribute__ ( (naked) );
void Os_TimerIncrement(void) 
{
	/* Increment os counter */
	os_counter++;
	
	/* Run Os Alarms */
	for (volatile uint8_t i = 0; i < OS_NUMBER_OF_ALARMS; i++)
	{
		Os_Alarm alarm = os_alarms[i];
		os_alarms[i].tick++;
		if (alarm.active &&
		    alarm.tick % alarm.basetype.ticksperbase == 0) 
		{
			RunAlarm(&os_alarms[i]);
		}
	}
	
	/* Probably switch to another task */
	Os_Schedule();
	
	asm volatile ("ret");
}

// Interrupt routine for compare match of TCC0
void TCC0_CCA_vect(void) __attribute__ ( (signal, naked) );
void TCC0_CCA_vect(void)
{
	OSEK_SAVE_CONTEXT();
	Os_TimerIncrement();	
	OSEK_RESTORE_CONTEXT();
	
	asm volatile("reti");
}

void Os_Schedule(void) __attribute__ ( (naked) );
void Os_Schedule(void)
{	
	// Decide which task to run next...
	#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
	OSEK_ENTER_CRITICAL();
	void * newtcb = NULL;
	if (os_currentTcb->preempt == PREEMPTABLE
		|| os_currentTcb->state == SUSPENDED) 
	{
		for (volatile uint8_t i = OS_NUMBER_OF_TCBS - 1; i > 0; i--)
		{
			if (os_ready_queue[i])
			{
				newtcb = &(os_tcbs[i]);
				os_ready_queue[i] = 0;
				break;
			}
		}
	}
	
	if (newtcb == NULL)
	{
		// Switch to idle then...
		os_currentTcb = &os_tcbs[0];
	}
	else 
	{
		os_currentTcb = newtcb;
	}
	
	OSEK_EXIT_CRITICAL();
	#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
	#error Multiple activations for basic tasks, multiple tasks per priority
	#endif
		
	asm volatile("reti");
}

//QUESTION(Benjamin): What is the difference of Schedule and Os_Schedule?
StatusType Schedule(void)
{
	// Decide which task to run next...
	#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
	if (os_currentTcb->preempt == PREEMPTABLE
		|| os_currentTcb->state == SUSPENDED) 
	{
		for (volatile int8_t i = OS_NUMBER_OF_TCBS - 1; i >= 0; i--)
		{
			if (os_ready_queue[i])
			{
				os_currentTcb = &os_tcbs[i];
				os_ready_queue[i] = 0;
				break;
			}
		}
	}
	#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
	#error Multiple activations for basic tasks, multiple tasks per priority
	#endif

	//QUESTION(Benjamin): Is this old code that was only used, when you didn't have
	//                    a real scheduler? If so, we could remove it now.
	//if (os_counter % 8 == 0)
		//os_currentTcb = &os_tcbs[2];
	//else if (os_counter % 4 == 0)
		//os_currentTcb = &os_tcbs[1];
	//else
		//os_currentTcb = &os_tcbs[0];
		
	//if (os_counter % 8 == 0 && os_tcbs[2].state == READY)
		//os_currentTcb = &os_tcbs[2];
	//else if (os_counter % 4 == 0 && os_tcbs[1].state == READY)
		//os_currentTcb = &os_tcbs[1];
	//else
		//os_currentTcb = &os_tcbs[0];
	
	// Should never get here...
	return E_OK;	
}

// This task is run, whenever no other task is ready to run.
TASK(Task_Idle)
{
	uint16_t i = 0;
	for (;;)
	{
		i++;
	}
}
