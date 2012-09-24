/*
 * Os_cfg_generated.c
 *
 * Created: 27.12.2011 21:17:36
 *  Author: peer
 */ 

#include "Os_cfg_application.h"

#define COUNT_OF(x) (sizeof(x) / sizeof(*(x)))

const uint8_t OS_NUMBER_OF_TCBS   = OS_NUMBER_OF_TCBS_DEFINE;
const uint8_t OS_NUMBER_OF_ALARMS = OS_NUMBER_OF_ALARMS_DEFINE;

volatile Os_Tcb os_tcbs[OS_NUMBER_OF_TCBS_DEFINE] =
{	
	{		 
		(StackPointerType *) 0x08FF,	/* Top of stack	*/
		(StackPointerType *) 0x08FF, /* Base address of the stack */
		READY, /* Task State */
		Task_Idle,	/* Function Pointer */
		0, /* Id/Priority */
		PREEMPTABLE,
	},				 				 
	{
		(StackPointerType *) 0x06FF,									
		(StackPointerType *) 0x06FF,																	 
		SUSPENDED,										 
		Task_Update,									 
		1,
		PREEMPTABLE,												 
	},
	{		 
		(StackPointerType *) 0x04FF,	/* Top of stack	*/
		(StackPointerType *) 0x04FF, /* Base address of the stack */
		SUSPENDED, /* Task State */
		Task_Increment,	/* Function Pointer */
		2, /* Id/Priority */
		PREEMPTABLE,
	},
	{
		(StackPointerType *) 0x02FF,	/* Top of stack */
		(StackPointerType *) 0x02FF,	/*Base address of stack */
		SUSPENDED, /* Task State */
		Task_Shift,
		3, /* ID/Priority */
		PREEMPTABLE,
	}													 
};

volatile Os_Alarm os_alarms[OS_NUMBER_OF_ALARMS_DEFINE] =
{						
	{	// Alarm for Task_Update
		1,				// Task ID: Update
		0,				// Current Value
		2,				// Ticks Per Base	
	},					
	{	// Alarm for Task_Increment
		2,				// Task ID: Increment
		0,				// Current Value
		2,				// Ticks Per Base		
	},
	{	// Alarm for Task_Shift
		3,				// Task ID: Shift
		0,				// Current Value
		2,				// Ticks Per Base
	}			
};
