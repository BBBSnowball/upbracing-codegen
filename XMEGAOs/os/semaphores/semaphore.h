/*
 * semaphore.h
 *
 * Created: 10-Jul-12 12:18:04 PM
 *  Author: Krishna
 */ 


#ifndef SEMAPHORE_H_
#define SEMAPHORE_H_

struct  
{
	uint8_t token_count; // the count of how many tokens have been given out
	uint8_t token_base_value; //= 9000, the first token value
	uint8_t token_limit; // = 255, max tokens that can be given out
	uint8_t token_array[255]; // array holding tokens
}SEM_TOKEN;
//Initialize token_array: 9000-9255

typedef struct { //Declarations incomplete 
	int8_t count;
	uint8_t queue_end;
	uint8_t queue_cap;
	
	uint8_t queue [1];
}Semaphore;

/* Defining a semaphore with macro */
# define SEMAPHORE(name , queue_capacity ) \
	struct { Semaphore sem; uint8_t rest_of_queue[(queue_capacity)-1]; } SEM_##name##; SEM_##name##.count=1;SEM_##name##.queue_cap=(queue_capacity);SEM_##name##.queue_end=0
//TODO: check if above declaration is correct ?
//TODO: check if parameter passing is correct ?

/* Synchronous wait and signal */

#define sem_wait(name) _sem_wait(&SEM_##name.sem)
void _sem_wait(Semaphore* sem);

//void sem_wait(Semaphore name);
#define sem_signal(name) _sem_signal(&SEM_##name.sem)
void _sem_signal(Semaphore* sem);


/* Asynchronous waiting */

typedef uint8_t sem_token_t;
const sem_token_t SEM_TOKEN_SUCCESSFUL = 0;

//Start waiting on semaphore
#define sem_start_wait(name) _sem_start_wait(&SEM_##name##.sem)
sem_token_t _sem_start_wait (Semaphore name);

//Continue waiting for semaphore
#define sem_continue_wait(name, token) _sem_continue_wait(&SEM_##name##.sem, (token))
bool _sem_continue_wait (Semaphore name , sem_token_t token );

//Stop waiting for semaphore
#define sem_stop_wait(name, token) _sem_stop_wait(&SEM_##name##.sem, (token)/*##token / (token)?*/)
void _sem_stop_wait (Semaphore name , sem_token_t token );





/* Semaphores for Queue Synchronization */
typedef struct Semaphore_n{ /*Declarations incomplete */
	uint8_t count;
	
	uint8_t queue [1];
	}Semaphore_n;

#define SEMAPHORE_n(name , queue_capacity ) \
	struct { Semaphore sem; uint8_t rest_of_queue[(queue_capacity)-1]; } SEM_##name##_n /*_n? will name be replaced*/
 //Synchronous wait & signal
 
 #define sem_wait_n(name, n) _sem_wait_n(&SEM_##name##_n, n)
 void _sem_wait_n (SEMAPHORE_n name , uint8_t n);
 
 #define sem_signal_n(name, n) _sem_signal_n(&SEM_##name##_n, n)
 void _sem_signal_n (Semaphore_n name , uint8_t n);
 
 //Start waiting for queue
 #define sem_start_wait_n(name,n) _sem_start_wait_n(&SEM_##name##_n, n)
 sem_token_t _sem_start_wait_n (SEMAPHORE_n name , uint8_t n);
 
 //Continue waiting for queue
 #define sem_continue_wait_n(name, token) _sem_continue_wait_n(&SEM_##name##_n, token)
 bool _sem_continue_wait_n (Semaphore_n name , sem_token_t token );
 
 //Stop waiting for queue
  #define sem_stop_wait_n(name, token) _sem_stop_wait_n(&SEM_##name##_n, token)
 void _sem_stop_wait_n (SEMAPHORE_n name , sem_token_t token );


#endif /* SEMAPHORE_H_ */