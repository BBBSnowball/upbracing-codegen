/*
 * XMEGATest2.c
 *
 * Created: 20.12.2011 21:44:56
 *  Author: peer
 */ 

#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/delay.h>
#include "OSEK.h"

volatile uint8_t j = 0;

int main(void)
{	
	// Init PORTA
	//GpioInit();
	DDRA = 0xFF;
	
	sei();
	
	// Init Os
	StartOS();

	//NOTE: Since OS is used, program will never get here!
    while(1);
}

TASK(Task_Update)
{
	// Update the port with the leds connected
	PORTA = j;
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Increment)
{
	// Increment global counter for leds
	j++;
	
	// Terminate this task
	TerminateTask();
}