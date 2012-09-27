/*
 * semaphore.c
 *
 * Created: 10-Jul-12 1:19:32 PM
 *  Author: Krishna
 */ 
#include "semaphore.h"
#include "queue.h"
#include "Os.h"
//#include <util/delay.h>
#include "Os_Kernel.h"
#include <avr/interrupt.h>

const sem_token_t SEM_TOKEN_SUCCESSFUL = 0;

void _sem_wait(Semaphore* sem)
{
	OS_ENTER_CRITICAL();
	
	//Definition check needed
	TaskType t;
	GetTaskID(&t);
	
	sem->count--;
		
	if (sem->count < 0) 
	{
		sem->queue[sem->queue_end] = t; 
		sem->queue_end++;
		if (sem->queue_end == sem->queue_cap)
		{
			sem->queue_end = 0;
		}
			
		// Another operation is going on: wait
		WaitTask(t);
	}	
}

void _sem_signal(Semaphore* sem)
{
	// Get number of waiting tasks for this resource:
	uint8_t nrOfTasks = 0;
	if (sem->queue_end >= sem->queue_front) 
	{
		nrOfTasks = sem->queue_end - sem->queue_front;
	}
	else 
	{
		nrOfTasks = sem->queue_end + (sem->queue_cap - sem->queue_front);	
	}
		
	sem->count++;
	
	// Only continue, if tasks are waiting AND the resource is free
	if (sem->count > 0 && nrOfTasks > 0)
	{
		// Wake task waiting for this semaphore:
		ResumeTask(sem->queue[sem->queue_front]);
	
		sem->queue_front++;
		if (sem->queue_front == sem->count)
		{
			sem->queue_front = 0;
		}
	}
	
	OS_EXIT_CRITICAL();
}

/*	Semaphore synchronization for queues*/
/*	@brief	Performs wait for queue semaphore*/
void _sem_wait_n(Semaphore_n* sem , uint8_t n)
{
	OS_ENTER_CRITICAL();
	
	TaskType t;
	GetTaskID(&t);
	
	// Is there enough free space available?
	if (sem->count < n)
	{
		// No.
		// 1) Store semaphore in this queue
		sem->queue[sem->queue_end].pid = t;
		sem->queue[sem->queue_end].n = n;
		sem->queue_end++;
		if (sem->queue_end == sem->queue_cap)
		{
			sem->queue_end = 0;
		}
		
		// 2) Block this task since there is not enough free space
		WaitTask(t);
	}
	
	// We have a First-Come-First-Serve scenario here:
	// -> reserve the necessary or at least the available space.
	sem->count -= n;
	
	// If we got here, no blocking was necessary.
}

void _sem_signal_n(Semaphore_n* sem, uint8_t n)
{
	// Get number of waiting tasks for this resource:
	uint8_t nrOfTasks = 0;
	if (sem->queue_end >= sem->queue_front) 
	{
		nrOfTasks = sem->queue_end - sem->queue_front;
	}
	else 
	{
		nrOfTasks = sem->queue_end + (sem->queue_cap - sem->queue_front);	
	}

	// Signalize free space:
	sem->count += n;
	
	// Is there a task waiting for this resource?
	// And: Is there enough resource available for the waiting task?
	if (nrOfTasks > 0 && sem->count >= sem->queue[sem->queue_front].n) 
	{
		// Yes, there are.
			
		// Wake the task waiting for this semaphore:
		// TODO: Replace ActivateTask() with ImmediatelyResumeTask()
		//ActivateTask(sem->queue[sem->queue_front].pid);
		ResumeTask(sem->queue[sem->queue_front].pid);
		
		// Increment pointer to next semaphore queue entry
		sem->queue_front++;
		if (sem->queue_front == sem->queue_cap)
		{
			sem->queue_front = 0;
		}
	}	
	
	OS_EXIT_CRITICAL();
}

sem_token_t _sem_start_wait(Semaphore* sem)
{
	uint8_t tok;
	
	tok = sem->token_count++;
	if(sem->token_count == 0) sem->token_count = 65280;
	
		
	OS_ENTER_CRITICAL();
	sem->count--;
	if (sem->count < 0)
	{
		if (sem->queue_end == sem->queue_cap)
		{
			sem->queue_end = 0;
		}
		else
		{
			sem->queue_end++;
		}
		sem->queue[sem->queue_end] = tok;
			
			
	}
	OS_EXIT_CRITICAL();
	return tok;
	//if no need of waiting what to return ?
}

bool _sem_continue_wait(Semaphore* sem , sem_token_t token)
{
	uint8_t tok;
	tok = token;
	if (tok == 0)
	{
		return TRUE;
	}
	
		
	if (tok == sem->queue[sem->queue_front]) 
	{
		return TRUE;
	}
	return FALSE;
	
}

void _sem_stop_wait(Semaphore* sem, sem_token_t token)
{
	uint8_t i,j,tok;
	tok = token;
	
	if (sem->queue[sem->queue_front] == tok)
	{
		if (sem->queue_front == sem->count)
		{
			sem->queue_front = 0;
		}
		else
		{
			sem->queue_front++;
		}
		//activatetask(sem->queue[sem->queue_front])
		return;
	}
	
	
	while(i != sem->queue_end)
	{
		if (sem->queue[i]==tok)
		{
			for (j=i;j<=sem->queue_end;j++)
			{
				sem->queue[j]=sem->queue[j+1];
			}
			if (sem->queue_end==0)
			{
				sem->queue_end = sem->queue_cap;
			} 
			else
			{
				sem->queue_end--;
			}
			
			
		}
		if (i==sem->queue_cap){i=0;}else{i++;}
		
	}
	
}

sem_token_t _sem_start_wait_n(Semaphore_n* sem, uint8_t n)
{
	OS_EXIT_CRITICAL();
	
	uint8_t tok;
	tok = sem->token_count++;
	if(sem->token_count == 0) sem->token_count = 65280;
	
	sem->count--;
	if (sem->count < 0)
	{
		if (sem->queue_end == sem->queue_cap)
		{
			sem->queue_end = 0;
		}
		else
		{
			sem->queue_end++;
		}
		sem->queue[sem->queue_end].pid = tok;
		sem->queue[sem->queue_end].n = n;
		//no suspend
		
	}
	
	OS_EXIT_CRITICAL();
	return tok;
}

bool _sem_continue_wait_n(Semaphore_n* sem, sem_token_t token)
{
	if (token == 0)
	{
		return TRUE;
	}
	
	if (sem->queue[sem->queue_front].pid == token)
	{
		return TRUE;
	}
	return FALSE;
}

void _sem_stop_wait_n(Semaphore_n* sem, sem_token_t token)
{
	uint8_t i,j,tok;
	tok = token;
	
	if (sem->queue[sem->queue_front].pid == tok)
	{
		if (sem->queue_front == sem->count)
		{
			sem->queue_front = 0;
		}
		else
		{
			sem->queue_front++;
		}
		//activatetask(sem->queue[sem->queue_front].pid)
		return;
	}
	
	while(i != sem->queue_end)
	{
		if (sem->queue[i].pid==tok)
		{
			for (j=i;j<=sem->queue_end;j++)
			{
				sem->queue[j]=sem->queue[j+1];
			}
			if (sem->queue_end==0)
			{
				sem->queue_end = sem->queue_cap;
			}
			else
			{
				sem->queue_end--;
			}
			
		}
		if (i==sem->queue_cap){i=0;}else{i++;}
	}
}
