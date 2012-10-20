/*
 * queue.h
 */
#include "datatypes/Platform_Types.h"
#include "semaphores/semaphore.h"

#ifndef QUEUE_H_
#define QUEUE_H_

typedef struct  
{
	int8_t queue_front; // Points to first element in queue; initially -1
	int8_t queue_end; // Points to last element in queue; initially 0
	int8_t capacity; // capacity of the queue
	//TODO we don't need that! -> semaphores make sure that we never
	//     enqueue/dequeue at an inappropriate time
	int8_t occupied; // number of bytes present in the queue
	
	uint8_t q_queue[1];
} Queue;

//NOTE(Benjamin): We have to add a parameter - the size of the semaphore queues.
#define QUEUE(name, capacity, reader_count, writer_count) \
		struct type_for_##name##_QUEUE { Queue q; uint8_t rest_q_queue[(capacity)-1]; } name##_QUEUE \
			= { { 0, 0, (capacity), 0 } }; \
		SEMAPHORE(name##_QUEUE_MUTEX, 1, (reader_count)+(writer_count)); \
		SEMAPHORE_N(name##_QUEUE_FREE, (writer_count), (capacity)); \
		SEMAPHORE_N(name##_QUEUE_AVAILABLE, (reader_count), 0)
		/* add the other semaphores here */

//NOTE(Benjamin): We have to add a parameter - the size of the semaphore queues.
#define QUEUE_EXTERNAL(name) \
		extern struct type_for_##name##_QUEUE { Queue q; } name##_QUEUE; \
		SEMAPHORE_EXTERNAL(name##_QUEUE_MUTEX); \
		SEMAPHORE_EXTERNAL_N(name##_QUEUE_FREE); \
		SEMAPHORE_EXTERNAL_N(name##_QUEUE_AVAILABLE)
		/* add the other semaphores here */

// what is the max length of queue ?
//NOTE(Benjamin): I'm not sure whether I understand this question...
//                The maximum count of items in the waiting queue is queue_capacity. It
//                can be different for each semaphore instance. The maximum value of
//                queue_capacity is 255. You can reduce it a bit, if you have to. You
//                should note that in the documentation.

#define QUEUE_REF(name)       &name##_QUEUE.q
#define QUEUE_MUTEX_REF(name) SEMAPHORE_REF(name##_QUEUE_MUTEX)
#define QUEUE_PROD_REF(name)  SEMAPHORE_REF_N(name##_QUEUE_FREE)
#define QUEUE_CONS_REF(name)  SEMAPHORE_REF_N(name##_QUEUE_AVAILABLE)


// blocks until enough space is available
#define queue_enqueue(name, data) \
		_queue_enqueue(QUEUE_REF(name), QUEUE_PROD_REF(name), \
				QUEUE_CONS_REF(name), QUEUE_MUTEX_REF(name), data)
void _queue_enqueue(Queue* q, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q, uint8_t data);

// blocks until enough space is available
#define queue_enqueue_many(name, count, data) \
		_queue_enqueue_many(QUEUE_REF(name), QUEUE_PROD_REF(name), \
				QUEUE_CONS_REF(name), QUEUE_MUTEX_REF(name), count, data)
void _queue_enqueue_many(Queue* q, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q,
		uint8_t count, const uint8_t* data);

// blocks until data is available
#define queue_dequeue(name) \
		_queue_dequeue(QUEUE_REF(name), QUEUE_PROD_REF(name), \
				QUEUE_CONS_REF(name), QUEUE_MUTEX_REF(name))
uint8_t _queue_dequeue(Queue* q, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q);

// blocks until data is available
#define queue_dequeue_many(name, count, data) \
		_queue_dequeue_many(QUEUE_REF(name), QUEUE_PROD_REF(name), \
				QUEUE_CONS_REF(name), QUEUE_MUTEX_REF(name), count, data)
void _queue_dequeue_many(Queue* q, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q,
		uint8_t count, uint8_t* data);

// asynchronous waiting on the queue
// (similar to the semaphore operations)

// start an enqueue/dequeue operation
#define queue_start_enqueue(name, no_of_bytes) \
		sem_start_wait_n(QUEUE_PROD_REF(name), no_of_bytes)
#define queue_start_dequeue(name, no_of_bytes) \
		sem_start_wait_n(QUEUE_CONS_REF(name), no_of_bytes)

// is the queue ready for the operation?
#define queue_continue_enqueue(name, token) \
		sem_continue_wait_n(QUEUE_PROD_REF(name), token)
#define queue_continue_dequeue(name, token) \
		sem_continue_wait_n(QUEUE_CONS_REF(name), token)

// finish the operation
// The parameter no_of_bytes mustn't be greater than the
// no_of_bytes parameter that has been passed to
// queue_start_{en,de}queue. If you pass a big value to
// the start function, your program might have to wait
// longer than necessary (including forever).
#define queue_finish_enqueue(name, token, no_of_bytes, data) \
		_queue_finish_enqueue(QUEUE_REF(name), QUEUE_PROD_REF(name), \
				QUEUE_CONS_REF(name), QUEUE_MUTEX_REF(name), count, data)
#define queue_finish_dequeue(name, token, no_of_bytes, data) \
		_queue_finish_dequeue(QUEUE_REF(name), QUEUE_PROD_REF(name), \
				QUEUE_CONS_REF(name), QUEUE_MUTEX_REF(name), count, data)
void _queue_finish_enqueue(Queue* sem, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q, sem_token_t token,
		uint8_t no_of_bytes, const uint8_t* data);
void _queue_finish_dequeue(Queue* sem, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q, sem_token_t token,
		uint8_t no_of_bytes,       uint8_t* data);

// abort waiting
#define queue_abort_enqueue(name, token) \
		sem_abort_wait_n(QUEUE_PROD_REF(name), token)
#define queue_abort_dequeue(name, token) \
		sem_abort_wait_n(QUEUE_CONS_REF(name), token)

//TODO we might want a function that changes the reservation
//     size for a token


#endif /* QUEUE_H_ */
