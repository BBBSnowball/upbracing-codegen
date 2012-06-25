/*
 * Os_cfg_generated.c
 *
 * Created: 27.12.2011 21:17:36
 *  Author: peer
 */ 

#include "Os_cfg_generated.h"

extern volatile Os_Tcb os_tcbs[OS_NUMBER_OF_TCBS] = 
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
		NONPREEMPTABLE,
	}													 
};

extern volatile Os_Alarm os_alarms[OS_NUMBER_OF_ALARMS] = 
{						
	{	// Alarm for Task_Update
		1,				// Task ID: Update
		0,				// Current Value
		1,				// Ticks Per Base			
		//1,				// Active state
	},					
	{	// Alarm for Task_Increment
		2,				// Task ID: Increment
		0,				// Current Value
		4,			// Ticks Per Base		
		//1,				// Active state
	}			
};