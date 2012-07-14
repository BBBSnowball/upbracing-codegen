/*
 * semaphore.c
 *
 * Created: 10-Jul-12 1:19:32 PM
 *  Author: Krishna
 */ 
#include "semaphore.h"


/*	@brief Performs wait operation on semaphore
	
	@param[in] name		Semaphore name on which wait is performed
	
	@detail	Wait function decrements the value of semaphore count by 1. If the count is negative, the task is added to the end of the queue.
*/
void _sem_wait(Semaphore name){
	//Definition pending
	while(name->queue_cap <= name->queue_end){ /* Check if the capacity of the queue is less than the end of the queue - this is illegal */
		//add sleep function here	
	}
	
	
	if (name->queue_end < name->queue_cap) // location for critical section wrap
	{
		name->count--;
		if (name->count < 0) 
		{
			name->queue_end++;
			name->queue[name->queue_end] = GetTaskID(os_currentTcb->id); // How to get reference of self task pointer ?
			//save context here ?
			//TerminateTask();
			//load context here ?
			//will the program resume here to load context back ? 
			//if it loads back here, why do we need load context ? then is save context needed ?
		}		
	}
}

/*	@brief Performs signal operation on semaphore

	@param[in]name		Semaphore name on which signal is performed
	
	@detail	Signal function increments the semaphore count. If the count is negative, the next blocking task is put into the ready queue.
*/
void _sem_signal(Semaphore name){
	//Definition Pending
	uint8_t i;
	
	name->count++;
	for (i=0;i<=name->queue_end;i++)
	{
		name->queue[i]=name->queue[i+1];
	}
	name->queue_end--;
	if (name->count < 0)
	{
		ActivateTask(name->queue[0]);
	}
	
}

/*	@brief	Start waiting on semaphore asynchronously

	@param[in]name		Semaphore name on which asynchronous wait is performed
	
	@return				Semaphore token, to be used for sem_continue_wait()
	
	@detail Start waiting on a semaphore . 
			If it returns zero , access to the semaphore have been granted . 
			Otherwise , it returns a token that you can use with sem_continue_wait.
*/
sem_token_t _sem_start_wait(Semaphore name){
	//Definition pending
	//Since there are only 255 tasks possible, i am not checking for overflow.
	uint8_t i,tok;
	for (i=0;i<=255;i++)
	{
		if (SEM_TOKEN.token_array[i] != 0)
		{
			tok = SEM_TOKEN.token_array[i];
			SEM_TOKEN.token_array[i]=0;
			SEM_TOKEN.token_count++;
			break;
		}
	}
	while(name->queue_cap <= name->queue_end){
		//add sleep function here
	}
	if (name->queue_end < name->queue_cap) // location for critical section wrap
	{
		name->count--;
		if (name->count < 0)
		{
			name->queue_end++;
			name->queue[name->queue_end] = tok;
			return tok;
			
		}
	}
	//if no need of waiting what to return ?
}


/*	@brief Continue waiting for semaphore

	@param[in]name		Semaphore name whose availability is checked
	@param[in]token		Semaphore token, returned by sem_start_wait(), with which semaphore availability is checked
	
	@return				True, if semaphore granted. False, if semaphore not granted.
	
	@detail	Query the state of an asynchronous waiting token . 
			If the semaphore has been granted to the owner of the token, a non-zero value is returned. In that case , you mustn't use the token again . 
			If false (or zero ) is returned , you must try again later. It returns true without further action, if the token is zero.
*/
bool _sem_continue_wait(Semaphore name , sem_token_t token){
	//Definition pending
	//Should i check if token is valid or not ?
	uint8_t i,tok;
	tok = token;
	for (i=0;i<=255;i++)
	{
		if (tok == SEM_TOKEN.token_array[i])
		{
			//return "invalid token/harmful execution"
		}
	}
	
	if (tok == name->queue[0])
	{
		name->count++;
		for (i=0;i<=name->queue_end;i++)
		{
			name->queue[i]=name->queue[i+1];
		}
		SEM_TOKEN.token_array[SEM_TOKEN.token_base_value-tok] = tok;
		SEM_TOKEN.token_count--;
		return true;
	}
	return false;
	
}


/*	@brief	Stop waiting on semaphore

	@param[in]name		Semaphore name whose availability is not needed
	@param[in]token		Semaphore token of the corresponding semaphore
	
	@detail	Stop waiting for a semaphore. 
			You mustn't use the token after calling this function. 
			It does nothing , if the token is zero.
*/
void _sem_stop_wait(Semaphore name, sem_token_t token){
	//Definition pending
	uint8_t i,j,tok;
	tok = token;
	
	for (i=0; i<= name->queue_end; i++)
	{
		if (name->queue[i]==tok)
		{
			for (j=i;j<=name.queue_end;j++)
			{
				name->queue[j]=name->queue[j+1];
			}
			SEM_TOKEN.token_array[SEM_TOKEN.token_base_value-tok] = tok;
			SEM_TOKEN.token_count--;
		}
	}
	
}


/*	Semaphore synchronization for queues*/
/*	@brief	Performs wait for queue semaphore*/
void sem_wait_n(SEMAPHORE_n name , uint8_t n){
	//Definition pending
}

void sem_signal_n(Semaphore_n name , uint8_t n){
	//Definition pending
}

sem_token_t sem_start_wait_n(SEMAPHORE_n name , uint8_t n){
	//Definition pending
}

bool sem_continue_wait_n(Semaphore_n name , sem_token_t token){
	//Definition pending
}

void sem_stop_wait_n(SEMAPHORE_n name , sem_token_t token){
	//Definition pending
}