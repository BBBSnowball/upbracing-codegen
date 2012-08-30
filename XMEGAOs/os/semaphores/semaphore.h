/*
 * semaphore.h
 *
 * Created: 10-Jul-12 12:18:04 PM
 *  Author: Krishna
 */ 
#include "OSEK.h"
#include "Platform_Types.h"

#ifndef SEMAPHORE_H_
#define SEMAPHORE_H_

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
//NOTE(Krishna):  Global tokens have been disabled and semaphore specific tokens have been declared.
//struct  
//{
	//uint8_t token_count; // the count of how many tokens have been given out
	////NOTE(Benjamin): This is always the first ID which is not used for a task. Therefore, you
	////                can use the constant OS_NUMBER_OF_TCBS.
	////                It cannot be 9000, if you use a uint8_t.
	//uint8_t token_base_value; //= 9000, the first token value
	////NOTE(Benjamin): This is always the maximum of the numeric type
	//uint8_t token_limit; // = 255, max tokens that can be given out
	//uint8_t token_array[255]; // array holding tokens
//}SEM_TOKEN;
//Global tokens not used.
//Initialize token_array: 9000-9255

typedef struct { //Declarations incomplete 
	int8_t count;
	uint16_t token_count; // = 65280, will roll over to 0 when tokens exhausted. then reset to 65280.
	int8_t queue_front;
	int8_t queue_end;
	int8_t queue_cap;
	
	uint8_t queue [1];
} Semaphore;

/*****************************************SEMAPHPHORE CAPACITY IS ONLY 127 TASKS *********************************/
//Because of the usage of int8_t for queue_front, queue_end and queue_cap.

/* Defining a semaphore with macro */

#define SEMAPHORE_DECL(sem, initial_value, queue_capacity) \
		struct { Semaphore sem; uint8_t rest_of_queue[(queue_capacity)-1]; } sem##_SEM
#define SEMAPHORE_INIT(sem, initial_value, queue_capacity) \
		{ { (initial_value), 65280, -1, 0, (queue_capacity) } }
#define SEMAPHORE(sem, initial_value, queue_capacity) \
		SEMAPHORE_DECL(sem, initial_value, queue_capacity) \
			= SEMAPHORE_INIT(sem, initial_value, queue_capacity)
#define SEMAPHORE_REF(sem) (&(sem##_SEM).sem)


/* Synchronous wait and signal */

/*	@brief Performs wait operation on semaphore
	
	@param[in] name		Semaphore name on which wait is performed
	
	@detail	Wait function decrements the value of semaphore count by 1. If the count is negative, the task is added to the end of the queue.
*/
#define sem_wait(sem) _sem_wait(SEMAPHORE_REF(sem))
void _sem_wait(Semaphore* sem);

/*	@brief Performs signal operation on semaphore

	@param[in]name		Semaphore name on which signal is performed
	
	@detail	Signal function increments the semaphore count. If the count is negative, the next blocking task is put into the ready queue.
*/
#define sem_signal(sem) _sem_signal(SEMAPHORE_REF(sem))
void _sem_signal(Semaphore* sem);


/* Asynchronous waiting */

typedef uint8_t sem_token_t;
const sem_token_t SEM_TOKEN_SUCCESSFUL = 0;

/*	@brief	Start waiting on semaphore asynchronously

	@param[in]name		Semaphore name on which asynchronous wait is performed
	
	@return				Semaphore token, to be used for sem_continue_wait()
	
	@detail Start waiting on a semaphore . 
			If it returns zero , access to the semaphore have been granted . 
			Otherwise , it returns a token that you can use with sem_continue_wait.
*/
#define sem_start_wait(sem) _sem_start_wait(SEMAPHORE_REF(sem))
sem_token_t _sem_start_wait (Semaphore* sem);

/*	@brief Continue waiting for semaphore

	@param[in]name		Semaphore name whose availability is checked
	@param[in]token		Semaphore token, returned by sem_start_wait(), with which semaphore availability is checked
	
	@return				True, if semaphore granted. False, if semaphore not granted.
	
	@detail	Query the state of an asynchronous waiting token . 
			If the semaphore has been granted to the owner of the token, a non-zero value is returned. In that case , you mustn't use the token again . 
			If false (or zero ) is returned , you must try again later. It returns true without further action, if the token is zero.
*/
#define sem_continue_wait(sem, token) _sem_continue_wait(SEMAPHORE_REF(sem), (token))
bool _sem_continue_wait (Semaphore* sem , sem_token_t token );

/*	@brief	Stop waiting on semaphore

	@param[in]name		Semaphore name whose availability is not needed
	@param[in]token		Semaphore token of the corresponding semaphore
	
	@detail	Stop waiting for a semaphore. 
			You mustn't use the token after calling this function. 
			It does nothing , if the token is zero.
*/
#define sem_stop_wait(sem, token) _sem_stop_wait(SEMAPHORE_REF(sem), (token))
void _sem_stop_wait (Semaphore* sem , sem_token_t token );



/* Semaphores for Queue Synchronization */

// entry in the queue of a Semaphore_n
typedef struct {
	// the waiting process or token
	sem_token_t pid;

	// how often is it waiting?
	// (parameter n of sem_wait_n)
	uint8_t n;
} Semaphore_n_queue_entry;

typedef struct { 
	int8_t count;
	uint8_t token_count; //= 65280, will roll over to 0 when tokens exhausted. then reset to 65280. 
	int8_t queue_front;
	int8_t queue_end;
	uint8_t queue_cap;
	
	Semaphore_n_queue_entry queue [1];
} Semaphore_n;

#define SEMAPHORE_n(sem , queue_capacity ) \
	struct { Semaphore sem; Semaphore_n_queue_entry rest_of_queue[(queue_capacity)-1]; } sem##_SEM_n \
		= { { (initial_value), 0, (queue_capacity) }, 0 };
#define SEMAPHORE_REF_N(sem) (&(sem##_SEM_n).sem)

 #define sem_wait_n(sem, n) _sem_wait_n(SEMAPHORE_REF_N(sem), n)
 void _sem_wait_n (Semaphore_n* sem , uint8_t n);
 
 #define sem_signal_n(sem, n) _sem_signal_n(&sem##_SEM_n, n)
 void _sem_signal_n (Semaphore_n* sem , uint8_t n);
 
 //Start waiting for queue
 #define sem_start_wait_n(sem,n) _sem_start_wait_n(&sem##_SEM_n, n)
 sem_token_t _sem_start_wait_n (Semaphore_n* sem, uint8_t n);
 
 //Continue waiting for queue
 #define sem_continue_wait_n(sem, token) _sem_continue_wait_n(&sem##_SEM_n, token)
 bool _sem_continue_wait_n (Semaphore_n* sem, sem_token_t token );
 
 //Stop waiting for queue
 #define sem_stop_wait_n(sem, token) _sem_stop_wait_n(&sem##_SEM_n, token)
 void _sem_stop_wait_n (Semaphore_n* sem, sem_token_t token );


#endif /* SEMAPHORE_H_ */
