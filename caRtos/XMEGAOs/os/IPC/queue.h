/*
 * queue.h
 *
 * Created: 16-Jul-12 7:19:01 PM
 *  Author: Krishna
 */ 
#include "OSEK.h"
#include "semaphores/semaphore.h"
#include "Platform_Types.h"

#ifndef QUEUE_H_
#define QUEUE_H_

//NOTE(Benjamin): What should this struct do? I don't think that we have to
//                keep track of the processes owning a queue.
typedef struct
{
	TaskType id;
	uint8_t n;
}ipc_queue;


typedef struct  
{
	int8_t queue_front; // Points to first element in queue; initially -1
	int8_t queue_end; // Points to last element in queue; initially 0
	int8_t capacity; // capacity of the queue
	int8_t occupied; // number of bytes present in the queue
	
	uint8_t q_queue[1];
}Queue;
//NOTE(Benjamin): We have to add a parameter - the size of the semaphore queues.
#define QUEUE(sem, capacity, reader_count, writer_count) \
		struct { Queue q; uint8_t rest_q_queue[(capacity)-1]; } sem##_QUEUE \
			= { { 0, 0, (capacity), 0 } }; \
		SEMAPHORE(sem##_QUEUE_SEM, 1, (reader_count)+(writer_count)); \
		SEMAPHORE_N(sem##_QUEUE_FREE, (capacity), (writer_count)); \
		SEMAPHORE_N(sem##_QUEUE_AVAILABLE, 0, (reader_count));
		/* add the other semaphores here */
//TODO: check if above declaration is correct ?
//TODO: check if parameter passing is correct ?

// what is the max length of queue ?
//NOTE(Benjamin): I'm not sure whether I understand this question...
//                The maximum count of items in the waiting queue is queue_capacity. It
//                can be different for each semaphore instance. The maximum value of
//                queue_capacity is 255. You can reduce it a bit, if you have to. You
//                should note that in the documentation.

#define QUEUE_REF(sem)     (&(sem##_QUEUE).q)
#define QUEUE_SEM_REF(sem) SEMAPHORE_REF(sem##_QUEUE_SEM)
#define QUEUE_PROD_REF(sem) SEMAPHORE_REF_N(sem##_QUEUE_FREE)
#define QUEUE_CONS_REF(sem) SEMAPHORE_REF_N(sem##_QUEUE_AVAILABLE)
//NOTE(Benjamin): Define similar macros for the other semaphores.

//How to create queues ?
//implement macro overloading
//NOTE(Benjamin): not possible; you have to change the name

//NOTE(Benjamin): When you implement the queues, you will find out which semaphores you
//                need for each of the functions. Then you can add them as arguments, as
//                I have done here for queue_enqueue.
#define queue_enqueue (sem, data) _queue_enqueue(QUEUE_REF(sem), data, QUEUE_SEM_REF(sem))
void _queue_enqueue(Queue* sem, uint8_t data);
#define queue_enqueue2(sem, bytes, data) _queue_enqueue2(QUEUE_REF(sem), bytes, data)
void _queue_enqueue2(Queue* sem, uint8_t bytes, const uint8_t* data);

#define queue_dequeue(sem) _queue_dequeue(QUEUE_REF(sem))
uint8_t _queue_dequeue(Queue* sem);
#define  queue_dequeue2(sem, bytes, data_out) _queue_dequeue2(QUEUE_REF(sem), bytes, data_out)
void _queue_dequeue2(Queue* sem, uint8_t bytes, uint8_t* data_out);

#define queue_is_data_available(sem, bytes) _queue_is_data_available(QUEUE_CONS_REF(sem), bytes)
bool _queue_is_data_available (Queue* sem, uint8_t number_of_bytes);

#define queue_has_free_space(sem, bytes) _queue_has_free_space(QUEUE_PROD_REF(sem), bytes)
bool _queue_has_free_space (Queue* sem, uint8_t number_of_bytes);

#define queue_start_wait_data_available(sem,n) _queue_start_wait_data_available(QUEUE_CONS_REF(sem), n)
sem_token_t _queue_start_wait_data_available (Semaphore_n* sem, uint8_t n);

#define queue_continue_wait_data_available(sem, token) _queue_continue_wait_data_available(QUEUE_CONS_REF(sem), QUEUE_REF(sem), token)
bool _queue_continue_wait_data_available (Semaphore_n* sem , Queue* que, sem_token_t token );

#define queue_stop_wait_data_available(sem, token) _queue_stop_wait_data_available(QUEUE_CONS_REF(sem), token)
void _queue_stop_wait_data_available (Semaphore_n* sem , sem_token_t token );

#define queue_start_wait_free_space(sem, n) _queue_start_wait_free_space(QUEUE_PROD_REF(sem), n)
sem_token_t _queue_start_wait_free_space (Semaphore_n* sem , uint8_t n);

#define queue_continue_wait_free_space(sem, token) _queue_continue_wait_free_space(QUEUE_PROD_REF(sem), QUEUE_REF(sem), token)
bool _queue_continue_wait_free_space (Semaphore_n* sem ,Queue* que, sem_token_t token );

#define queue_stop_wait_data_free_space(sem, token) _queue_stop_wait_data_free_space(QUEUE_PROD_REF(sem), token)
void _queue_stop_wait_data_free_space (Semaphore_n* sem , sem_token_t token );


#endif /* QUEUE_H_ */