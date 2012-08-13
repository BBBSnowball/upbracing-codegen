/*
 * semaphore.h
 *
 * Created: 10-Jul-12 12:18:04 PM
 *  Author: Krishna
 */ 
#include "OSEK.h"

#ifndef SEMAPHORE_H_
#define SEMAPHORE_H_

//NOTE(Benjamin): This declaration doesn't belong to semaphores, so it should be in another
//                file, e.g. Platform_Types.h
typedef uint8_t bool;

//NOTE(Benjamin): In theory, we have to keep track of the tokens that we have given out.
//                However, this takes up too much memory. A token doesn't have to be
//                unique for the whole system - only for the its own semaphore. Each
//                semaphore has a list of waiting tokens, which contains all used tokens.
//                I suggest this solution: Each semaphore has a variable which contains
//                the token that will be used next. When a token is given out by start_wait,
//                the variable is incremented. When the counter rolls over to 0, it must be
//                set to the minimal token value instead. We hope that we don't get any token
//                clashes. In a later implementation, we would look through the queue of
//                waiting tokens to make sure that the token is not used, yet. If it is, we
//                simply use the next one.
struct  
{
	uint8_t token_count; // the count of how many tokens have been given out
	//NOTE(Benjamin): This is always the first ID which is not used for a task. Therefore, you
	//                can use the constant OS_NUMBER_OF_TCBS.
	//                It cannot be 9000, if you use a uint8_t.
	uint8_t token_base_value; //= 9000, the first token value
	//NOTE(Benjamin): This is always the maximum of the numeric type
	uint8_t token_limit; // = 255, max tokens that can be given out
	uint8_t token_array[255]; // array holding tokens
}SEM_TOKEN;
//Initialize token_array: 9000-9255

typedef struct { //Declarations incomplete 
	int8_t count;
	//NOTE(Benjamin): I think you need some more variables here. I won't change it, as
	//                this depends on the details of your queue implementation. I've
	//                copied this to Semaphore_n - when you change it, you have to
	//                update this declaration as well.
	uint8_t queue_end;
	uint8_t queue_cap;
	
	uint8_t queue [1];
} Semaphore;

/* Defining a semaphore with macro */
//NOTE(Benjamin): You need a few semaphores to implement the queue. I think
//                you need one normal semaphore and two of the "_n" type. I
//                have modified the semaphore declaration a bit, so you can
//                use it within a struct. I thought we could use this for the
//                queue implementation, but it seems that we cannot.
#define SEMAPHORE_DECL(name, initial_value, queue_capacity) \
		struct { Semaphore sem; uint8_t rest_of_queue[(queue_capacity)-1]; } name##_SEM
#define SEMAPHORE_INIT(name, initial_value, queue_capacity) \
		{ { (initial_value), 0, (queue_capacity) } }
#define SEMAPHORE(name, initial_value, queue_capacity) \
		SEMAPHORE_DECL(name, initial_value, queue_capacity) \
			= SEMAPHORE_INIT(name, initial_value, queue_capacity)
#define SEMAPHORE_REF(name) (&(name##_SEM).sem)
//TODO: check if above declaration is correct ?
//TODO: check if parameter passing is correct ?

// what is the max length of queue ?
//NOTE(Benjamin): I'm not sure whether I understand this question...
//                The maximum count of items in the waiting queue is queue_capacity. It
//                can be different for each semaphore instance. The maximum value of
//                queue_capacity is 255. You can reduce it a bit, if you have to. You
//                should note that in the documentation.

/* Synchronous wait and signal */

#define sem_wait(name) _sem_wait(SEMAPHORE_REF(name))
void _sem_wait(Semaphore* sem);

//void sem_wait(Semaphore name);
#define sem_signal(name) _sem_signal(SEMAPHORE_REF(name))
void _sem_signal(Semaphore* sem);


/* Asynchronous waiting */

typedef uint8_t sem_token_t;
const sem_token_t SEM_TOKEN_SUCCESSFUL = 0;

//Start waiting on semaphore
#define sem_start_wait(name) _sem_start_wait(SEMAPHORE_REF(name))
sem_token_t _sem_start_wait (Semaphore name);

//Continue waiting for semaphore
#define sem_continue_wait(name, token) _sem_continue_wait(SEMAPHORE_REF(name), (token))
bool _sem_continue_wait (Semaphore name , sem_token_t token );

//Stop waiting for semaphore
#define sem_stop_wait(name, token) _sem_stop_wait(SEMAPHORE_REF(name), (token)/*##token / (token)?*/)
void _sem_stop_wait (Semaphore name , sem_token_t token );





/* Semaphores for Queue Synchronization */

// entry in the queue of a Semaphore_n
typedef struct {
	// the waiting process or token
	sem_token_t pid;

	// how often is it waiting?
	// (parameter n of sem_wait_n)
	uint8_t n;
} Semaphore_n_queue_entry;

typedef struct Semaphore_n{ /*Declarations incomplete */
	uint8_t count;
	uint8_t queue_end;
	uint8_t queue_cap;
	
	Semaphore_n_queue_entry queue [1];
} Semaphore_n;

#define SEMAPHORE_n(name , queue_capacity ) \
	struct { Semaphore sem; Semaphore_n_queue_entry rest_of_queue[(queue_capacity)-1]; } name##_SEM_n \
		= { { (initial_value), 0, (queue_capacity) }, 0 };
//NOTE(Benjamin): Yes, name will be replaced in SEM_##name##_n

//Synchronous wait & signal
 
 #define sem_wait_n(name, n) _sem_wait_n(&name##_SEM_n, n)
 void _sem_wait_n (Semaphore_n* name , uint8_t n);
 
 #define sem_signal_n(name, n) _sem_signal_n(&name##_SEM_n, n)
 void _sem_signal_n (Semaphore_n* name , uint8_t n);
 
 //Start waiting for queue
 #define sem_start_wait_n(name,n) _sem_start_wait_n(&name##_SEM_n, n)
 sem_token_t _sem_start_wait_n (Semaphore_n* name , uint8_t n);
 
 //Continue waiting for queue
 #define sem_continue_wait_n(name, token) _sem_continue_wait_n(&name##_SEM_n, token)
 bool _sem_continue_wait_n (Semaphore_n* name , sem_token_t token );
 
 //Stop waiting for queue
 #define sem_stop_wait_n(name, token) _sem_stop_wait_n(&name##_SEM_n, token)
 void _sem_stop_wait_n (Semaphore_n* name , sem_token_t token );


#endif /* SEMAPHORE_H_ */
