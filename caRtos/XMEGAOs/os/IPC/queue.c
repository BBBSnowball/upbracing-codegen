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
void _queue_enqueue(Queue* name, uint8_t data )
{
	//queue name ?
	//should i assume critical section wrap or implement it here ?
	
	
	
}


/*	@brief	Blocks called task until enough space is available, then places data on queue

	@param[in] bytes	Number of bytes of input
	@param[in] *data	Pointer to data, to be placed on the queue
	
*/
void _queue_enqueue(Queue* name, uint8_t bytes, const uint8_t* data )
{
	
}

/*	@brief	Returns data from the queue. Blocks till data is available.

	@return				First data in the queue
	
*/
uint8_t _queue_dequeue(Queue* name)
{
	
}

/*	@brief	Returns large data from the queue. Blocks until required amount of data in in the queue.

	@param[in] bytes	Number of bytes of required data
	@param[in] *data_out	Pointer to memory where data is to be stored
	
*/
void _queue_dequeue(Queue* name, uint8_t bytes, uint8_t* data_out )
{
	
}

/*	@brief	Checks if there is data available to be read, in the queue

	@param[in] number_of_bytes		the number of bytes which has to be written in the queue
	
	@return		True, if data available. False, if otherwise.
	
*/
bool _queue_is_data_available(Queue* name, uint8_t number_of_bytes )
{
	
}

/*	@brief	Checks if there is space available to write into the queue

	@param[in] number_of_bytes		the number of bytes which has to be read from the queue
	
	@return		True, if space available. False, if otherwise.

*/
bool _queue_has_free_space(Queue* name, uint8_t number_of_bytes )
{
	
}

/*	@brief	Starts asynchronous waiting for data to be available in the queue

	@param[in] name		Name of the queue to be waited on
	@param[in] n		Number of requests
	
	@return		Token to be used for continue wait operation
*/
sem_token_t _sem_start_wait_data_available(Queue* name , uint8_t n )
{
	
}

/*	@brief	Checks if the data is available to be read

	@param[in] name		Name of the queue being waited on
	@param[in] token	Token for the queue
	
	@return		True, if data is available. False, if data is not available.
				If returned true, the token must not be used further.

*/
bool _sem_continue_wait_data_available(Queue* name , sem_token_t token )
{
	
}

/*	@brief	Stops waiting for data to be available

	@param[in] name		Name of the queue being waited on
	@param[in] token	Token for the queue
	
	The token must not be used after this.
*/
void _sem_stop_wait_data_available(Queue* name , sem_token_t token )
{
	
}

/*	@brief	Starts asynchronous waiting for free space to be available in the queue

	@param[in] name		Name of the queue to be waited on
	@param[in] n		Number of requests
	
	@return		Token to be used for continue wait operation
*/
sem_token_t _sem_start_wait_free_space(Queue* name , uint8_t n )
{
	
}

/*	@brief	Checks if the space is available to write

	@param[in] name		Name of the queue being waited on
	@param[in] token	Token for the queue
	
	@return		True, if space is available. False, if space is not available.
				If returned true, the token must not be used further.

*/
bool _sem_continue_wait_free_space(Queue* name , sem_token_t token )
{
	
}

/*	@brief	Stops waiting for space to be available

	@param[in] name		Name of the queue being waited on
	@param[in] token	Token for the queue
	
	The token must not be used after this.
*/
void _sem_stop_wait_data_free_space(Queue* name , sem_token_t token )
{
	
}


