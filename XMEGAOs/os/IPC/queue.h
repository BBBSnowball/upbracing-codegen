/*
 * queue.h
 *
 * Created: 16-Jul-12 7:19:01 PM
 *  Author: Krishna (s.krishna1989@gmail.com)
 */ 
//#include "Os.h"
#include "semaphore.h"
#include "Platform_Types.h"

#ifndef QUEUE_H_
#define QUEUE_H_

//NOTE(Benjamin): What should this struct do? I don't think that we have to
//                keep track of the processes owning a queue.
//NOTE(Peer): Since this is not used anywhere, I removed it.
//typedef struct
//{
	//TaskType id;
	//uint8_t n;
//}ipc_queue;

typedef struct  
{
	int8_t queue_front; // Points to first element in queue; initially -1
	int8_t queue_end; // Points to last element in queue; initially 0
	int8_t capacity; // capacity of the queue
	int8_t occupied; // number of bytes present in the queue
	
	uint8_t q_queue[1];
} Queue;

//NOTE(Benjamin): We have to add a parameter - the size of the semaphore queues.
#define QUEUE(sem, capacity, reader_count, writer_count) \
		struct { Queue q; uint8_t rest_q_queue[(capacity)-1]; } sem##_QUEUE \
			= { { 0, 0, (capacity), 0 } }; \
		SEMAPHORE(sem##_QUEUE_SEM, 1, (reader_count)+(writer_count)); \
		SEMAPHORE_N(sem##_QUEUE_FREE, (capacity), (capacity)); \
		SEMAPHORE_N(sem##_QUEUE_AVAILABLE, (capacity), 0)
		/* add the other semaphores here */


// what is the max length of queue ?
//NOTE(Benjamin): I'm not sure whether I understand this question...
//                The maximum count of items in the waiting queue is queue_capacity. It
//                can be different for each semaphore instance. The maximum value of
//                queue_capacity is 255. You can reduce it a bit, if you have to. You
//                should note that in the documentation.

#define QUEUE_REF(sem)     &sem##_QUEUE.q
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

#define queue_enqueue (sem, data) \
_sem_wait_n(QUEUE_PROD_REF(sem), 1); \
_sem_wait(QUEUE_SEM_REF(sem)); \
_queue_enqueue(QUEUE_REF(sem), (const uint8_t*) data); \
_sem_signal(QUEUE_SEM_REF(sem)); \
_sem_signal_n(QUEUE_CONS_REF(sem), 1)
void _queue_enqueue(Queue* sem, uint8_t data);

#define queue_enqueue2(sem, bytes, data) \
_sem_wait_n(QUEUE_PROD_REF(sem), bytes); \
_sem_wait(QUEUE_SEM_REF(sem)); \
_queue_enqueue2(QUEUE_REF(sem), bytes, (const uint8_t*) data); \
_sem_signal(QUEUE_SEM_REF(sem)); \
_sem_signal_n(QUEUE_CONS_REF(sem), bytes)
void _queue_enqueue2(Queue* sem, uint8_t bytes, const uint8_t* data);

#define queue_dequeue(sem, output) \
_sem_wait_n(QUEUE_CONS_REF(sem), 1); \
_sem_wait(QUEUE_SEM_REF(sem)); \
_queue_dequeue(QUEUE_REF(sem), output); \
_sem_signal(QUEUE_SEM_REF(sem)); \
_sem_signal_n(QUEUE_PROD_REF(sem), 1)
void _queue_dequeue(Queue* sem, uint8_t* output);

#define  queue_dequeue2(sem, bytes, data_out) \
_sem_wait_n(QUEUE_CONS_REF(sem), bytes); \
_sem_wait(QUEUE_SEM_REF(sem)); \
_queue_dequeue2(QUEUE_REF(sem), bytes, data_out); \
_sem_signal(QUEUE_SEM_REF(sem)); \
_sem_signal_n(QUEUE_PROD_REF(sem), bytes)
void _queue_dequeue2(Queue* sem, uint8_t bytes, uint8_t* data_out);

#define queue_enqueue_async(sem, bytes, data_in) _queue_enqueue2(QUEUE_REF(sem), bytes, (const uint8_t*) data_in)

#define queue_dequeue_async(sem, bytes, data_out) _queue_dequeue2(QUEUE_REF(sem), bytes, data_out)

#define queue_is_data_available(q, bytes) _queue_is_data_available(QUEUE_REF(q), bytes)
bool _queue_is_data_available (Queue* q, uint8_t number_of_bytes);

#define queue_has_free_space(q, bytes) _queue_has_free_space(QUEUE_REF(q), bytes)
bool _queue_has_free_space (Queue* q, uint8_t number_of_bytes);

#define queue_start_wait_data_available(sem,n) _queue_start_wait_data_available(QUEUE_CONS_REF(sem), n)
sem_token_t _queue_start_wait_data_available (Semaphore_n* sem, uint8_t n);

#define queue_continue_wait_data_available(sem, token) _queue_continue_wait_data_available(QUEUE_CONS_REF(sem), QUEUE_REF(sem), token)
bool _queue_continue_wait_data_available (Semaphore_n* sem , Queue* que, sem_token_t token );

#define queue_stop_wait_data_available(sem, n, token) _queue_stop_wait_data_available(QUEUE_CONS_REF(sem), n, token)
void _queue_stop_wait_data_available (Semaphore_n* sem , uint8_t n, sem_token_t token );

#define queue_start_wait_free_space(sem, n) _queue_start_wait_free_space(QUEUE_PROD_REF(sem), n)
sem_token_t _queue_start_wait_free_space (Semaphore_n* sem , uint8_t n);

#define queue_continue_wait_free_space(sem, token) _queue_continue_wait_free_space(QUEUE_PROD_REF(sem), QUEUE_REF(sem), token)
bool _queue_continue_wait_free_space (Semaphore_n* sem ,Queue* que, sem_token_t token );

#define queue_stop_wait_data_free_space(sem, n, token) _queue_stop_wait_data_free_space(QUEUE_PROD_REF(sem), n, token)
void _queue_stop_wait_data_free_space (Semaphore_n* sem , uint8_t n, sem_token_t token );

#define queue_start_wait(que) _queue_start_wait(QUEUE_SEM_REF(que))
sem_token_t _queue_start_wait(Semaphore* sem);

#define queue_continue_wait(que, token) _queue_continue_wait(QUEUE_SEM_REF(que), token)
bool _queue_continue_wait(Semaphore* sem, sem_token_t token);

#define queue_stop_wait(que, token) _queue_stop_wait(QUEUE_SEM_REF(que), token)
_queue_stop_wait(Semaphore* sem, sem_token_t token);

#endif /* QUEUE_H_ */