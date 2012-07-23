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

typedef uint8_t bool;
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
#define QUEUE(name, capacity) \
		struct { Queue q; ipc_queue rest_ipc_queue[(capacity)-1]; }QUEUE_##name##; QUEUE_##name##.capacity=(capacity); QUEUE_##name##.queue_front=0; QUEUE_##name##.queue_end=0; QUEUE_##name##.occupied=0
//TODO: check if above declaration is correct ?
//TODO: check if parameter passing is correct ?
// what is the max length of queue ?

//How to create queues ?
//implement macro overloading

#define queue_enqueue (name, data) _queue_enqueue(&QUEUE_##name##.q, data)
void _queue_enqueue(Queue* name, uint8_t data);
#define queue_enqueue(name, bytes, data) _queue_enqueue(&QUEUE_##name##.q, bytes, data)
void _queue_enqueue(Queue* name, uint8_t bytes, const uint8_t* data);

#define queue_dequeue(name) _queue_dequeue(&QUEUE_##name##.q)
uint8_t _queue_dequeue(Queue* name);
#define  queue_dequeue(name, bytes, data_out) _queue_dequeue(&QUEUE_##name##.q, bytes, data_out)
void _queue_dequeue(Queue* name, uint8_t bytes, uint8_t* data_out);

#define queue_is_data_available(name, bytes) _queue_is_data_available(&QUEUE_##name##.q, bytes)
bool _queue_is_data_available (Queue* name, uint8_t number_of_bytes);

#define queue_has_free_space(name, bytes) _queue_has_free_space(&QUEUE_##name##.q, bytes)
bool _queue_has_free_space (Queue* name, uint8_t number_of_bytes);

#define sem_start_wait_data_available(name,n) _sem_start_wait_data_available(&QUEUE_##name##.q, n)
sem_token_t _sem_start_wait_data_available (Queue* name, uint8_t n);

#define sem_continue_wait_data_available(name, token) _sem_continue_wait_data_available(&QUEUE_##name##.q, token)
bool _sem_continue_wait_data_available (Queue* name , sem_token_t token );

#define sem_stop_wait_data_available(name, token) _sem_stop_wait_data_available(&QUEUE_##name##.q, token)
void _sem_stop_wait_data_available (Queue* name , sem_token_t token );

#define sem_start_wait_free_space(name, n) _sem_start_wait_free_space(&QUEUE_##name##.q, n)
sem_token_t _sem_start_wait_free_space (Queue* name , uint8_t n);

#define sem_continue_wait_free_space(name, token) _sem_continue_wait_free_space(&QUEUE_##name##.q, token)
bool _sem_continue_wait_free_space (Queue* name , sem_token_t token );

#define sem_stop_wait_data_free_space(name, token) _sem_stop_wait_data_free_space(&QUEUE_##name##.q, token)
void _sem_stop_wait_data_free_space (Queue* name , sem_token_t token );


#endif /* QUEUE_H_ */