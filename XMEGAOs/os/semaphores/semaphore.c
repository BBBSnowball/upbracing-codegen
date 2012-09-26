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


const sem_token_t SEM_TOKEN_SUCCESSFUL = 0;

void _sem_wait(Semaphore* sem){
	//Definition check needed
	TaskType t;
	GetTaskID(&t);
	
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
			sem->queue[sem->queue_end] = t; 
			
			//To be suspended.
		}	
		OS_EXIT_CRITICAL();
}


void _sem_signal(Semaphore* sem){
		
	sem->count++;
	
	if (sem->queue_front == sem->count)
	{
		sem->queue_front = 0;
	}
	else
	{
		sem->queue_front++;
	}
	
	if (sem->count <= 0)
	{
		//if (sem->queue[sem->queue_front] <= OS_NUMBER_OF_TCBS)
		//	ActivateTask(sem->queue[sem->queue_front]);
		//call event(semaphore_event, taskid);
	}
	
}


sem_token_t _sem_start_wait(Semaphore* sem){
	
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



bool _sem_continue_wait(Semaphore* sem , sem_token_t token){
	
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



void _sem_stop_wait(Semaphore* sem, sem_token_t token){
	
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


/*	Semaphore synchronization for queues*/
/*	@brief	Performs wait for queue semaphore*/
void _sem_wait_n(Semaphore_n* sem , uint8_t n){
	
	TaskType t;
	GetTaskID(&t);
	
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
		sem->queue[sem->queue_end].pid = t;
		sem->queue[sem->queue_end].n = n;
		//Suspend here
	}
	OS_EXIT_CRITICAL();
}

void _sem_signal_n(Semaphore_n* sem, uint8_t n){
	
	sem->count++;
	
	if (sem->queue_front == sem->queue_cap)
	{
		sem->queue_front = 0;
	}
	else
	{
		sem->queue_front++;
	}
	
	if (sem->count <= 0)
	{
		//if (sem->queue[sem->queue_front].pid <= OS_NUMBER_OF_TCBS)
		//	ActivateTask(sem->queue[sem->queue_front].pid);
		//If n number of bytes are not there, what to do ?
		
	}
	
}

sem_token_t _sem_start_wait_n(Semaphore_n* sem, uint8_t n){
	
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
		sem->queue[sem->queue_end].pid = tok;
		sem->queue[sem->queue_end].n = n;
		//no suspend
		
	}
	OS_EXIT_CRITICAL();
	return tok;
}

bool _sem_continue_wait_n(Semaphore_n* sem, sem_token_t token){
	
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

void _sem_stop_wait_n(Semaphore_n* sem, sem_token_t token){
	
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
