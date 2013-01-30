/*
 * usart_fast.h
 *
 *  Created on: Jan 2, 2013
 *      Author: benny
 */

#ifndef USART_FAST_H_
#define USART_FAST_H_

// This is similar to rs232.h, but it uses
// - interrupts to make it faster
// - queues as buffer and for synchronization
// - more than one process can use the usart
//   (probably not a good idea for the receiving half *g*)

#include <avr/io.h>
#include <IPC/queue.h>
#include <string.h>
#include <stdint.h>

#include "statemachines.h"


// a few constants that can be used in the config header

#define USART_MODE_8N1 ((1<<UCSZx0) | (1<<UCSZx1))


// config header
#include "usart_fast_config.h"

// use macros for all usart registers
// -> we can easily switch to usart 0 or 1

#if (USE_USART_NUMBER == 0)

#define UCSRxA UCSR0A
#define UDREx  UDRE0
#define RXCx   RXC0

#define UCSRxB UCSR0B
#define RXENx  RXEN0
#define TXENx  TXEN0

#define UCSRxC UCSR0C
#define UCSZx0 UCSZ00
#define UCSZx1 UCSZ01

#define UBRRxH UBRR0H
#define UBRRxL UBRR0L

#define UDRx   UDR0

#define USARTx_RX_vect    USART0_RX_vect
#define USARTx_UDRE_vect  USART0_UDRE_vect

#elif (USE_USART_NUMBER == 1)

#define UCSRxA UCSR1A
#define UDREx  UDRE1
#define RXCx   RXC1

#define UCSRxB UCSR1B
#define RXENx  RXEN1
#define TXENx  TXEN1

#define UCSRxC UCSR1C
#define UCSZx0 UCSZ10
#define UCSZx1 UCSZ11

#define UBRRxH UBRR1H
#define UBRRxL UBRR1L

#define UDRx   UDR1

#define USARTx_RX_vect    USART1_RX_vect
#define USARTx_UDRE_vect  USART1_UDRE_vect

#else
#	error Please select USART 0 or 1
#endif


// the queues
QUEUE_EXTERNAL(usart_recv_queue);
QUEUE_EXTERNAL(usart_send_queue);

void usart_init(void);

inline static void usart_send(uint8_t data) {
	queue_enqueue(usart_send_queue, data);
}

inline static uint8_t usart_recv(void) {
	return queue_dequeue(usart_recv_queue);
}

inline static void usart_recv_many(uint8_t* buf, uint8_t count) {
	queue_dequeue_many(usart_recv_queue, count, buf);
}

// never send more bytes than there is space in the queue
// (This would block forever.)
inline static void usart_send_many(const uint8_t* s, uint8_t count) {
	queue_enqueue_many(usart_send_queue, count, s);
}

// never send more bytes than there is space in the queue
// (This would block forever.)
inline static void usart_send_str(const char* s) {
	usart_send_many((const uint8_t*)s, strlen(s));
}

void usart_send_str_P(const char* s);


// baud rate calculation
#ifdef USART_UBRR_VALUE
	// user provides the value - nothing to do for us
#	ifdef USART_BAUD_RATE
#		error "Please set either a baud rate or the value of UBRR (both are set)"
#	endif
#else
#	ifndef USART_BAUD_RATE
#		error "Please set either a baud rate or the value of UBRR (none of them is set)"
#	endif

	// prescaler is different in 'double speed mode'
#	if USART_U2X > 0
#		define USART_PRESCALER 8
#	else
#		define USART_PRESCALER 16
#	endif

// datasheet 17.4.1 Internal Clock Generation – Baud Rate Generator (page 180)
// UBRR = F_CPU / (prescaler * BAUD) - 1
// rounding to nearest integer (with integer arithmetics):
// UBRR = (F_CPU + prescaler * BAUD / 2) / (prescaler * BAUD) - 1
//NOTE We have to make sure that long ints are used to avoid numeric overflows, but the preprocessor
//     doesn't support casts in the calculation. We could disable the casts for the #ifs, but we rather
//     fake the casts by multiplication with 1UL.
#define USART_UBRR_VALUE ((F_CPU + (1UL * (USART_PRESCALER) * (USART_BAUD_RATE))/2) / (1UL * (USART_PRESCALER) * (USART_BAUD_RATE)) - 1)

#if (USART_UBRR_VALUE) > 0xffff
#	error "Overflow of UBRR value. You might want to disable U2X (if enabled) or choose a higher baud rate."
#	define USART_UBRR_VALUE 0xffff
#endif

// Calculate error (in tenth of a percent)
// (datasheet 17.12 Examples of Baud Rate Setting,
//  adapted to tenth of percent and integer arithmetics)
// error = (1 - BAUDRATE_real / BAUDRATE_expected) * 1000
//       = 1000 - BAUDRATE_real * 1000 / BAUDRATE_expected
//   -- baud = F_CPU / prescaler / (UBRR + 1)
//       = 1000 - F_CPU * 1000 / prescaler / (UBRR + 1) / BAUDRATE_expected
// We center the value around 1000 because the preprocessor seems
// to use unsigned values (and C casts don't work).
// ->  baud = F_CPU * 1000 / prescaler / (UBRR + 1) / BAUDRATE_expected
#define USART_BAUD_ERROR_PERMILL (F_CPU * 1000L / (USART_PRESCALER) / ((USART_UBRR_VALUE) + 1) / (USART_BAUD_RATE))

// Anything below 0.5% (error <= 5) is good (according to the datasheet).
// Anything below 2% (error > 20) is ok according to some source I don't
// remember (probably a value from the RS232 standard quoted somewhere).
// However, we accept anything up to 2.1% because some common baud rates
// yield this error (e.g. 57600 baud at 16Mhz cpu frequency).
#if (USART_BAUD_ERROR_PERMILL) > 1021 || (USART_BAUD_ERROR_PERMILL) < (1000-21)
//DEBUG#	error "Baud rate is VERY inaccurate (more than 2.1%). Use a different baud rate or system frequency. Switching double speed mode (U2X) might help a bit."
#else
#	if (USART_BAUD_ERROR_PERMILL) > 1005 || (USART_BAUD_ERROR_PERMILL) < (1000-5)
#		warning "Baud rate is inaccurate, but most likely it will work."
#	endif
#endif

#endif	// not defined USART_UBRR_VALUE

#endif /* USART_FAST_H_ */
