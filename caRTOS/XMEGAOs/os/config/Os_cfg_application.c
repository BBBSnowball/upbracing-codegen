/*
 * Os_cfg_application.c
 *
 * This file defines application specific stuff, e.g. the task
 * control blocks.
 *
 * Generated automatically. DO NOT MODIFY! Change config.rb instead.
 */

#include "Os_cfg_application.h"

#define COUNT_OF(x) (sizeof(x) / sizeof(*(x)))

const uint8_t OS_NUMBER_OF_TCBS   = OS_NUMBER_OF_TCBS_DEFINE;
const uint8_t OS_NUMBER_OF_ALARMS = OS_NUMBER_OF_ALARMS_DEFINE;

static uint8_t Task_Idle_Stack[512];
#define TOP_OF_STACK_Idle \
	(Task_Idle_Stack + sizeof(Task_Idle_Stack) - 1)
static uint8_t Task_Update_Stack[512];
#define TOP_OF_STACK_Update \
	(Task_Update_Stack + sizeof(Task_Update_Stack) - 1)
static uint8_t Task_Increment_Stack[512];
#define TOP_OF_STACK_Increment \
	(Task_Increment_Stack + sizeof(Task_Increment_Stack) - 1)
static uint8_t Task_Shift_Stack[512];
#define TOP_OF_STACK_Shift \
	(Task_Shift_Stack + sizeof(Task_Shift_Stack) - 1)
static uint8_t Task_UsartTransmit_Stack[512];
#define TOP_OF_STACK_UsartTransmit \
	(Task_UsartTransmit_Stack + sizeof(Task_UsartTransmit_Stack) - 1)

volatile Os_Tcb os_tcbs[OS_NUMBER_OF_TCBS_DEFINE] =
{
	{		 
		TOP_OF_STACK_Idle,          /* current stack pointer */
		TOP_OF_STACK_Idle,          /* top of stack (upper end) */
		READY,                      /* Task State */
		TASKPTR(Idle),              /* Function Pointer */
		0,                          /* Id/Priority */
		PREEMPTABLE,                
	},
	{		 
		TOP_OF_STACK_Update,        /* current stack pointer */
		TOP_OF_STACK_Update,        /* top of stack (upper end) */
		SUSPENDED,                  /* Task State */
		TASKPTR(Update),            /* Function Pointer */
		1,                          /* Id/Priority */
		PREEMPTABLE,                
	},
	{		 
		TOP_OF_STACK_Increment,     /* current stack pointer */
		TOP_OF_STACK_Increment,     /* top of stack (upper end) */
		SUSPENDED,                  /* Task State */
		TASKPTR(Increment),         /* Function Pointer */
		2,                          /* Id/Priority */
		PREEMPTABLE,                
	},
	{		 
		TOP_OF_STACK_Shift,         /* current stack pointer */
		TOP_OF_STACK_Shift,         /* top of stack (upper end) */
		SUSPENDED,                  /* Task State */
		TASKPTR(Shift),             /* Function Pointer */
		3,                          /* Id/Priority */
		PREEMPTABLE,                
	},
	{		 
		TOP_OF_STACK_UsartTransmit, /* current stack pointer */
		TOP_OF_STACK_UsartTransmit, /* top of stack (upper end) */
		SUSPENDED,                  /* Task State */
		TASKPTR(UsartTransmit),     /* Function Pointer */
		4,                          /* Id/Priority */
		PREEMPTABLE,                
	},
};

volatile Os_Alarm os_alarms[OS_NUMBER_OF_ALARMS_DEFINE] =
{	{	// Alarm for Task_Update
		TASK_ID_Update,           // Task ID: Update
		ALARM_PHASE(0, 8),        // Current Value: phase = 0 system ticks = 0,00 ms
		8,                        // Ticks Per Base: f = 31,25 Hz, T = 32,00 ms
		//1,                      // Active state
	},
	{	// Alarm for Task_Increment
		TASK_ID_Increment,        // Task ID: Increment
		ALARM_PHASE(0, 128),      // Current Value: phase = 0 system ticks = 0,00 ms
		128,                      // Ticks Per Base: f = 1,95 Hz, T = 512,00 ms
		//1,                      // Active state
	},
	{	// Alarm for Task_Shift
		TASK_ID_Shift,            // Task ID: Shift
		ALARM_PHASE(0, 24),       // Current Value: phase = 0 system ticks = 0,00 ms
		24,                       // Ticks Per Base: f = 10,42 Hz, T = 96,00 ms
		//1,                      // Active state
	},
	{	// Alarm for Task_UsartTransmit
		TASK_ID_UsartTransmit,    // Task ID: UsartTransmit
		ALARM_PHASE(0, 1),        // Current Value: phase = 0 system ticks = 0,00 ms
		1,                        // Ticks Per Base: f = 250,00 Hz, T = 4,00 ms
		//1,                      // Active state
	},
};
