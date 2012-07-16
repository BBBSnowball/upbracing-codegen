/*
 * queue.c
 *
 * Created: 16-Jul-12 7:20:31 PM
 *  Author: Krishna
 */ 

void _queue_enqueue( uint8_t data )
{
	
}

void _queue_enqueue( uint8_t bytes, const uint8_t* data )
{
	
}

uint8_t _queue_dequeue()
{
	
}

void _queue_dequeue( uint8_t bytes, uint8_t* data_out )
{
	
}

bool _queue_is_data_available( uint8_t number_of_bytes )
{
	
}

bool _queue_has_free_space( uint8_t number_of_bytes )
{
	
}

sem_token_t _sem_start_wait_data_available( name , uint8_t n )
{
	
}

bool _sem_continue_wait_data_available( name , sem_token_t token )
{
	
}

void _sem_stop_wait_data_available( name , sem_token_t token )
{
	
}

sem_token_t _sem_start_wait_free_space( name , uint8_t n )
{
	
}

bool _sem_continue_wait_free_space( name , sem_token_t token )
{
	
}

void _sem_stop_wait_data_free_space( name , sem_token_t token )
{
	
}


