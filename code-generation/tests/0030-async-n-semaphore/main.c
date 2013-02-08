/*
 * main.c
 *
 * Created: 20.12.2011 21:44:56
 *  Author: peer
 */ 

// You can use this #define to disable the semaphores. The test
// should fail, if you do so!
//#define DISABLE_SEMAPHORES

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

#include "Os.h"
#include <semaphores/semaphore.h>
#include <internal/Os_Error.h>

#include <rs232.h>

SEMAPHORE_N(our_semaphore, 4, 1);

int main(void)
{	
	// Init GPIO: all leds on
	DDRA  = 0xFF;
	PORTA = 0xFF;

	// init USART (9600 baud, 8N1)
	usart_init();
	
	// Init Os
	StartOS();

	//NOTE: Since OS is used, program will never get here!
    while(1);
}

TASK(Update)
{
	sem_token_t led_token1;
	
#	ifndef DISABLE_SEMAPHORES
		led_token1 = sem_start_wait_n(our_semaphore, 1);
	 	while(sem_continue_wait_n(our_semaphore,led_token1) == FALSE )
	 	{
	 	}
	 	if(sem_continue_wait_n(our_semaphore,led_token1) == TRUE)
	 	{
#	endif
		
	 		usart_send_str("Up");

			// make sure that another task will interrupt us, if it can
			_delay_ms(10);

			usart_send_str("date");
	 	
#	ifndef DISABLE_SEMAPHORES
		}
	 	sem_finish_wait_n(our_semaphore,led_token1);
	 	sem_signal_n(our_semaphore, 1);
#	endif

	// Terminate this task
	TerminateTask();
}

TASK(Increment)
{
	sem_token_t led_token2;
	
#	ifndef DISABLE_SEMAPHORES
		led_token2 = sem_start_wait_n(our_semaphore, 1);
	 	while(sem_continue_wait_n(our_semaphore,led_token2) == FALSE )
	 	{
	 	}
	 	if(sem_continue_wait_n(our_semaphore,led_token2) == TRUE)
	 	{
#	endif
		
	 		usart_send_str("Incre");

			// make sure that another task will interrupt us, if it can
			_delay_ms(10);

			usart_send_str("ment\r\n");
	 	
#	ifndef DISABLE_SEMAPHORES
		}
	 	sem_finish_wait_n(our_semaphore,led_token2);
	 	sem_signal_n(our_semaphore, 1);
#	endif
	
	// Terminate this task
	TerminateTask();
}

TASK(Shift)
{
	sem_token_t led_token3;
	
#	ifndef DISABLE_SEMAPHORES
		led_token3 = sem_start_wait_n(our_semaphore, 1);
		_delay_ms(10);
	 	sem_abort_wait_n(our_semaphore, led_token3);
#	endif
	
	// Terminate this task
	TerminateTask();
}

void OS_error(OS_ERROR_CODE error)
{
	//TODO DO NOT use OS function to report the error!
	usart_send_str("Error");
}
