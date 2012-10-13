/*
 * main.c
 *
 * Created: 20.12.2011 21:44:56
 *  Author: peer
 */ 

#include <avr/io.h>
#include <avr/interrupt.h>
#include "Os.h"
#include "USART.h"
#include "Gpio.h"
#include "semaphore.h"
#include "Os_error.h"

volatile uint8_t j = 1;
volatile uint8_t shift = 0;

//SEMAPHORE(led,1,5);
SEMAPHORE_N(led,5,1);

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

TASK(Task_Update)
{
	sem_token_t led_token1, read_token, queue_token2 ;
	bool see, look;
	
	
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
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Increment)
{
	sem_token_t led_token2, free_token1, queue_token1;
	bool check;
	
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
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Shift)
{
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
	
	// Terminate this task
	TerminateTask();
}

extern void OS_ERROR( ERROR_CODES error )
{
	USARTEnqueue(5,"error");
}
