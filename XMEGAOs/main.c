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

volatile uint8_t j = 10;

void USARTInit(uint16_t ubrr_value)
{
	//Set Baud rate

	UBRR0L = ubrr_value;
	UBRR0H = (ubrr_value>>8);
	
	 /*Set Frame Format

   >> Asynchronous mode
   >> No Parity
   >> 1 StopBit

   >> char size 8

   */

   UCSR0C=(1<<UMSEL0)|(3<<UCSZ0);

   //Enable The receiver and transmitter

   UCSR0B=(1<<RXEN)|(1<<TXEN);

}

void USARTWriteChar(char data)
{
	//Wait until the transmitter is ready

	while( ! ( UCSR0A & (1<<UDRE0)));
	

	//Now write the data to USART buffer

	UDR0=data;
}

int main(void)
{	
	// Init PORTA
	//GpioInit();
	DDRA = 0xFF;
	
	sei();
	USARTInit(51);
	
	// Init Os
	StartOS();

	//NOTE: Since OS is used, program will never get here!
    while(1);
}

TASK(Task_Update)
{
	// Update the port with the leds connected
	//OS_ENTER_CRITICAL();
	PORTA = j;
	//OS_EXIT_CRITICAL();
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Increment)
{
	// Increment global counter for leds
	//j++;
	//OS_ENTER_CRITICAL();
	j++;
	//OS_EXIT_CRITICAL();
	// Terminate this task
	TerminateTask();
}

TASK(Task_Shift)
{
	//Left shifts global counter for leds
	//OS_ENTER_CRITICAL();
	uint8_t temp = j << 1;
	uint8_t lsb = (j >> 7) & 0x01;
	j = temp | lsb;
	//OS_EXIT_CRITICAL();
	//PORTA = 0x01;
	//USARTWriteChar('a');
	//Terminate this task
	TerminateTask();
}
