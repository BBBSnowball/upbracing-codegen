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

void DacInit()
{
    // First we have to enable the audio amplifier by setting PQ3 high.
	PORTQ.PIN3CTRL = (PORTQ.PIN3CTRL & ~PORT_OPC_gm) | PORT_OPC_PULLUP_gc;
	
	// Use AVCC as Voltage Reference
    DACB.CTRLC = ( DACB.CTRLC & ~DAC_REFSEL_gm) | DAC_REFSEL_AVCC_gc;
    // Use Single Conversion Mode
    DACB.CTRLB = ( DACB.CTRLB & ~DAC_CHSEL_gm ) | DAC_CHSEL_SINGLE_gc;
    // Enable Channel 0 and Enable the (entire) DACB module
	DACB.CTRLA = DAC_CH0EN_bm | DAC_ENABLE_bm;
	
	_delay_ms(2000);
}

volatile uint8_t buffer[256];

int main(void)
{	
	// Init PortE
	GpioInit();
	
	// Init Audio DAC
	DacInit();
	
	// Fill buffer:
	for (int p = 0; p < 256; p++)
	{
		buffer[p] = p + 128;
	}
	for (int p = 0; p < 512; p++)
	{
		for (int o = 0; o < 256; o++)
		{
			// Wait for Data register Empty
			while ( !(DACB.STATUS & DAC_CH0DRE_bm) );            
            // Write the part of the triangle pointing upwards
            DACB.CH0DATA = buffer[o];
			//DACB.CH0DATA = o;
		}
	}
	
	// Init Os
	StartOS();

	//NOTE: Since OS is used, program will never get here!
    while(1);
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
