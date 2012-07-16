/*
 * queue.h
 *
 * Created: 16-Jul-12 7:19:01 PM
 *  Author: Krishna
 */ 


#ifndef QUEUE_H_
#define QUEUE_H_

#define QUEUE(name, capacity)

#define queue_enqueue (data) _queue_enqueue(data)
void _queue_enqueue(uint8_t data);
#define queue_enqueue(bytes, data) _queue_enqueue(bytes, data)
void _queue_enqueue(uint8_t bytes, const uint8_t* data);

#define queue_dequeue() _queue_dequeue()
uint8_t _queue_dequeue();
#define  queue_dequeue(bytes, data_out) _queue_dequeue(bytes, data_out)
void _queue_dequeue(uint8_t bytes, uint8_t* data_out);

#define queue_is_data_available(bytes) _queue_is_data_available(bytes)
bool _queue_is_data_available (uint8_t number_of_bytes);

#define queue_has_free_space(bytes) _queue_has_free_space(bytes)
bool _queue_has_free_space (uint8_t number_of_bytes);

#define sem_start_wait_data_available(name,n) _sem_start_wait_data_available(name, n)
sem_token_t _sem_start_wait_data_available (name , uint8_t n);

#define sem_continue_wait_data_available(name, token) _sem_continue_wait_data_available(name, token)
bool _sem_continue_wait_data_available (name , sem_token_t token );

#define sem_stop_wait_data_available(name, token) _sem_stop_wait_data_available(name, token)
void _sem_stop_wait_data_available (name , sem_token_t token );

#define sem_start_wait_free_space(name, n) _sem_start_wait_free_space(name, n)
sem_token_t _sem_start_wait_free_space (name , uint8_t n);

#define sem_continue_wait_free_space(name, token) _sem_continue_wait_free_space(name, token)
bool _sem_continue_wait_free_space (name , sem_token_t token );

#define sem_stop_wait_data_free_space(name, token) _sem_stop_wait_data_free_space(name, token)
void _sem_stop_wait_data_free_space (name , sem_token_t token );


#endif /* QUEUE_H_ */