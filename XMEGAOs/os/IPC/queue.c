#include "queue.h"
#include "OSEK.h"
/*
 * queue.c
 *
 * Created: 16-Jul-12 7:20:31 PM
 *  Author: Krishna
 */ 

/*	@brief	Blocks called task until enough space is available

	@param[in] data		Data to be placed on the queue
	
*/
void _queue_enqueue(Queue* sem, uint8_t data )
{
	sem->queue_end = (sem->queue_end==sem->capacity)? 0 : sem->queue_end + 1;
	sem->q_queue[sem->queue_end] = data;
	sem->occupied++;
	
	//activate tasks which are waiting for data, activateConsumer(taskId, 1)
	
}


/*	@brief	Blocks called task until enough space is available, then places data on queue

	@param[in] bytes	Number of bytes of input
	@param[in] *data	Pointer to data, to be placed on the queue
	
*/
void _queue_enqueue(Queue* sem, uint8_t bytes, const uint8_t* data )
{
	uint8_t i;
	for (i=0;i<bytes;i++)
	{
		sem->queue_end = (sem->queue_end==sem->capacity)? 0 : sem->queue_end + 1;
		sem->q_queue[sem->queue_end] = data[i];
	}
	sem->occupied = sem->occupied + bytes;
	
	//activate tasks which are waiting for data, activateConsumer(taskId, bytes)
}

/*	@brief	Returns data from the queue. Blocks till data is available.

	@return				First data in the queue
	
*/
uint8_t _queue_dequeue(Queue* sem)
{
	uint8_t ret;
	ret = sem->q_queue[sem->queue_front];
	sem->queue_front = (sem->queue_front==sem->capacity)? 0 : sem->queue_front +1;
	sem->occupied--;
	
	//activate tasks which are waiting for space, activateProducer(taskId, 1)
	
	return ret;
}

/*	@brief	Returns large data from the queue. Blocks until required amount of data in in the queue.

	@param[in] bytes	Number of bytes of required data
	@param[in] *data_out	Pointer to memory where data is to be stored
	
*/
void _queue_dequeue(Queue* sem, uint8_t bytes, uint8_t* data_out )
{
	uint8_t i;
	for (i=0;i<bytes;i++)
	{
		data_out[i] = sem->q_queue[sem->queue_front];
		sem->queue_front = (sem->queue_front==sem->capacity)? 0 : sem->queue_front +1;
	}
	sem->occupied = sem->occupied - bytes;
	
	//activate tasks which are waiting for space, activateProducer(taskId, bytes)
}

/*	@brief	Checks if there is data available to be read, in the queue

	@param[in] number_of_bytes		the number of bytes which has to be written in the queue
	
	@return		True, if data available. False, if otherwise.
	
*/
bool _queue_is_data_available(Queue* sem, uint8_t number_of_bytes )
{
	if (sem->occupied >= number_of_bytes)
	{
		return TRUE;
	}
	return FALSE;
}

/*	@brief	Checks if there is space available to write into the queue

	@param[in] number_of_bytes		the number of bytes which has to be read from the queue
	
	@return		True, if space available. False, if otherwise.

*/
bool _queue_has_free_space(Queue* sem, uint8_t number_of_bytes )
{
	if ((sem->capacity - sem->occupied) >= number_of_bytes)
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
void _queue_stop_wait_data_available(Semaphore_n* sem , sem_token_t token )
{
	_sem_start_wait_n(sem, token);
	
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
	
	if (ret = TRUE)
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
void _queue_stop_wait_data_free_space(Semaphore_n* sem , sem_token_t token )
{
	_sem_stop_wait_n(sem,token);
}


