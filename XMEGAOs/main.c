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

volatile uint8_t j = 10;
volatile uint8_t shift = 0;

int main(void)
{	
	// Init PORTA
	DDRA = 0xFF;
	
	// Init the USART (38400 8N1)
	USARTInit(12);
	
	sei();
	
	// Init Os
	StartOS();

	//NOTE: Since OS is used, program will never get here!
    while(1);
}

TASK(Task_Update)
{
	PORTA = j;
	
	// Enqueue something for USART
	// -> demonstration of Queues and Semaphores
	USARTEnqueue(6, "update");
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Increment)
{
	// Increment global counter for leds
	j++;
	
	// Enqueue something for USART
	// -> demonstration of Queues and Semaphores
	USARTEnqueue(3, "inc");
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Shift)
{
	// Increment shifter variable
	shift++;
	if (shift == 8)
		shift = 0;
		
	// Terminate this task
	TerminateTask();
}