/*
 * Gpio.c
 *
 * Created: 28.12.2011 16:52:07
 *  Author: peer
 */ 

#include "Gpio.h"
#include <avr/io.h>

void GpioInit(void)
{
	DDRA = 0xFF;	  // Set PORTA (LEDs) as 8bit wide output
}