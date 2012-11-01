/*
 * Os_Task.h
 *
 * Created: 27.12.2011 20:34:33
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 


#ifndef OS_TASK_H_
#define OS_TASK_H_

#include "datatypes/Os_StatusTypes.h"
#include "datatypes/Os_TaskTypes.h"

extern volatile Os_Tcb * os_currentTcb;
extern volatile Os_Tcb os_tcbs[];

#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
/* Simple priority "queue":
 * - Just an array of bools */
extern uint8_t os_ready_queue[];
#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
#error Multiple activations for basic tasks, multiple tasks per priority
#endif

#define OS_RESET_CONTEXT()		\
	asm volatile(	/* Reset stack pointer for this task */						\
					"lds r26, os_currentTcb		\n\t"							\
					"lds r27, os_currentTcb + 1	\n\t"							\
					"adiw r26, 2				\n\t"							\
					"ld __tmp_reg__, x+					\n\t"					\
					"out __SP_L__, __tmp_reg__			\n\t"							\
					"ld __tmp_reg__, x+					\n\t"							\
					"out __SP_H__, __tmp_reg__			\n\t"							\
					/* Get task function address */								\
					"lds r26, os_currentTcb		\n\t"							\
					"lds r27, os_currentTcb + 1	\n\t"							\
					"adiw r26, 5                \n\t"							\
					/* Push the function address at the very top position */	\
					"ld __tmp_reg__, x+					\n\t"							\
					"push __tmp_reg__					\n\t"							\
					"ld __tmp_reg__, x+					\n\t"							\
					"push __tmp_reg__					\n\t"							\
					/* Clear zero reg, store init SREG and clear context */		\
					"clr __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"ldi r16, 128				\n\t"							\
					"push r16					\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					"push __zero_reg__			\n\t"							\
					:															\
					:															\
					/* Clobber list */											\
					: "r16", "r26", "r27"						\
	)

//////////////////////////////////////////////////////////////////////////
// Function:  Os_InitializeTaskContext                                  //
// Returns:   void                                                      //
// Parameter: Os_TcbType * tcb                                          //     
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Initializes the stack of a given TCB for first time use.             //
//////////////////////////////////////////////////////////////////////////					
void Os_InitializeTaskContext(Os_Tcb *tcb);

//////////////////////////////////////////////////////////////////////////
// Function:  TerminateTask                                             //
// Returns:   void                                                      //
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Suspends the current task and cleans up stack space.                 //
//////////////////////////////////////////////////////////////////////////
void TerminateTask(void);

//////////////////////////////////////////////////////////////////////////
// Function:  GetTaskID                                                 //
// Returns:   ID of the running task                                    //
// Parameter: Pointer to return value                                   //
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Returns the ID of the running task.                                  //
//////////////////////////////////////////////////////////////////////////
StatusType GetTaskID(TaskRefType taskId);

//////////////////////////////////////////////////////////////////////////
// Function:  GetTaskState                                              //
// Returns:   State of the running task                                 //
// Parameter: Pointer to return value                                   //
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Returns the state of the running task.                               //
//////////////////////////////////////////////////////////////////////////
StatusType GetTaskState(TaskStateRefType state);

//////////////////////////////////////////////////////////////////////////
// Function:  ActivateTask                                              //
// Parameter: ID of the task to activate                                //
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Sets the state of a task to ready and marks this in the              //
// ready-flag array accordingly.                                        //
//////////////////////////////////////////////////////////////////////////
StatusType ActivateTask(TaskType taskId);

StatusType WaitTask(TaskType taskId) __attribute__ ( (naked) );

StatusType ResumeTask(TaskType taskId) __attribute__ ( (naked) );

#endif /* OS_TASK_H_ */
