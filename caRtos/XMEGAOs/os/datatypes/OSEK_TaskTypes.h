/*
 * OSEK_TaskTypes.h
 *
 * Created: 27.12.2011 20:51:13
 *  Author: peer
 */ 


#ifndef OSEK_TASKTYPES_H_
#define OSEK_TASKTYPES_H_

#include "Platform_Types.h"

#define TASK(id) void id(void)

// OSEK Task Management (see OS223.pdf -> p.49)
// Assumption: no more than 256 tasks
// TaskType (just the id)
typedef uint8_t TaskType;

// TaskRefType
typedef TaskType * TaskRefType;

// TaskStateType
typedef enum
{
	RUNNING = 0,
	READY = 1,
	SUSPENDED = 2,
	WAITING = 3,
} TaskStateType;

// TaskStateRefType
typedef TaskStateType * TaskStateRefType;

// Preemptible?
typedef enum
{
	PREEMPTABLE = 0,
	NONPREEMPTABLE = 1
} TaskPreemptable;

// OS_Tcb
typedef struct 
{
	volatile StackPointerType *topOfStack;		// Points to the item that was pushed last
	volatile StackPointerType *baseOfStack;		// Base address of the stack
	TaskStateType state;						// State of current task (Suspended/Ready/Running/Waiting)
	TaskFunctionPointerType func;				// Pointer to task function
	//QUESTION(Benjamin): Do we need a task id in the struct? Can't we simply use the position in the array as an id?
	TaskType id;								// ID of this task
	TaskPreemptable preempt;
	
} Os_Tcb;

#endif /* OSEK_TASKTYPES_H_ */
