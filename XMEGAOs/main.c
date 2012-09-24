/*
 * XMEGATest2.c
 *
 * Created: 20.12.2011 21:44:56
 *  Author: peer
 */ 

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

#include "OSEK.h"
#include "semaphore.h"

volatile uint8_t j = 10;
SEMAPHORE(led,1,5);
QUEUE(uart,10,1,2);

void USARTInit(uint16_t ubrr_value)
{
	//Set Baud rate

	
	UBRR0H = (ubrr_value>>8);
	UBRR0L = ubrr_value;
	
	 /*Set Frame Format

   >> Asynchronous mode
   >> No Parity
   >> 1 StopBit

   >> char size 8

   */

   //UCSR0C=(1<<UMSEL0)|(3<<UCSZ0);
   UCSR0C = (0<<UMSEL0) | (0<<UPM0) | (1<<USBS0) | (3<<UCSZ0); // 8data, no parity & 2 stop bits

   //Enable The receiver and transmitter

   UCSR0B=(1<<RXEN0)|(1<<TXEN0);

}

void USARTWriteChar(char data)
{
	//Wait until the transmitter is ready

	while( ! ( UCSR0A & (1<<UDRE0)))
	{
		
	}
	

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
	
	//USARTWriteChar('u');
	
	// Init Os
	StartOS();

	//NOTE: Since OS is used, program will never get here!
    while(1);
}

TASK(Task_Update)
{
	// Update the port with the leds connected
	//OS_ENTER_CRITICAL();
	sem_wait(led);
	USARTWriteChar('a');
	_delay_ms(1000);
	USARTWriteChar('a');
	_delay_ms(1000);
	USARTWriteChar('a');
	sem_signal(led);
	
	
	//OS_EXIT_CRITICAL();
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Increment)
{
	// Increment global counter for leds
	//j++;
	//OS_ENTER_CRITICAL();
	sem_wait(led);
	//j++;
	USARTWriteChar('b');
	_delay_ms(1000);
	USARTWriteChar('b');
	_delay_ms(1000);
	USARTWriteChar('b');
	sem_signal(led);
	//OS_EXIT_CRITICAL();
	
	// Terminate this task
	TerminateTask();
}

TASK(Task_Shift)
{
	//Left shifts global counter for leds
	//OS_ENTER_CRITICAL();
	sem_wait(led);
	//uint8_t temp = j << 1;
	//uint8_t lsb = (j >> 7) & 0x01;
	//j = temp | lsb;
	USARTWriteChar('c');
	_delay_ms(1000);
	USARTWriteChar('c');
	_delay_ms(1000);
	USARTWriteChar('c');
	sem_signal(led);
	//OS_EXIT_CRITICAL();
	//PORTA = 0x01;
	
	//Terminate this task
	TerminateTask();
}
