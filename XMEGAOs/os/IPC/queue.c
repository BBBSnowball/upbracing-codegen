/*
 * queue.c
 *
 * Created: 16-Jul-12 7:20:31 PM
 *  Author: Krishna (s.krishna1989@gmail.com)
 */ 
#include "queue.h"
#include "semaphore.h"
#include "Os.h"
#include <avr/interrupt.h>

void queue_enqueue_internal(uint8_t count, const uint8_t* data) {
	//TODO This is VERY inefficient!
	for (i=0;i<bytes;i++)
	{
		q->q_queue[q->queue_end] = data[i];
		q->queue_end++;
		if (q->queue_end == q->capacity)
			q->queue_end = 0;
	}
	q->occupied = q->occupied + count;
}

void queue_dequeue_internal(uint8_t count, uint8_t* data) {
	//TODO This is VERY inefficient!
	for (i=0;i<bytes;i++)
	{
		data_out[i] = q->q_queue[q->queue_front];
		q->queue_front = (q->queue_front==q->capacity-1)? 0 : q->queue_front +1;
	}
	q->occupied = q->occupied - bytes;
}



/*	@brief	Writes a byte of data into the queue passed as the parameter

	@param[in] data		Data to be placed on the queue
	
*/
void _queue_enqueue(Queue* sem, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q, uint8_t data)
{
	_sem_wait_n(sem_prod, 1);
	_sem_wait(sem_q);

	q->q_queue[q->queue_end] = data;
	q->queue_end++;
	if (q->queue_end == q->capacity)
		q->queue_end = 0;
	q->occupied++;

	_sem_signal(sem_q);
	_sem_signal_n(sem_cons, 1);
	
}


/*	@brief	Writes multiple bytes into the queue passed as parameter

	@param[in] bytes	Number of bytes of input
	@param[in] *data	Pointer to data, to be placed on the queue
	
*/
void _queue_enqueue_many(Queue* q, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q,
		uint8_t count, const uint8_t* data)
{
	uint8_t i;

	_sem_wait_n(sem_prod, count);
	_sem_wait(sem_q);
	
	queue_enqueue_internal(count, data);

	_sem_signal(sem_q);
	_sem_signal_n(sem_cons, count);
}



/*	@brief	Returns data from the queue.

	@return				First data in the queue
	
*/
uint8_t _queue_dequeue(Queue* sem, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q)
{
	uint8_t ret;

	_sem_wait_n(sem_cons, 1);
	_sem_wait(sem_q);
	
	ret = q->q_queue[q->queue_front];
	q->queue_front = (q->queue_front==(q->capacity-1))? 0 : q->queue_front +1;
	q->occupied--;

	_sem_wait(sem_q);
	_sem_wait_n(sem_prod, 1);
	
	return ret;
}

/*	@brief	Returns large data from the queue. 

	@param[in] bytes	Number of bytes of required data
	@param[in] *data_out	Pointer to memory where data is to be stored
	
*/
void _queue_dequeue_many(Queue* q, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q,
		uint8_t count, uint8_t* data)
{
	uint8_t i;

	_sem_wait_n(sem_cons, count);
	_sem_wait(sem_q);
	
	queue_dequeue_internal(count, data);

	_sem_wait(sem_q);
	_sem_wait_n(sem_prod, count);
}


void _queue_finish_enqueue(Queue* sem, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q, sem_token_t token,
		uint8_t no_of_bytes, const uint8_t* data)
{
	// the token must be ready
	if (!_sem_continue_wait_n(sem_prod, token)) {
		OS_report_error(OS_ERROR_NOT_READY);
		_sem_abort_wait_n(sem_prod, token);
		return;
	}

	// remove token from waiting queue
	uint8_t reserved = _sem_finish_wait_n(sem_prod, token);
	if (reserved < no_of_bytes) {
		// we don't have enough space -> error
		OS_report_error(OS_ERROR_SMALL_RESERVATION);
		// we can only insert part of the data
		no_of_bytes = reserved;
	} else if (reserved > no_of_bytes) {
		// we have reserved to much space -> free superfluous space
		_sem_signal_n(sem_prod, reserved - no_of_bytes);
	}

	// enqueue the data
	_sem_wait(sem_q);
	queue_enqueue_internal(no_of_bytes, data);
	_sem_signal(sem_q);

	// notify consumers
	_sem_signal_n(sem_cons, no_of_bytes);
}

void _queue_finish_dequeue(Queue* sem, Semaphore_n* sem_prod,
		Semaphore_n* sem_cons, Semaphore* sem_q, sem_token_t token,
		uint8_t no_of_bytes, uint8_t* data)
{
	// the token must be ready
	if (!_sem_continue_wait_n(sem_cons, token)) {
		OS_report_error(OS_ERROR_NOT_READY);
		_sem_abort_wait_n(sem_cons, token);
		return;
	}

	// remove token from waiting queue
	uint8_t reserved = _sem_finish_wait_n(sem_cons, token);
	if (reserved < no_of_bytes) {
		// we don't have enough data -> error
		OS_report_error(OS_ERROR_SMALL_RESERVATION);
		// we can only insert part of the data
		no_of_bytes = reserved;
	} else if (reserved > no_of_bytes) {
		// we have reserved to much space -> free superfluous space
		_sem_signal_n(sem_cons, reserved - no_of_bytes);
	}

	// enqueue the data
	_sem_wait(sem_q);
	queue_dequeue_internal(no_of_bytes, data);
	_sem_signal(sem_q);

	// notify producers
	_sem_signal_n(sem_prod, no_of_bytes);
}
