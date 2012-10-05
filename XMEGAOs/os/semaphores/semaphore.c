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
	TaskType t;
	GetTaskID(&t);
	
	OS_ENTER_CRITICAL();
		
	if (sem->count < 1) 
	{
		sem->queue[sem->queue_end] = t; 
		sem->queue_end++;
		if (sem->queue_end == sem->queue_cap)
		{
			sem->queue_end = 0;
		}
			
		// Another operation is going on: wait
		WaitTask(t);
		
		OS_ENTER_CRITICAL();
	}	
	
	sem->count--;
	OS_EXIT_CRITICAL();
}

void _sem_signal(Semaphore* sem)
{
	// Get number of waiting tasks for this resource:
	//uint8_t nrOfTasks = 0;
	
	OS_ENTER_CRITICAL();
	
	//if (sem->queue_end >= sem->queue_front) 
	//{
		//nrOfTasks = sem->queue_end - sem->queue_front;
	//}
	//else 
	//{
		//nrOfTasks = sem->queue_end + (sem->queue_cap - sem->queue_front);	
	//}
		
	sem->count++;
	
	// Only continue, if tasks are waiting AND the resource is free
	if (sem->queue_end != sem->queue_front && sem->count > 0)
	{
		uint8_t tId = sem->queue[sem->queue_front];
		
		// Increment pointer to next semaphore queue entry
		sem->queue_front++;
		if (sem->queue_front == sem->queue_cap)
		{
			sem->queue_front = 0;
		}
		
		// Wake the task waiting for this semaphore:
		// TODO: Replace ActivateTask() with ImmediatelyResumeTask()
		//ActivateTask(sem->queue[sem->queue_front].pid);
		//To Peer: Resume task only if the front of the queue contains task id.
		//			do not do anything, if it is token
		ResumeTask(tId);
	}
	
	OS_EXIT_CRITICAL();
}

/*	Semaphore synchronization for queues*/
/*	@brief	Performs wait for queue semaphore*/
void _sem_wait_n(Semaphore_n* sem , uint8_t n)
{
	//OS_ENTER_CRITICAL();
	
	TaskType t;
	GetTaskID(&t);
	
	OS_ENTER_CRITICAL();
	
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
		
		OS_ENTER_CRITICAL();
	}
	
	// We have a First-Come-First-Serve scenario here:
	// -> reserve the necessary or at least the available space.
	sem->count -= n;
	
	// If we got here, no blocking was necessary.
	OS_EXIT_CRITICAL();
}

void _sem_signal_n(Semaphore_n* sem, uint8_t n)
{
	// Get number of waiting tasks for this resource:
	//uint8_t nrOfTasks = 0;
	
	OS_ENTER_CRITICAL();
	
	//if (sem->queue_end >= sem->queue_front) 
	//{
		//nrOfTasks = sem->queue_end - sem->queue_front;
	//}
	//else 
	//{
		//nrOfTasks = sem->queue_end + (sem->queue_cap - sem->queue_front);	
	//}

	// Signalize free space:
	sem->count += n;
	
	// Is there a task waiting for this resource?
	// And: Is there enough resource available for the waiting task?
	if (sem->queue_end != sem->queue_front && sem->count >= sem->queue[sem->queue_front].n) 
	{
		uint8_t tId = sem->queue[sem->queue_front].pid;
		
		// Increment pointer to next semaphore queue entry
		sem->queue_front++;
		if (sem->queue_front == sem->queue_cap)
		{
			sem->queue_front = 0;
		}
		
		// Wake the task waiting for this semaphore:
		// TODO: Replace ActivateTask() with ImmediatelyResumeTask()
		//ActivateTask(sem->queue[sem->queue_front].pid);
		ResumeTask(tId);
	}	
	
	OS_EXIT_CRITICAL();
}

sem_token_t _sem_start_wait(Semaphore* sem)
{
	uint8_t tok = 0;
		
	OS_ENTER_CRITICAL();
	sem->count--;
	if (sem->count < 0)
	{
		tok = sem->token_count++;
		if(sem->token_count == 0) sem->token_count = OS_NUMBER_OF_TCBS_DEFINE;
		sem->queue[sem->queue_end] = tok;
		
		sem->queue_end++;
		if (sem->queue_end == sem->queue_cap)
		{
			sem->queue_end = 0;
		}
			
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
	uint8_t i,j,k,tok;
	tok = token;
	
	if (tok == 0)
	{
		sem->count++;
		return;
	}
	
	if (sem->queue[sem->queue_front] == tok)
	{
		sem->count++;
		sem->queue_front++;
		if (sem->queue_front == sem->queue_cap)
		{
			sem->queue_front = 0;
		}
		
		
		//if front is not token, activate task
		if (sem->queue[sem->queue_front] < OS_NUMBER_OF_TCBS_DEFINE)
		{
			ResumeTask(sem->queue[sem->queue_front]);
		}
		
		return;
	}
	
	i = sem->queue_front;
	while(i != sem->queue_end)
	{
		if (sem->queue[i]==tok)
		{
			j=i;
			while (j != sem->queue_end)
			{
				k = j+1;
				if (k == sem->queue_cap){k=0;}
				
				sem->queue[j]=sem->queue[k];
				j=k;
			}
			sem->queue_end--;
			if (sem->queue_end < 0)
			{
				sem->queue_end = sem->queue_cap-1;
			} 
			sem->count++;
			
			
		}
		if (i==sem->queue_cap){i=0;}else{i++;}
		
	}
	
}

sem_token_t _sem_start_wait_n(Semaphore_n* sem, uint8_t n)
{
	OS_EXIT_CRITICAL();
	
	uint8_t tok = 0;
	
	
	
	if (sem->count < n)
	{
		tok = sem->token_count++;
		if(sem->token_count == 0) sem->token_count = OS_NUMBER_OF_TCBS_DEFINE;
		
		sem->queue[sem->queue_end].pid = tok;
		sem->queue[sem->queue_end].n = n;
		
		sem->queue_end++;
		if (sem->queue_end == sem->queue_cap)
		{
			sem->queue_end = 0;
		}
		
		
		//no suspend
		
	}
	sem->count -= n;
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

void _sem_stop_wait_n(Semaphore_n* sem, uint8_t n, sem_token_t token)
{
	uint8_t i,j,k,tok;
	tok = token;
	
	if (tok == 0)
	{
		sem->count += n;
		return;
	}
	
	if (sem->queue[sem->queue_front].pid == tok)
	{
		sem->count = sem->count + sem->queue[sem->queue_front].n;
		if (sem->queue_front == sem->queue_cap-1)
		{
			sem->queue_front = 0;
		}
		else
		{
			sem->queue_front++;
		}
		
		//if front is not token, activate task
		if (sem->queue[sem->queue_front].pid < OS_NUMBER_OF_TCBS_DEFINE)
		{
			ResumeTask(sem->queue[sem->queue_front].pid);
		}
		return;
	}
	
	i = sem->queue_front;
	while(i != sem->queue_end)
	{
		if (sem->queue[i].pid==tok)
		{
			sem->count = sem->count + sem->queue[i].n;
			j=i;
			while (j != sem->queue_end)
			{
				k = j+1;
				if (k == sem->queue_cap) k=0;
				
				sem->queue[j]=sem->queue[k];
				j=k;
			}
			if (sem->queue_end==0)
			{
				sem->queue_end = sem->queue_cap-1;
			}
			else
			{
				sem->queue_end--;
			}
			
		}
		if (i==sem->queue_cap){i=0;}else{i++;}
	}
}
