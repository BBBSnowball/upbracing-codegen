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
	sem_token_t queue_token2;
	
	char data[5];
	
	/*ASYNCHRONOUS QUEUES*/	
	queue_token2 = queue_start_dequeue(our_queue,2);
	while (queue_continue_dequeue(our_queue,queue_token2) == FALSE)
	{
	}
	if (queue_continue_dequeue(our_queue,queue_token2) == TRUE)
	{
		queue_finish_dequeue(our_queue,queue_token2,2,&data);
		usart_send_str(&data);
		
	}

	// Terminate this task
	TerminateTask();
}

TASK(Increment)
{
	sem_token_t queue_token1;
	
 	queue_token1 = queue_start_enqueue(our_queue,2);
	while (queue_continue_enqueue(our_queue,queue_token1) == FALSE)
 	{
 	}
 	if (queue_continue_enqueue(our_queue,queue_token1) == TRUE)
 	{
 		queue_finish_enqueue(our_queue,queue_token1,2,"OK");
 	}

	// Terminate this task
	TerminateTask();
}

TASK(Shift)
{
	sem_token_t queue_token3;
	
	queue_token3 = queue_start_enqueue(our_queue,5);
	queue_abort_enqueue(our_queue, queue_token3);
	
	// Terminate this task
	TerminateTask();
}

void OS_error(OS_ERROR_CODE error)
{
	//TODO DO NOT use OS function to report the error!
	usart_send_str("error");
}
