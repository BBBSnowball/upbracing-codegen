/*
 * queue.h
 *
 * Created: 16-Jul-12 7:19:01 PM
 *  Author: Krishna (s.krishna1989@gmail.com)
 */
#include "datatypes/Platform_Types.h"
#include "semaphores/semaphore.h"

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


// blocks until enough space is available
#define queue_enqueue (sem, data) \
		_queue_enqueue(QUEUE_REF(sem), QUEUE_PROD_REF(sem), \
				QUEUE_SEM_REF(sem), QUEUE_CONS_REF(sem), data)
void _queue_enqueue(Queue* sem, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q, uint8_t data);

// blocks until enough space is available
#define queue_enqueue_many(sem, count, data) \
		_queue_enqueue_many(QUEUE_REF(sem), QUEUE_PROD_REF(sem), \
				QUEUE_SEM_REF(sem), QUEUE_CONS_REF(sem), count, data)
void _queue_enqueue_many(Queue* q, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q,
		uint8_t count, const uint8_t* data);

// blocks until data is available
#define queue_dequeue (sem) \
		_queue_dequeue(QUEUE_REF(sem), QUEUE_PROD_REF(sem), \
				QUEUE_SEM_REF(sem), QUEUE_CONS_REF(sem))
uint8_t _queue_dequeue(Queue* sem, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q);

// blocks until data is available
#define queue_dequeue_many(sem, count, data) \
		_queue_dequeue_many(QUEUE_REF(sem), QUEUE_PROD_REF(sem), \
				QUEUE_SEM_REF(sem), QUEUE_CONS_REF(sem), count, data)
void _queue_dequeue_many(Queue* q, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q,
		uint8_t count, uint8_t* data);

// asynchronous waiting on the queue
// (similar to the semaphore operations)

// start an enqueue/dequeue operation
#define queue_start_enqueue(name, no_of_bytes) \
		sem_start_wait_n(QUEUE_PROD_REF(sem), no_of_bytes)
#define queue_start_dequeue(name, no_of_bytes) \
		sem_start_wait_n(QUEUE_CONS_REF(sem), no_of_bytes)

// is the queue ready for the operation?
#define queue_continue_enqueue(name, token) \
		sem_continue_wait_n(QUEUE_PROD_REF(sem), token)
#define queue_continue_dequeue(name, token) \
		sem_continue_wait_n(QUEUE_CONS_REF(sem), token)

// finish the operation
// The parameter no_of_bytes mustn't be greater than the
// no_of_bytes parameter that has been passed to
// queue_start_{en,de}queue. If you pass a big value to
// the start function, your program might have to wait
// longer than necessary (including forever).
#define queue_finish_enqueue(name, token, no_of_bytes, data) \
		_queue_finish_enqueue(QUEUE_REF(sem), QUEUE_PROD_REF(sem), \
				QUEUE_SEM_REF(sem), QUEUE_CONS_REF(sem), count, data)
#define queue_finish_dequeue(name, token, no_of_bytes, data) \
		_queue_finish_dequeue(QUEUE_REF(sem), QUEUE_PROD_REF(sem), \
				QUEUE_SEM_REF(sem), QUEUE_CONS_REF(sem), count, data)
void _queue_finish_enqueue(Queue* sem, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q, sem_token_t token,
		uint8_t no_of_bytes, const uint8_t* data);
void _queue_finish_dequeue(Queue* sem, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q, sem_token_t token,
		uint8_t no_of_bytes,       uint8_t* data);

// abort waiting
#define queue_abort_enqueue(name, token) \
		sem_abort_wait_n(QUEUE_PROD_REF(sem), token)
#define queue_abort_dequeue(name, token) \
		sem_abort_wait_n(QUEUE_CONS_REF(sem), token)

//TODO we might want a function that changes the reservation
//     size for a token


#endif /* QUEUE_H_ */
