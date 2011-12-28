/*
 * Os_cfg_generated.c
 *
 * Created: 27.12.2011 21:17:36
 *  Author: peer
 */ 

#include "Os_cfg_generated.h"

volatile Os_Tcb os_tcbs[OSEK_NUMBER_OF_TCBS] = 
{	
	{		 
		(StackPointerType *) 0x3DFF,	/* Top of stack	*/
		(StackPointerType *) 0x3DFF, /* Base address of the stack */
		BASIC, /* Task Type */
		READY, /* Task State */
		Task_Idle,	/* Function Pointer */
		0, /* Id/Priority */
		PREEMPTABLE,
	},				 				 
	{
		(StackPointerType *) 0x3BFF,									
		(StackPointerType *) 0x3BFF,							
		BASIC,											 
		SUSPENDED,										 
		Task_Update,									 
		1,
		PREEMPTABLE,												 
	},
	{		 
		(StackPointerType *) 0x39FF,	/* Top of stack	*/
		(StackPointerType *) 0x39FF, /* Base address of the stack */
		BASIC, /* Task Type */
		SUSPENDED, /* Task State */
		Task_Increment,	/* Function Pointer */
		2, /* Id/Priority */
		NONPREEMPTABLE,
	}													 
};

volatile Os_Alarm os_alarms[OSEK_NUMBER_OF_ALARMS] = 
{						
	{	// Alarm for Task_Update
		0,				
		{				
			65535,		
			1,		
			0,		    
		},				
		NULL,
		1,
		&os_tcbs[1],
	},					
	{	// Alarm for Task_Increment
		0,				
		{				
			65535,		
			64,			
			0,			
		},				
		NULL,
		1,	
		&os_tcbs[2],
	},	
	{	// Alarm for callback test
		0,			
		{				
			65535,		
			8192,		
			0,		    
		},				
		Alarm_Test,
		0,
		NULL,
	}				
};