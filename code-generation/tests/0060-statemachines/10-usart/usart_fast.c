/*
 * usart_fast.c
 *
 *  Created on: Jan 3, 2013
 *      Author: benny
 */

#include "usart_fast.h"
#include <avr/pgmspace.h>
#include <avr/interrupt.h>
#include <internal/Os_Error.h>

QUEUE_IMPLEMENTATION(usart_recv_queue, USART_RECV_QUEUE_LENGTH, USART_RECV_QUEUE_PROCESSES, 1);
QUEUE_IMPLEMENTATION(usart_send_queue, USART_SEND_QUEUE_LENGTH, 1, USART_SEND_QUEUE_PROCESSES);

void usart_init_for_statemachine(void) {
	UBRRxH = ((USART_UBRR_VALUE) >> 8);
	UBRRxL = ((USART_UBRR_VALUE) & 0xff);

	// reset interrupt reasons except 'data register empty'
	UCSRxA = (1<<UDRE0);

	// set frame format
	// mask everything except parity, stop bit and character size
	//UCSRxC = (1<<UCSZx0) | (1<<UCSZx1);	// 8N1
	UCSRxC = (USART_MODE_8N1) & 0x3e;

	// enable RX and TX; enable receive interrupt
	UCSRxB = (1<<RXENx) | (1<<TXENx) | (1<<RXCIE0) | (0<<UDRIE0);
}

void usart_disable_all(void) {
	// disable all interrupts, RX and TX
	UCSRxB = 0;
}

void usart_send_str_P(const char* s) {
	//TODO implement this atomically
	while (1) {
		char c = pgm_read_byte(s);
		if (!c)
			return;
		usart_send(c);
		++s;
	}
}

void usart_receive_interrupt(void) {
	uint8_t c = UDRx;

	// We mustn't block on the queue because this
	// interrupt is executed in the context of an
	// arbitrary task.
	//TODO We could do that MUCH faster (without enqueueing a token).
	sem_token_t token = queue_start_enqueue(usart_recv_queue, 1);
	if (queue_continue_enqueue(usart_recv_queue, token))
		queue_finish_enqueue(usart_recv_queue, token, 1, &c);
	else {
		queue_abort_enqueue(usart_recv_queue, token);
		OS_report_error(OS_ERROR_USART_RX_OVERFLOW);
	}
}
