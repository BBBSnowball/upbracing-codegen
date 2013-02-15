/*
 * main.c
 *
 * Created: 20.12.2011 21:44:56
 *  Author: peer
 */ 

// You can use this #define to disable the semaphores. The test
// should fail, if you do so!
//#define DISABLE_SEMAPHORES

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

#include "Os.h"
#include <IPC/queue.h>
#include <internal/Os_Error.h>

#include <rs232.h>
#include <rs232-helpers.h>

// queue for 13 chars, 3 readers, 2 writers
// We need enough space to avoid deadlocks, i.e.
// the program mustn't deadlock, if the two
// 'largest' requests are at the front of the
// reader and writer waiting queues. We need at
// least (write_max + read_max - 1) bytes.
QUEUE(our_queue, 15, 3, 2);
// mutex for sending serial data
SEMAPHORE(mutex, 1, 2);

int main(void)
{	
	// Init GPIO: all leds on
	DDRA  = 0xFF;
	PORTA = 0xFF;

	// init USART (9600 baud, 8N1)
	usart_init();

	// make sure the USART works
	// (and PC can check that it has the right test)
	usart_send_str("async-queues test\r\n");

	// wait for the PC
	while (usart_recv() != 's')
		usart_send('?');

	// enable USART receive interrupts
	UCSRxB |= (1<<RXCIE0);

	// Init Os
	StartOS();

	//NOTE: Since OS is used, program will never get here!
    while(1);
}

// USART receive interrupt
#if (USE_USART_NUMBER == 0)
ISR(USART0_RX_vect) {
#elif (USE_USART_NUMBER == 1)
ISR(USART1_RX_vect) {
#endif
	switch (UDRx) {
	case 'e':
		usart_send_str("stopped");

		// don't send any further data
		// We simply make sure that the program
		// cannot continue.
		// (interrupts are disabled in an interrupt handler)
		while (1);
		break;

	case 'd':
		{
			// debug output
			Queue* q = QUEUE_REF(our_queue);
			Semaphore* mutex = QUEUE_MUTEX_REF(our_queue);
			Semaphore_n* prodsem = QUEUE_PROD_REF(our_queue);
			Semaphore_n* conssem = QUEUE_CONS_REF(our_queue);
			usart_send_str("\r\nqueue:");
			usart_send_str("\r\n  front:       "); usart_send_number(q->queue_front, 10, 0);
			usart_send_str("\r\n  end:         "); usart_send_number(q->queue_end,   10, 0);
			usart_send_str("\r\n  capacity:    "); usart_send_number(q->capacity,    10, 0);
			usart_send_str("\r\n  occupied:    "); usart_send_number(q->occupied,    10, 0);
			usart_send_str("\r\n  mutex-value: "); usart_send_number(mutex->count,          10, 0);
			usart_send_str("\r\n  mutex-ready: "); usart_send_number(mutex->ready_count,    10, 0);
			usart_send_str("\r\n  prod-value:  "); usart_send_number(prodsem->count,        10, 0);
			usart_send_str("\r\n  prod-ready:  "); usart_send_number(prodsem->ready_count,  10, 0);
			usart_send_str("\r\n  cons-value:  "); usart_send_number(conssem->count,        10, 0);
			usart_send_str("\r\n  cons-ready:  "); usart_send_number(conssem->ready_count,  10, 0);

			usart_send_str("\r\n  data:");
			for (uint8_t i=0;i<q->occupied;i++) {
				if (i > 0)
					usart_send(',');
				usart_send(' ');
				usart_send('\'');
				usart_send(q->q_queue[(q->queue_front + i) % q->capacity]);
				usart_send('\'');
			}

			usart_send_str("\r\n  mutex-waiting:");
			for (uint8_t i=mutex->queue_front;i!=mutex->queue_end;i = (i+1) % mutex->queue_cap) {
				if (i > 0)
					usart_send(',');
				usart_send(' ');
				usart_send_number(mutex->queue[i], 10, 0);
			}

			usart_send_str("\r\n  prod-waiting:");
			for (uint8_t i=prodsem->queue_front;i!=prodsem->queue_end;i = (i+1) % prodsem->queue_cap) {
				if (i > 0)
					usart_send(',');
				usart_send(' ');
				usart_send_number(prodsem->queue[i].pid, 10, 0);
				usart_send(' ');
				usart_send('(');
				usart_send_number(prodsem->queue[i].n, 10, 0);
				usart_send(')');
			}

			usart_send_str("\r\n  cons-waiting:");
			for (uint8_t i=conssem->queue_front;i!=conssem->queue_end;i = (i+1) % conssem->queue_cap) {
				if (i > 0)
					usart_send(',');
				usart_send(' ');
				usart_send_number(conssem->queue[i].pid, 10, 0);
				usart_send(' ');
				usart_send('(');
				usart_send_number(conssem->queue[i].n, 10, 0);
				usart_send(')');
			}

			usart_send_str("\r\n\r\n");
			break;
		}
	}
}

TASK(Writer1)
{
	sem_token_t queue_token1;
	
 	queue_token1 = queue_start_enqueue(our_queue,5);
	while (queue_continue_enqueue(our_queue,queue_token1) == FALSE)
 	{
 	}
 	if (queue_continue_enqueue(our_queue,queue_token1) == TRUE)
 	{
 		queue_finish_enqueue(our_queue,queue_token1,5,"Shift");
 	}

	// Terminate this task
	TerminateTask();
}

TASK(Writer2)
{
	sem_token_t queue_token2;
	
 	queue_token2 = queue_start_enqueue(our_queue,11);
	while (queue_continue_enqueue(our_queue,queue_token2) == FALSE)
 	{
 	}
 	if (queue_continue_enqueue(our_queue,queue_token2) == TRUE)
 	{
 		queue_finish_enqueue(our_queue,queue_token2,11,"Increment\r\n");
 	}

	// Terminate this task
	TerminateTask();
}

TASK(Reader1)
{
	const uint8_t len = 3;
	uint8_t data[3];
	sem_token_t queue_token4;
	
	//NOTE The tick period is so long that the
	//     OS won't interrupt the task between
	//     dequeue and usart_send_str.
	//     We wouldn't want to move the dequeue
	//     into the mutex, as this would defeat
	//     the purpose of the test.
	queue_token4 = queue_start_dequeue(our_queue,3);
	while (queue_continue_dequeue(our_queue,queue_token4) == FALSE)
 	{
 	}
	if (queue_continue_enqueue(our_queue,queue_token4) == TRUE)
 	{
 		queue_finish_dequeue(our_queue,queue_token4,3,data);
 	}
	
	sem_wait(mutex);
	usart_send('.');
	//NOTE usart_send_str expects a null-terminated
	//     string, so we have to use usart_send.
	for (uint8_t i=0;i<len;i++)
		usart_send(data[i]);
	sem_signal(mutex);

	// Terminate this task
	TerminateTask();
}

TASK(Reader2)
{
	const uint8_t len = 5;
	uint8_t data[5];
	sem_token_t queue_token5;
	
	//NOTE The tick period is so long that the
	//     OS won't interrupt the task between
	//     dequeue and sem_wait.
	//     We wouldn't want to move the dequeue
	//     into the mutex, as this would defeat
	//     the purpose of the test.
	queue_token5 = queue_start_dequeue(our_queue,3);
	while ( ! queue_continue_dequeue(our_queue,queue_token5))
 	{
 	}
	if ( ! queue_continue_enqueue(our_queue,queue_token5))
		usart_send_str("ERR(I): queue_continue_wait returned true, then false\r\n");
		
 	queue_finish_dequeue(our_queue,queue_token5,3,data);
 	
	
	sem_wait(mutex);
	usart_send(',');
	//NOTE usart_send_str expects a null-terminated
	//     string, so we have to use usart_send.
	for (uint8_t i=0;i<len;i++)
		usart_send(data[i]);
	sem_signal(mutex);
	
	// Terminate this task
	TerminateTask();
}

TASK(Reader3)
{
	sem_token_t queue_token3;
	sem_token_t queue_token6, queue_token7, queue_token8;
	
	//abort enqueue before ready
	queue_token3 = queue_start_enqueue(our_queue,5);
	queue_abort_enqueue(our_queue, queue_token3);
	
	//abort queue enqueue after ready
	queue_token3 = queue_start_enqueue(our_queue, 10);
	while( ! queue_continue_dequeue(our_queue,queue_token3))
		;
	queue_abort_enqueue(our_queue, queue_token3);
	
	// query for invalid token and abort
	queue_token3 = queue_start_enqueue(our_queue, 20);
	queue_abort_enqueue(our_queue, queue_token3);
	
	//aborting in a funny way
	queue_token3 = queue_start_enqueue(our_queue, 1);
	queue_token6 = queue_start_enqueue(our_queue, 1);
	queue_token7 = queue_start_enqueue(our_queue, 1);
	_delay_ms(10);
	queue_abort_enqueue(our_queue, queue_token6);
	queue_abort_enqueue(our_queue, queue_token3);
	queue_abort_enqueue(our_queue, queue_token7);
	
	//abort invalid token
	queue_abort_enqueue(our_queue, queue_token8);
	
	// Terminate this task
	TerminateTask();
}

