/*
 * semaphore.c
 *
 * Created: 10-Jul-12 1:19:32 PM
 *  Author: Krishna
 */ 
#include "semaphore.h"
#include "OSEK.h"
#include <util\delay.h>

//NOTE(Benjamin): It is very good that you write documentation for your functions. However, I think that it should be in the
//                header file, as other programmers won't look at the c file, when they want to learn about the interface. Of
//                course, you can describe the details of your implementation in the c file.

//NOTE(Benjamin): Your comments look like they are meant to be processed by some automatic system similar to JavaDoc. It looks
//                like Doxygen, but for that you have to start your comments with two stars instead of one, so Doxygen will
//                know that they are special.
//                see http://www.stack.nl/~dimitri/doxygen/docblocks.html#specialblock

//NOTE(Benjamin): Your function signatures are wrong. Please make sure that you copy them from the header file. Otherwise, the
//                program will crash. You have to use a pointer because only then can you change the variable.

//NOTE(Benjamin): I get the impression that there are quite a few bugs in your implementation. We aren't the first ones who
//                implement semaphores. Please look at pseudo code in books or at existing implementations! That way you will
//                not only save yourself a lot of work, but we will also have fewer bugs.

//NOTE(Benjamin): The parameter "name" contains the semaphore. On the macro, this parameter name makes sense. For the functions, you
//                should consider calling it "sem" or something similar, as it contains a pointer to the semaphore and not its name.
//                Please update the documentation accordingly.

/*	@brief Performs wait operation on semaphore
	
	@param[in] name		Semaphore name on which wait is performed
	
	@detail	Wait function decrements the value of semaphore count by 1. If the count is negative, the task is added to the end of the queue.
*/
void _sem_wait(Semaphore name){
	//Definition check needed
	TaskRefType t;
	GetTaskID(&t);
	//NOTE(Benjamin): We shouldn't check that before we know that we need the queue -> first decrement and check the name->count
	while(name->queue_cap <= name->queue_end){ 
		/* Check if the capacity of the queue is less than or equal to the end of the queue - this is illegal/queue is full */
		//add sleep function here
		//NOTE(Benjamin): This shouldn't happen. If it does, this is an error in the design of the system. Therefore, we should
		//                report it to the error handling function. If it returns, we can use this loop. The loop is a really
		//                good idea.
		//TODO use a sleep function which is provided by the OS, so we don't waste processing time
		_delay_ms(1);
	}
	//NOTE(Benjamin): What does this condition do? I don't understand the comment!
	if (name->queue_end < name->queue_cap) // critical section wrap in task or here ?
	{
		OS_ENTER_CRITICAL();
		name->count--;
		if (name->count < 0) 
		{
			name->queue_end++;
			name->queue[name->queue_end] = t; 
		}	
		OS_EXIT_CRITICAL();	
	}
	//NOTE(Benjamin): You mustn't run into this loop, if the semaphore has been granted immediately.
	//NOTE(Benjamin): A semaphore mustn't do "busy waiting". Instead, it must use a OS function to disable the current
	//                task, so it doesn't waste any processing time while waiting. sem_signal will unblock the first
	//                process in the queue (if it is really a process (smaller than OS_NUMBER_OF_TCBS) and not a token).
	while (name->queue[0] != t)
	/* Check if the head of the queue is this task, i.e., if this task has been granted semaphore. Sleep in between. */
	{
		//_delay_ms(1);
		//call for suspend event
	}
}

/*	@brief Performs signal operation on semaphore

	@param[in]name		Semaphore name on which signal is performed
	
	@detail	Signal function increments the semaphore count. If the count is negative, the next blocking task is put into the ready queue.
*/
void _sem_signal(Semaphore name){
	//Definition check
	uint8_t i;
	
	name->count++;
	//NOTE(Benjamin): This is very slow. For a first implementation, this may be ok. However, you have to replace
	//                this by a ring buffer.
	for (i=0;i<=name->queue_end;i++)
	{
		name->queue[i]=name->queue[i+1];
	}
	name->queue_end--;
	if (name->count < 0)
	{
		//if (name->queue[0] < OS_NUMBER_OF_TCBS)
		//	ActivateTask(name->queue[0]);
		//call event(semaphore_event, taskid);
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
	if (tok == 0)
	{
		return true;
	}
	
	for (i=0;i<=255;i++)
	{
		if (tok == SEM_TOKEN.token_array[i])
		{
			//return "invalid token/harmful execution"
		}
	}
	
	if (tok == name->queue[0]) // should this be like signal or just check if sem has granted? if latter, then will signal() be called by task after finishing ?
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
