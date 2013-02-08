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
#include <IPC/queue.h>
#include <internal/Os_Error.h>

#include <rs232.h>


QUEUE(our_queue,10,1,2);

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
	sem_token_t led_token1, read_token, queue_token2, led_tokens[10] ;
	
	queue_enqueue_many(our_queue, 2, "OK");
		
	// Terminate this task
	TerminateTask();
}

TASK(Increment)
{
	
	char data[5];
	
	queue_dequeue_many(our_queue, 2, &data);
	usart_send_str(&data);
	
	// Terminate this task
	TerminateTask();
}

TASK(Shift)
{
	
	// Terminate this task
	TerminateTask();
}

void OS_error(OS_ERROR_CODE error)
{
	//TODO DO NOT use OS function to report the error!
	usart_send_str("error");
}
