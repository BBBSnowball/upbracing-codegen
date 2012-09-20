/*
 * semaphore.c
 *
 * Created: 10-Jul-12 1:19:32 PM
 *  Author: Krishna
 */ 
#include "semaphore.h"
#include "queue.h"
#include "OSEK.h"
#include <util/delay.h>
#include "OSEK_Kernel.h"


//NOTE(Benjamin): Your function signatures are wrong. Please make sure that you copy them from the header file. Otherwise, the
//                program will crash. You have to use a pointer because only then can you change the variable.

void _sem_wait(Semaphore* sem){
	//Definition check needed
	TaskType t;
	GetTaskID(&t);
	
	//while(name->queue_cap <= name->count{ 
		///* Check if the capacity of the queue is less than or equal to the end of the queue - this is illegal/queue is full */
		////add sleep function here
		////NOTE(Benjamin): This shouldn't happen. If it does, this is an error in the design of the system. Therefore, we should
		////                report it to the error handling function. If it returns, we can use this loop. The loop is a really
		////                good idea.
		////TODO use a sleep function which is provided by the OS, so we don't waste processing time
		//_delay_ms(1);
	//}
	//Checking of queue head to be done in next implementation.
	
	
		OS_ENTER_CRITICAL();
		sem->count--;
		//if (sem->count == -(sem->queue_cap))
		//{
			////system failure
		//}
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
			OS_EXIT_CRITICAL();	
			// Wait
			WaitTask();
		}
		
		
		
		//To be suspended.
}


void _sem_signal(Semaphore* sem){
		
	sem->count++;
	//if (sem->count > 1)
	//{
		////System failure
	//}
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
		//if (sem->queue[sem->queue_front] <= OS_NUMBER_OF_TCBS)
		//	ActivateTask(sem->queue[sem->queue_front]);
		//call event(semaphore_event, taskid);
		
		// Free the first task (make READY)
		if (sem->queue[sem->queue_front] <= OS_NUMBER_OF_TCBS)
		{
			// Ready
			SignalTask(sem->queue[sem->queue_front]);
		}			
	}
	
}


sem_token_t _sem_start_wait(Semaphore* sem){
	
	uint8_t i,tok;
	//for (i=0;i<=255;i++)
	//{
		//if (SEM_TOKEN.token_array[i] != 0)
		//{
			//tok = SEM_TOKEN.token_array[i];
			//SEM_TOKEN.token_array[i]=0;
			//SEM_TOKEN.token_count++;
			//break;
		//}
	//}
	//Token system for universal tokens. Maybe of use later.
	
	tok = sem->token_count++;
	if(sem->token_count == 0) sem->token_count = 65280;
	
	//while(sem->queue_cap <= sem->queue_end){
		////add sleep function here
		//Mst return error, look at wait()
	//}
	
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
	
	uint8_t i,tok;
	tok = token;
	if (tok == 0)
	{
		return TRUE;
	}
	
	//for (i=0;i<=255;i++)
	//{
		//if (tok == SEM_TOKEN.token_array[i])
		//{
			////return "invalid token/harmful execution"
		//}
	//}
	//Not using global tokens
	
	if (tok == sem->queue[sem->queue_front]) 
	{
		return TRUE;
	}
	return FALSE;
	
}



void _sem_stop_wait(Semaphore* sem, sem_token_t token){
	//Definition pending
	uint8_t i,j,k,tok;
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
	}
	
	i = sem->queue_front;
	while(i != sem->queue_end)
	{
		if (sem->queue_end < 0)
		{
			sem->queue_end = sem->queue_cap;
		}
		
		if (i == sem->queue_cap){i=0;}
		
		if (sem->queue[i]==tok)
		{
			j=i;
			while (j != sem->queue_end)
			{
				if (j==sem->queue_cap){j=0;}
				k = j+1;
				if (k==sem->queue_cap){k=0;}
				sem->queue[j]=sem->queue[k];
				
				j++;
			}
			 
			
			sem->queue_end--;
			
			
			//SEM_TOKEN.token_array[SEM_TOKEN.token_base_value-tok] = tok;
			//SEM_TOKEN.token_count--;		//Not using global tokens
		}
		i++;
	}
	
}


/*	Semaphore synchronization for queues*/
/*	@brief	Performs wait for queue semaphore*/
void _sem_wait_n(Semaphore_n* sem , uint8_t n){
	//Definition pending
	TaskType t;
	GetTaskID(&t);
	
	OS_ENTER_CRITICAL();
	sem->count--;
	//if (sem->count == -(sem->queue_cap))
	//{
		////system failure
	//}
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
		sem->queue[sem->queue_end].n = n;
		sem->queue[sem->queue_end].pid = t;
			//wait
	}
	OS_EXIT_CRITICAL();
}

void _sem_signal_n(Semaphore_n* sem, uint8_t n){
	//Definition pending
	sem->count++;
	//if (sem->count > 1)
	//{
		////System failure
	//}
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
	//Definition pending
	uint8_t i,tok;
	tok = sem->token_count++;
	if (sem->token_count == 0){ sem->token_count = 65280;	}
	
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
	//Definition pending
	if (token == 0)
	{
		return TRUE;
	}
	
	if (sem->queue[sem->queue_front].pid == token)
	{
		return TRUE;
		//remove token id from front
	}
	return FALSE;
}

void _sem_stop_wait_n(Semaphore_n* sem, sem_token_t token){
	//Definition pending
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
	}
	
	for (i=0; i<= sem->queue_end; i++)
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
	}
}
