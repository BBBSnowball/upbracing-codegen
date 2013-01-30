/*
 * main.c
 *
 *  Created on: Jan 27, 2013
 *      Author: benny
 */

#include "Os.h"
#include "usart_fast.h"

int main(void) {
	StartOS();

	UDR1 = 'X';

	while (1);
}

TASK(Task1) {
	DDRA = 0xff;
	PORTA = 1;

	usart_recv_sm_event_enable();

	UDR1 = 'Y';

	while (1) {
		uint8_t c = usart_recv();
		PORTA++;
		UDR1 = c+1;
	}
}
