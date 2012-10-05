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

volatile uint8_t j = 10;
volatile uint8_t shift = 0;

//SEMAPHORE(led,1,5);
SEMAPHORE_N(led,5,1);

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
	sem_token_t led_token1;
	bool see;
	
	// Enqueue something for USART
	// -> demonstration of Queues and Semaphores
	USARTEnqueue(6, "ABCDEF");
	
	
	led_token1 = sem_start_wait_n(led,1);
	
	while(sem_continue_wait_n(led,led_token1) == FALSE )
	{
	}
	//sem_stop_wait(led,led_token1);
	PORTA = j;
	
	
	sem_stop_wait_n(led,1,led_token1);
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Increment)
{
	sem_token_t led_token2;
	bool check;
	// Increment global counter for leds
	
	
	// Enqueue something for USART
	// -> demonstration of Queues and Semaphores
	USARTEnqueue(6, "mNoPqR");
	
	led_token2 = sem_start_wait_n(led,1);
	
	
	while (sem_continue_wait_n(led, led_token2) == FALSE)
	{
	}
	j++;
	
	
	sem_stop_wait_n(led,1,led_token2);
	
	//USARTEnqueue(5, "First");
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Shift)
{
	// Increment shifter variable
	shift++;
	if (shift == 8)
		shift = 0;
	
	USARTEnqueue(6,"ghijkl");
	// Terminate this task
	TerminateTask();
}