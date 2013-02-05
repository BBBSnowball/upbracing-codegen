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
//#include "USART.h"
//#include "Gpio.h"
//#include "semaphore.h"
//#include "Os_Error.h"
#include "gen/eeprom_accessors.h"
#include "D:\coll\project\program\program\code-generation\tests\common\rs232.h"
#include "D:\coll\project\program\program\code-generation\tests\common\rs232-helpers.h"

volatile uint8_t j = 1;
volatile uint8_t shift = 0;

#if PROGRAM_MODE == TEST_SYNC_QUEUE
//SEMAPHORE(led,1,4);
//SEMAPHORE_N(led,5,1);
#elif PROGRAM_MODE == KRISHNA_204b6f
//SEMAPHORE(led,1,4);
//SEMAPHORE_N(led,5,1);
#endif

//QUEUE(ipc,10,1,2);

int main(void)
{	
	// Init GPIO: (demo: DDRA = 0xFF)
	//GpioInit();
	//PORTA = 0xFF;
	uint8_t i, ver;
	
	
	// Init the USART (57600 8N1)
	//USARTInit(8);
	
	// NOTE(Peer):
	// DO NOT enable Interrupts here
	// StartOs will call Os_StartFirstTask.
	// Os_StartFirstTask will enable interrupts when system is ready.
	// If we enable interrupts now, the first timer tick
	// most likely comes too early if the timer freq is high enough.
	//// Globally enable interrupts
	//sei();
	
	// Init Os
	//StartOS();
	usart_init();
	
	ver = READ_CHECK();
	if(ver == 0xff)
	{
		WRITE_A(10);
		WRITE_CHECK(0xAA);
	}
	if(ver == 0xAA)
	{
		i = READ_A();
		usart_send_number(i,10,0);
	}
	

	//NOTE: Since OS is used, program will never get here!
    //while(1);
}

