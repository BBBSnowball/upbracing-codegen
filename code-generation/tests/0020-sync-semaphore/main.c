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

SEMAPHORE(our_semaphore, 1, 3);

int main(void)
{	
	// Init GPIO: all leds on
	DDRA  = 0xFF;
	PORTA = 0xFF;

	// init USART (9600 baud, 8N1)
	usart_init();
	
	// make sure the USART works
	// (and PC can check that it has the right test)
	usart_send_str("sync-semaphore test\r\n");

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
#	ifndef DISABLE_SEMAPHORES
	sem_wait(our_semaphore);
#	endif

 	usart_send_str("Up");

	// make sure that another task will interrupt us, if it can
	_delay_ms(10);

 	usart_send_str("date");

#	ifndef DISABLE_SEMAPHORES
	sem_signal(our_semaphore);
#	endif

	// Terminate this task
	TerminateTask();
}

TASK(Increment)
{
#	ifndef DISABLE_SEMAPHORES
	sem_wait(our_semaphore);
#	endif

	usart_send_str("Incre");

	// make sure that another task will interrupt us, if it can
	_delay_ms(10);

	usart_send_str("ment\r\n");

#	ifndef DISABLE_SEMAPHORES
	sem_signal(our_semaphore);
#	endif

	// Terminate this task
	TerminateTask();
}

TASK(Shift)
{
#	ifndef DISABLE_SEMAPHORES
	sem_wait(our_semaphore);
#	endif

	usart_send_str("Shi");

	// make sure that another task will interrupt us, if it can
	_delay_ms(10);

	usart_send_str("ft");

#	ifndef DISABLE_SEMAPHORES
	sem_signal(our_semaphore);
#	endif

	// Terminate this task
	TerminateTask();
}

void OS_error(OS_ERROR_CODE error)
{
	usart_send_str("error");
}
