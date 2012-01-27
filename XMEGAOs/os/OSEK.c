/*
 * OSEK.c
 *
 * Created: 22.12.2011 21:43:40
 *  Author: peer
 */ 

#include "Os_cfg_generated.h"
#include "OSEK.h"
#include "Timer.h"
#include "Sdram.h"
#include <avr/interrupt.h>
#include <stdlib.h>
#include <avr/delay.h>

#ifndef OS_CFG_CC
#error No Conformance Class specified!
#endif

#if OS_CFG_CC != BCC1 && OS_CFG_CC != BCC2 && \
	OS_CFG_CC != ECC1 && OS_CFG_CC != ECC2
#error No valid Conformance Class specified
#endif


void StartOS(void) 
{
	uint8_t i = 0;
	for (i = 0; i < OS_NUMBER_OF_TCBS; i++)
	{
		InitializeStackForTask(&os_tcbs[i]);
	}
	
	// Init Clock
	ClockInit();
	
	// Init UART:
	PORTC.DIRSET = PIN3_bm;							// TXD as Output
	PORTC.OUTSET = PIN3_bm;							// TXD high
	PORTC.DIRCLR = PIN2_bm;							// RXD as Input		
	USARTC0.CTRLC = USART_CMODE_ASYNCHRONOUS_gc		// Async
					| USART_PMODE_DISABLED_gc		// No parity
					| USART_CHSIZE_8BIT_gc;			// 8bit (single stop implicit)
	USARTC0_BAUDCTRLA = 34;							// BSEL = 34
	USARTC0_BAUDCTRLB = 0;							// No BSCALE, no upper BSEL bits...
	USARTC0.CTRLB = USART_TXEN_bm					// Enable Transmitter
					| USART_RXEN_bm					// Enable Receiver
					| USART_CLK2X_bm;				// Double transmission speed (64MHz)
	//uint8_t dummyread = USARTC0.DATA;
	// Hopefully, we are done now...	
	
	// Write to UART:	
	for (volatile int p = 0; p < 256; p++)
	{
		USARTC0.DATA = p;
		_delay_ms(5);
	}
	
	// Init external SDRAM
	SdramInit();
	
	// Init Timer
	TimerInit();
	
	// Start first task
	StartFirstTask();
	
	// Globally enable interrupts
	sei();
}