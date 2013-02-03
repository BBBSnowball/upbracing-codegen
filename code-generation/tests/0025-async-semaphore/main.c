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

	

		led_token1 = sem_start_wait(led);
	 	while(sem_continue_wait(led,led_token1) == FALSE )
	 	{
	 	}
	 	if(sem_continue_wait(led,led_token1) == TRUE)
	 	{
	 		usart_send_str("Update");
	 	}
	 	sem_finish_wait(led,led_token1);
	 	sem_signal(led);


	





	

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

		

		
		/*ASYNCHRONOUS SEMAPHORES*/
		
				 	led_token2 = sem_start_wait(led);
		 	while (sem_continue_wait(led, led_token2) == FALSE)
		 	{
		 	}
		 	if (sem_continue_wait(led,led_token2) == TRUE)
		 	{
		 		usart_send_str("Increment");
		 	}
		 	sem_finish_wait(led,led_token2);
		 	sem_signal(led);
		
		
		
		

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

	sem_token_t led_token3, free_token2,queue_token2;
	
	/*ASYNCHRONOUS SEMAPHORE*/
	
	
	 	led_token3 = sem_start_wait(led);
	 	sem_abort_wait(led, led_token3);
	
	
	#else
	
	
	
	//USARTEnqueue(5,"Shift");
	// Increment shifter variable
	/*shift++;
	if (shift == 8)
		shift = 0;*/
	
	
	
	#endif
	
	// Terminate this task
	TerminateTask();
}

void OS_error(OS_ERROR_CODE error)
{
	//TODO DO NOT use OS function to report the error!
	usart_send_str("error");
}
