/*
 * OSEK_Task.h
 *
 * Created: 27.12.2011 20:34:33
 *  Author: peer
 */ 


#ifndef OSEK_TASK_H_
#define OSEK_TASK_H_

#include "OSEK_StatusTypes.h"
#include "OSEK_TaskTypes.h"

extern volatile Os_Tcb * os_currentTcb;
extern volatile Os_Tcb os_tcbs[];

#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
/* Simple priority "queue":
 * - Just an array of bools */
extern uint8_t os_ready_queue[OS_NUMBER_OF_TCBS];
#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
#error Multiple activations for basic tasks, multiple tasks per priority
#endif

// Terminates the running task
//************************************
// Method:    TerminateTask
// FullName:  TerminateTask
// Access:    public 
// Returns:   StatusType
// Qualifier:
// Parameter: void
//************************************
StatusType TerminateTask(void);

// Returns the id of the current task
//************************************
// Method:    GetTaskID
// FullName:  GetTaskID
// Access:    public 
// Returns:   StatusType
// Qualifier:
// Parameter: TaskRefType taskId
//************************************
StatusType GetTaskID(TaskRefType taskId);

// Returns the state of current task
//************************************
// Method:    GetTaskState
// FullName:  GetTaskState
// Access:    public 
// Returns:   StatusType
// Qualifier:
// Parameter: TaskType taskId
// Parameter: TaskStateRefType state
//************************************
StatusType GetTaskState(TaskType taskId, TaskStateRefType state);

StatusType ActivateTask(TaskType taskId);

#endif /* OSEK_TASK_H_ */