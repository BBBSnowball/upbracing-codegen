/*
 * Gpio.c
 *
 * Created: 28.12.2011 16:52:07
 *  Author: peer
 */ 

#include "Gpio.h"
#include <avr/io.h>

extern void GpioInit(void)
{
	PORTE.DIR = 0xFF;	  // Set PORTE as 8bit wide output
}