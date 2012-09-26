/*
 * XMEGATest2.c
 *
 * Created: 20.12.2011 21:44:56
 *  Author: peer
 */ 

#include <avr/io.h>
#include <avr/interrupt.h>
//#include <util/delay.h>
#include "OSEK.h"
#include "queue.h"
#include "USART.h"

volatile uint8_t j = 10;
volatile uint8_t shift = 0;


int main(void)
{	
	// Init PORTA
	//GpioInit();
	DDRA = 0xFF;
	
	USARTInit(51);
	
	sei();
	
	
	// Init Os
	StartOS();

	//NOTE: Since OS is used, program will never get here!
    while(1);
}

TASK(Task_Update)
{
	// Update the port with the leds connected
	uint8_t temp = j << shift;
	uint8_t lsb = (j >> (8 - shift)) & 0x01;
	PORTA = temp | lsb;
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Increment)
{
	// Increment global counter for leds
	j++;
	
	// Enqueue something for USART
	
	USARTEnqueue(5, "First");
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Shift)
{
	// Increment shifter variable
	shift++;
	if (shift == 8)
		shift = 0;
	
	// Enqueue something different for USART
	USARTEnqueue(6, "Second");
	
	// Terminate this task
	TerminateTask();
}
