/*
 * main.c
 *
 * Created: 20.12.2011 21:44:56
 *  Author: peer
 */ 

//#define PROGRAM_MODE TEST_SYNC_QUEUE
#define PROGRAM_MODE KRISHNA_204b6f

#include <avr/io.h>
#include <avr/interrupt.h>
#include "Os.h"
#include "USART.h"
#include "Gpio.h"
#include "semaphore.h"
#include "Os_error.h"

volatile uint8_t j = 1;
volatile uint8_t shift = 0;

#if PROGRAM_MODE == TEST_SYNC_QUEUE
//SEMAPHORE(led,1,4);
SEMAPHORE_N(led,5,1);
#elif PROGRAM_MODE == KRISHNA_204b6f
SEMAPHORE(led,1,4);
//SEMAPHORE_N(led,5,1);
#endif

QUEUE(ipc,10,1,2);

int main(void)
{	
	// Init GPIO: (demo: DDRA = 0xFF)
	GpioInit();
	PORTA = 0xFF;
	
	// Init the USART (57600 8N1)
	USARTInit(8);
	
	// NOTE(Peer):
	// DO NOT enable Interrupts here
	// StartOs will call Os_StartFirstTask.
	// Os_StartFirstTask will enable interrupts when system is ready.
	// If we enable interrupts now, the first timer tick
	// most likely comes too early if the timer freq is high enough.
	//// Globally enable interrupts
	//sei();
	
	// Init Os
	StartOS();

	//NOTE: Since OS is used, program will never get here!
    while(1);
}

TASK(Update)
{
	sem_token_t led_token1, read_token, queue_token2, led_tokens[10] ;
	BOOL see, look;
	char data[3];
	#if PROGRAM_MODE == TEST_SYNC_QUEUE

	USARTEnqueue(6, "Update");

	#elif PROGRAM_MODE == KRISHNA_204b6f
	
	/*ASYNCHRONOUS QUEUES*/	
	queue_token2 = queue_start_dequeue(ipc,2);
	while (queue_continue_dequeue(ipc,queue_token2) == FALSE)
	{
	}
	if (queue_continue_dequeue(ipc,queue_token2) == TRUE)
	{
		queue_finish_dequeue(ipc,queue_token2,2,&data);
		usart_send_str(&data);
		//USARTEnqueue(3,&data);
	}

	#else
	#endif
	
	// Terminate this task
	TerminateTask();
}

TASK(Increment)
{
	sem_token_t led_token2, free_token1, queue_token1;
	BOOL check;
	#if PROGRAM_MODE == TEST_SYNC_QUEUE

	USARTEnqueue(10, "Increment\n");
	
	#elif PROGRAM_MODE == KRISHNA_204b6f

	/*ASYNCHRONOUS QUEUES*/

 	queue_token1 = queue_start_enqueue(ipc,2);
	while (queue_continue_enqueue(ipc,queue_token1) == FALSE)
 	{
 	}
 	if (queue_continue_enqueue(ipc,queue_token1) == TRUE)
 	{
 		queue_finish_enqueue(ipc,queue_token1,2,"OK");
 	}


	#else
	#endif
	
	// Terminate this task
	TerminateTask();
}

TASK(Shift)
{
	
	#if PROGRAM_MODE == TEST_SYNC_QUEUE

	USARTEnqueue(5,"Shift");

	#elif PROGRAM_MODE == KRISHNA_204b6f
	#else
	#endif
	
	// Terminate this task
	TerminateTask();
}

void OS_error(OS_ERROR_CODE error)
{
	//TODO DO NOT use OS function to report the error!
	usart_send_str("error");
}
