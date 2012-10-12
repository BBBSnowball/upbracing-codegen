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

/*	@brief	Writes a byte of data into the queue passed as the parameter

	@param[in] data		Data to be placed on the queue
	
*/
void _queue_enqueue(Queue* q, uint8_t data )
{
	q->q_queue[q->queue_end] = data;
	q->queue_end++;
	if (q->queue_end == q->capacity)
		q->queue_end = 0;
	q->occupied++;
	
}


/*	@brief	Writes multiple bytes into the queue passed as parameter

	@param[in] bytes	Number of bytes of input
	@param[in] *data	Pointer to data, to be placed on the queue
	
*/
void _queue_enqueue2(Queue* q, uint8_t bytes, const uint8_t* data )
{
	uint8_t i;
	
	
	for (i=0;i<bytes;i++)
	{
		q->q_queue[q->queue_end] = data[i];
		q->queue_end++;
		if (q->queue_end == q->capacity)
			q->queue_end = 0;
	}
	q->occupied = q->occupied + bytes;
}



/*	@brief	Returns data from the queue.

	@return				First data in the queue
	
*/
void _queue_dequeue(Queue* q, uint8_t* output)
{
	uint8_t ret;
	
	
	ret = q->q_queue[q->queue_front];
	q->queue_front = (q->queue_front==(q->capacity-1))? 0 : q->queue_front +1;
	q->occupied--;
	
	*output = ret;
}

/*	@brief	Returns large data from the queue. 

	@param[in] bytes	Number of bytes of required data
	@param[in] *data_out	Pointer to memory where data is to be stored
	
*/
void _queue_dequeue2(Queue* q, uint8_t bytes, uint8_t* data_out )
{
	uint8_t i;
	
	for (i=0;i<bytes;i++)
	{
		data_out[i] = q->q_queue[q->queue_front];
		q->queue_front = (q->queue_front==q->capacity-1)? 0 : q->queue_front +1;
	}
	q->occupied = q->occupied - bytes;
}

/*	@brief	Checks if there is data available to be read, in the queue

	@param[in] number_of_bytes		the number of bytes which has to be written in the queue
	
	@return		True, if data available. False, if otherwise.
	
*/
bool _queue_is_data_available(Queue* q, uint8_t number_of_bytes )
{
	if (q->occupied >= number_of_bytes)
	{
		return TRUE;
	}
	return FALSE;
}

/*	@brief	Checks if there is space available to write into the queue

	@param[in] number_of_bytes		the number of bytes which has to be read from the queue
	
	@return		True, if space available. False, if otherwise.

*/
bool _queue_has_free_space(Queue* q, uint8_t number_of_bytes )
{
	if ((q->capacity - q->occupied) >= number_of_bytes)
	{
		return TRUE;
	}
	return FALSE;
}

/*	@brief	Starts asynchronous waiting for data to be available in the queue

	@param[in] name		Name of the queue to be waited on
	@param[in] n		Number of requests
	
	@return		Token to be used for continue wait operation
*/
sem_token_t _queue_start_wait_data_available(Semaphore_n* sem , uint8_t n )
{
	return _sem_start_wait_n(sem, n);
}

/*	@brief	Checks if the data is available to be read

	@param[in] name		Name of the queue being waited on
	@param[in] token	Token for the queue
	
	@return		True, if data is available. False, if data is not available.
				If returned true, the token must not be used further.

*/
bool _queue_continue_wait_data_available(Semaphore_n* sem ,Queue* que, sem_token_t token )
{
	uint8_t n;
	bool ret = _sem_continue_wait_n(sem,token);
	if (ret == TRUE)
	{
		n = sem->queue[sem->queue_front].n;
		if (n > que->occupied)
		{
			ret = FALSE;
		}
	}
	
	return ret;
	
}

/*	@brief	Stops waiting for data to be available

	@param[in] name		Name of the queue being waited on
	@param[in] token	Token for the queue
	
	The token must not be used after this.
*/
void _queue_stop_wait_data_available(Semaphore_n* sem , uint8_t n, sem_token_t token )
{
	_sem_stop_wait_n(sem, n, token);
	
}

/*	@brief	Starts asynchronous waiting for free space to be available in the queue

	@param[in] name		Name of the queue to be waited on
	@param[in] n		Number of requests
	
	@return		Token to be used for continue wait operation
*/
sem_token_t _queue_start_wait_free_space(Semaphore_n* sem , uint8_t n )
{
	return _sem_start_wait_n(sem, n);
}

/*	@brief	Checks if the space is available to write

	@param[in] name		Name of the queue being waited on
	@param[in] token	Token for the queue
	
	@return		True, if space is available. False, if space is not available.
				If returned true, the token must not be used further.

*/
bool _queue_continue_wait_free_space(Semaphore_n* sem ,Queue* que, sem_token_t token )
{
	uint8_t n;
	bool ret = _sem_continue_wait_n(sem, token);
	
	if (ret == TRUE)
	{
		n = sem->queue[sem->queue_front].n;
		if (n > (que->capacity - que->occupied))
		{
			ret = FALSE;
		}
	}
	return ret;
}

/*	@brief	Stops waiting for space to be available

	@param[in] name		Name of the queue being waited on
	@param[in] token	Token for the queue
	
	The token must not be used after this.
*/
void _queue_stop_wait_data_free_space(Semaphore_n* sem , uint8_t n, sem_token_t token )
{
	_sem_stop_wait_n(sem, n, token);
}

sem_token_t _queue_start_wait( Semaphore* sem )
{
	return _sem_start_wait(sem);
}

bool _queue_continue_wait( Semaphore* sem, sem_token_t token )
{
	return _sem_continue_wait(sem,token);
}

 _queue_stop_wait( Semaphore* sem, sem_token_t token )
{
	_sem_stop_wait(sem,token);
}


