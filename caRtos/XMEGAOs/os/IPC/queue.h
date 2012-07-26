/*
 * queue.h
 *
 * Created: 16-Jul-12 7:19:01 PM
 *  Author: Krishna
 */ 
#include "OSEK.h"
#include "semaphores/semaphore.h"

#ifndef QUEUE_H_
#define QUEUE_H_

//NOTE(Benjamin): Doesn't belong in here!
typedef uint8_t bool;


//NOTE(Benjamin): What should this struct do? I don't think that we have to
//                keep track of the processes owning a queue.
typedef struct
{
	TaskType id;
	uint8_t n;
}ipc_queue;

typedef struct  
{
	uint8_t queue_front; // Points to first element in queue; initially 0
	uint8_t queue_end; // Points to last element in queue; initially 0
	uint8_t capacity; // capacity of the queue
	uint8_t occupied; // number of bytes present in the queue
	
	uint8_t ipc_queue[1];
}Queue;
//NOTE(Benjamin): We have to add a parameter - the size of the semaphore queues.
#define QUEUE(name, capacity, reader_count, writer_count) \
		struct { Queue q; ipc_queue rest_ipc_queue[(capacity)-1]; } name##_QUEUE \
			= { { 0, 0, (capacity), 0 } }; \
		SEMAPHORE(name##_QUEUE_SEM, 1, (reader_count)+(writer_count)); \
		SEMAPHORE_N(name##_QUEUE_FREE, (capacity), (writer_count)); \
		SEMAPHORE_N(name##_QUEUE_AVAILABLE, 0, (reader_count));
		/* add the other semaphores here */
//TODO: check if above declaration is correct ?
//TODO: check if parameter passing is correct ?

// what is the max length of queue ?
//NOTE(Benjamin): I'm not sure whether I understand this question...
//                The maximum count of items in the waiting queue is queue_capacity. It
//                can be different for each semaphore instance. The maximum value of
//                queue_capacity is 255. You can reduce it a bit, if you have to. You
//                should note that in the documentation.

#define QUEUE_REF(name)     (&(name##_QUEUE).q)
#define QUEUE_SEM_REF(name) SEMAPHORE_REF(name##_QUEUE_SEM)
//NOTE(Benjamin): Define similar macros for the other semaphores.

//How to create queues ?
//implement macro overloading
//NOTE(Benjamin): not possible; you have to change the name

//NOTE(Benjamin): When you implement the queues, you will find out which semaphores you
//                need for each of the functions. Then you can add them as arguments, as
//                I have done here for queue_enqueue.
#define queue_enqueue (name, data) _queue_enqueue(QUEUE_REF(name), data, QUEUE_SEM_REF(name))
void _queue_enqueue(Queue* name, uint8_t data, Semaphore* sem);
#define queue_enqueue(name, bytes, data) _queue_enqueue(QUEUE_REF(name), bytes, data)
void _queue_enqueue(Queue* name, uint8_t bytes, const uint8_t* data);

#define queue_dequeue(name) _queue_dequeue(QUEUE_REF(name))
uint8_t _queue_dequeue(Queue* name);
#define  queue_dequeue(name, bytes, data_out) _queue_dequeue(QUEUE_REF(name), bytes, data_out)
void _queue_dequeue(Queue* name, uint8_t bytes, uint8_t* data_out);

#define queue_is_data_available(name, bytes) _queue_is_data_available(QUEUE_REF(name), bytes)
bool _queue_is_data_available (Queue* name, uint8_t number_of_bytes);

#define queue_has_free_space(name, bytes) _queue_has_free_space(QUEUE_REF(name), bytes)
bool _queue_has_free_space (Queue* name, uint8_t number_of_bytes);

#define sem_start_wait_data_available(name,n) _sem_start_wait_data_available(QUEUE_REF(name), n)
sem_token_t _sem_start_wait_data_available (Queue* name, uint8_t n);

#define sem_continue_wait_data_available(name, token) _sem_continue_wait_data_available(QUEUE_REF(name), token)
bool _sem_continue_wait_data_available (Queue* name , sem_token_t token );

#define sem_stop_wait_data_available(name, token) _sem_stop_wait_data_available(QUEUE_REF(name), token)
void _sem_stop_wait_data_available (Queue* name , sem_token_t token );

#define sem_start_wait_free_space(name, n) _sem_start_wait_free_space(QUEUE_REF(name), n)
sem_token_t _sem_start_wait_free_space (Queue* name , uint8_t n);

#define sem_continue_wait_free_space(name, token) _sem_continue_wait_free_space(QUEUE_REF(name), token)
bool _sem_continue_wait_free_space (Queue* name , sem_token_t token );

#define sem_stop_wait_data_free_space(name, token) _sem_stop_wait_data_free_space(QUEUE_REF(name), token)
void _sem_stop_wait_data_free_space (Queue* name , sem_token_t token );


#endif /* QUEUE_H_ */
