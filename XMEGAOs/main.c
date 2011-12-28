/*
 * XMEGATest2.c
 *
 * Created: 20.12.2011 21:44:56
 *  Author: peer
 */ 

#include <avr/io.h>
#include <avr/interrupt.h>
#include "Clock.h"
#include "Timer.h"
//#include "os/generated/Os_Cfg.h"
#include "OSEK.h"

volatile uint8_t j = 0;

void PortsInit() 
{
	PORTE.DIR = 0xFF;	  // Set PORTE as 8bit wide output
}	

int main(void)
{
	// init clock
	ClockInit();
	
	// Init PortE
	PortsInit();
	
	// Init Timer
	TimerInit(TIMER_PRESCALE_1_bm, 0x7D00);
	
	// Init Os
	StartOS();

    while(1)
    {
        //TODO:: Please write your application code 
    }
}

TASK(Task_Update)
{
	// Update the port with the leds connected
	PORTE.OUT = j;
	
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

ALARMCALLBACK(Alarm_Test)
{
	j = 0;
}