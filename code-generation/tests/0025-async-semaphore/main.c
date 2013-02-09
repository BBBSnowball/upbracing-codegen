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
#include <common.h>

SEMAPHORE_N(our_semaphore, 1, 4);

int main(void)
{	
	// Init GPIO: all leds on
	DDRA  = 0xFF;
	PORTA = 0xFF;

	// init USART (9600 baud, 8N1)
	usart_init();
	
	// make sure the USART works
	// (and PC can check that it has the right test)
	usart_send_str("async-semaphore test\r\n");

	// wait for the PC
	while (usart_recv() != 's')
		usart_send('?');

	// enable USART receive interrupts
	UCSRxB |= (1<<RXCIE0);

	// Init Os
	StartOS();

	//NOTE: Since OS is used, program will never get here!
    while(1);
}

// USART receive interrupt
#if (USE_USART_NUMBER == 0)
ISR(USART0_RX_vect) {
#elif (USE_USART_NUMBER == 1)
ISR(USART1_RX_vect) {
#endif
	if (UDRx == 'e') {
		usart_send_str("stopped");

		// don't send any further data
		// We simply make sure that the program
		// cannot continue.
		// (interrupts are disabled in an interrupt handler)
		while (1);
	}
}

TASK(Update)
{
	sem_token_t led_token1;
	
	led_token1 = sem_start_wait(our_semaphore);
#	ifndef DISABLE_SEMAPHORES
	while( ! sem_continue_wait(our_semaphore, led_token1) )
	{
	}
	if( !sem_continue_wait(our_semaphore, led_token1) )
		usart_send_str("ERR(U): sem_continue_wait returned true, then false\r\n");
#	endif


	usart_send_str("Up");

	// make sure that another task will interrupt us, if it can
	_delay_ms(10);

	usart_send_str("date");


	sem_finish_wait(our_semaphore,led_token1);
	sem_signal(our_semaphore);

	// Terminate this task
	TerminateTask();
}

TASK(Increment)
{
	sem_token_t led_token1;
	
	led_token1 = sem_start_wait(our_semaphore);
#	ifndef DISABLE_SEMAPHORES
	while( ! sem_continue_wait(our_semaphore, led_token1) )
	{
	}
	if( ! sem_continue_wait(our_semaphore, led_token1) )
		usart_send_str("ERR(I): sem_continue_wait returned true, then false\r\n");
#	endif


	usart_send_str("Incre");

	// make sure that another task will interrupt us, if it can
	_delay_ms(10);

	usart_send_str("ment");


	sem_finish_wait(our_semaphore,led_token1);
	sem_signal(our_semaphore);

	// Terminate this task
	TerminateTask();
}

TASK(Shift)
{
	sem_token_t led_token3, led_token4;

	// abort a wait
	led_token3 = sem_start_wait(our_semaphore);
	_delay_ms(10);
	sem_abort_wait(our_semaphore, led_token3);

	// abort waits in a funny order
	led_token3 = sem_start_wait(our_semaphore);
	_delay_ms(10);
	led_token4 = sem_start_wait(our_semaphore);
	_delay_ms(10);
	sem_abort_wait(our_semaphore, led_token3);
	_delay_ms(10);
	sem_abort_wait(our_semaphore, led_token4);

	// abort a wait that is not ready
	led_token3 = sem_start_wait(our_semaphore);
	sem_abort_wait(our_semaphore, led_token3);

	// same thing for two waits
	led_token3 = sem_start_wait(our_semaphore);
	led_token4 = sem_start_wait(our_semaphore);
	sem_abort_wait(our_semaphore, led_token3);
	sem_abort_wait(our_semaphore, led_token4);

	// abort a ready wait
	led_token3 = sem_start_wait(our_semaphore);
	while ( ! sem_continue_wait(our_semaphore, led_token3) )
		;
	sem_abort_wait(our_semaphore, led_token3);

	// mix it with asynchronous access
#	ifndef DISABLE_SEMAPHORES
	sem_wait(our_semaphore);
#	endif DISABLE_SEMAPHORES
	
	// report that we finished our test cases
	// (we're still within a critical section, so
	//  it won't be interrupted)
	usart_send_str("Shift\r\n");

	// leave critical section
#	ifndef DISABLE_SEMAPHORES
	sem_signal(our_semaphore);
#	endif DISABLE_SEMAPHORES

	// Terminate this task
	TerminateTask();
}

void OS_error(OS_ERROR_CODE error)
{
	usart_send_str("error");
}
