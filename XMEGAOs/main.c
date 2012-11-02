/*
 * main.c
 *
 * Created: 20.12.2011 21:44:56
 *  Author: peer
 */ 

#define PROGRAM_MODE TEST_SYNC_QUEUE
//#define PROGRAM_MODE KRISHNA_204b6f

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
//SEMAPHORE(led,1,5);
SEMAPHORE_N(led,5,1);
#elif PROGRAM_MODE == KRISHNA_204b6f
SEMAPHORE(led,1,5);
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
	#if PROGRAM_MODE == TEST_SYNC_QUEUE

	USARTEnqueue(6, "Update");

	#elif PROGRAM_MODE == KRISHNA_204b6f

	/*SYNCHRONOUS SEMAPHORE*/
	
	/*
	// 	sem_wait(led);
	// 	PORTA = j;
	// 	sem_signal(led);
	*/
	
	/*ASYNCHRONOUS SEMAPHORE*/
	
	/*
	// 	led_token1 = sem_start_wait(led);
	// 	while(sem_continue_wait(led,led_token1) == FALSE )
	// 	{
	// 	}
	// 	if(sem_continue_wait(led,led_token1) == TRUE)
	// 	{
	// 		PORTA = j;
	// 	}
	// 	sem_finish_wait(led,led_token1);
	// 	sem_signal(led);
	*/

		/* ASYNCHRONOUS N SEMAPHORES*/
	/*
	// 	led_token1 = sem_start_wait_n(led,1);
	// 	while (sem_continue_wait_n(led,led_token1) == FALSE)
	// 	{
	// 	}
	// 	if (sem_continue_wait_n(led,led_token1) == TRUE)
	// 	{
	// 		PORTA = j;
	// 	}
	// 	sem_finish_wait_n(led,led_token1);
	// 	sem_signal_n(led,1);
	*/

	/*SYNCHRONOUS QUEUE*/
	//USARTEnqueue(6, "Update");
	
	/*ASYNCHRONOUS QUEUES*/	
	queue_token2 = queue_start_dequeue(ipc,3);
	while (queue_continue_dequeue(ipc,queue_token2) == FALSE)
	{
	}
	if (queue_continue_dequeue(ipc,queue_token2) == TRUE)
	{
		queue_finish_dequeue(ipc,queue_token2,3,&data);
		USARTEnqueue(3,&data);
	}

	sem_token_t led_token1, read_token, queue_token2 ;
	BOOL see, look;
	char data[3];

	#else
	
	sem_token_t led_token1, read_token, queue_token2 ;
	BOOL see, look;
	
	char data[2];
	// Enqueue something for USART
	// -> demonstration of Queues and Semaphores
	//USARTEnqueue(6, "Update");
	
	read_token = queue_start_wait_data_available(ipc,2);
	
	if (queue_continue_wait_data_available(ipc,read_token) == TRUE)
	{
		queue_token2 = queue_start_wait(ipc);
		
		if (queue_continue_wait(ipc,queue_token2) == TRUE)
		{
			queue_dequeue_async(ipc,2, (uint8_t *) &data);
			USARTEnqueue(2,&data);
		}
		queue_stop_wait(ipc,queue_token2);
	}
	
	queue_stop_wait_data_available(ipc,2,read_token);
	
	
	
	
	//led_token1 = sem_start_wait(led);
	//while(sem_continue_wait(led,led_token1) == FALSE )
	//{
	//}
	//if(sem_continue_wait(led,led_token1) == TRUE)
	//{
		//PORTA = j;
	//}
	//sem_stop_wait(led,led_token1);
	
	led_token1 = sem_start_wait_n(led,1);
	while (sem_continue_wait_n(led,led_token1) == FALSE)
	{
	}
	if (sem_continue_wait_n(led,led_token1) == TRUE)
	{
		PORTA = j;
	}
	sem_stop_wait_n(led,1,led_token1);
	
	#endif
	
	// Terminate this task
	TerminateTask();
}

TASK(Increment)
{
	#if PROGRAM_MODE == TEST_SYNC_QUEUE

	USARTEnqueue(10, "Increment\n");

	#elif PROGRAM_MODE == KRISHNA_204b6f

		sem_token_t led_token2, free_token1, queue_token1;
		BOOL check;

		/*SYNCHRONOUS SEMAPHORES*/
		/*
		// 	sem_wait(led);
		// 	j++;
		// 	sem_signal(led);
		*/
		
		/*ASYNCHRONOUS SEMAPHORES*/
		
		/*
		// 	led_token2 = sem_start_wait(led);
		// 	while (sem_continue_wait(led, led_token2) == FALSE)
		// 	{
		// 	}
		// 	if (sem_continue_wait(led,led_token2) == TRUE)
		// 	{
		// 		j++;
		// 	}
		// 	sem_finish_wait(led,led_token2);
		// 	sem_signal(led);
		*/
		
		/*ASYNCHRONOUS N SEMAPHORE*/
		/*
		// 	led_token2 = sem_start_wait_n(led,1);
		// 	while (sem_continue_wait_n(led,led_token2) == FALSE)
		// 	{
		// 	}
		// 	if (sem_continue_wait_n(led,led_token2) == TRUE)
		// 	{
		// 		j++;
		// 	}
		// 	sem_finish_wait_n(led,led_token2);
		// 	sem_signal_n(led,1);
		*/
		/*SYNCHRONOUS QUEUE*/
		//USARTEnqueue(10, "Increment\n");
		
		/*ASYNCHRONOUS QUEUES*/
		queue_token1 = queue_start_enqueue(ipc,3);
		while (queue_continue_enqueue(ipc,queue_token1) == FALSE)
		{
		}
		if (queue_continue_enqueue(ipc,queue_token1) == TRUE)
		{
			queue_finish_enqueue(ipc,queue_token1,3,"One");
		}

	#else
	
	sem_token_t led_token2, free_token1, queue_token1;
	BOOL check;
	
	// Increment global counter for leds
	
	
	// Enqueue something for USART
	// -> demonstration of Queues and Semaphores
	//USARTEnqueue(9, "Increment");
	
	free_token1 = queue_start_wait_free_space(ipc,1);
	
	if (queue_continue_wait_free_space(ipc,free_token1) == TRUE)
	{
		queue_token1 = queue_start_wait(ipc);
		
		if (queue_continue_wait(ipc,queue_token1) == TRUE)
		{
			queue_enqueue_async(ipc,1,"A");
		}
		queue_stop_wait(ipc,queue_token1);
	}
	
	queue_stop_wait_data_free_space(ipc,1,queue_token1);
	
	//led_token2 = sem_start_wait(led);
	//while (sem_continue_wait(led, led_token2) == FALSE)
	//{
	//}
	//if (sem_continue_wait(led,led_token2) == TRUE)
	//{
		//j++;
	//}
	//sem_stop_wait(led,led_token2);
	
	led_token2 = sem_start_wait_n(led,1);
	while (sem_continue_wait_n(led,led_token2) == FALSE)
	{
	}
	if (sem_continue_wait_n(led,led_token2) == TRUE)
	{
		j++;
	}
	sem_stop_wait_n(led,1,led_token2);
	
	//USARTEnqueue(5, "First");
	
	#endif
	
	// Terminate this task
	TerminateTask();
}

TASK(Shift)
{
	
	#if PROGRAM_MODE == TEST_SYNC_QUEUE

	USARTEnqueue(5,"Shift");

	#elif PROGRAM_MODE == KRISHNA_204b6f

	sem_token_t led_token3, free_token2,queue_token2;
	
	/*ASYNCHRONOUS SEMAPHORE*/
	
	/*
	// 	led_token3 = sem_start_wait(led);
	// 	sem_abort_wait(led, led_token3);
	*/
		/*ASYNCHRONOUS N SEMAPHORE*/
	/*
	// 	led_token3 = sem_start_wait_n(led,1);
	// 	sem_abort_wait_n(led,led_token3);
	*/
	
	/*SYNCHRONOUS QUEUES*/
	//USARTEnqueue(5,"Shift");
	
	/*ASYNCHRONOUS QUEUES*/
	
	// Increment shifter variable
	shift++;
	if (shift == 8)
	shift = 0;

	#else
	
	sem_token_t free_token2,queue_token2;
	
	//USARTEnqueue(5,"Shift");
	// Increment shifter variable
	shift++;
	if (shift == 8)
		shift = 0;
	
	free_token2 = queue_start_wait_free_space(ipc,1);
	
	if (queue_continue_wait_free_space(ipc,free_token2) == TRUE)
	{
		queue_token2 = queue_start_wait(ipc);
		
		if (queue_continue_wait(ipc,queue_token2) == TRUE)
		{
			queue_enqueue_async(ipc,1,"B");
		}
		queue_stop_wait(ipc,queue_token2);
	}
	
	queue_stop_wait_data_free_space(ipc,1,queue_token2);
	
	#endif
	
	// Terminate this task
	TerminateTask();
}

void OS_error(OS_ERROR_CODE error)
{
	//TODO DO NOT use OS function to report the error!
	USARTEnqueue(5,"error");
}
