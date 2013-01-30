/*
 * usart_fast_config.h
 *
 *  Created on: Jan 27, 2013
 *      Author: benny
 */

#ifndef USART_FAST_CONFIG_H_
#define USART_FAST_CONFIG_H_

////////////
// config //
////////////

// use usart 1 because usart 0 conflicts with the ISP interface
#define USE_USART_NUMBER 1

// count of processes accessing the queues
// (including statemachines that use async tokens)
#define USART_RECV_QUEUE_PROCESSES 1
#define USART_SEND_QUEUE_PROCESSES 4

// length of the queues (buffer)
#define USART_RECV_QUEUE_LENGTH 10
#define USART_SEND_QUEUE_LENGTH 32


// baud rate configuration

// Either provide a value for UBRR (not recommended) ...
// (see datasheet 17.4 Clock Generation and
//  17.12 Examples of Baud Rate Setting)
// example for 9600 Baud with cpu speed of 8MHz
//#define USART_UBRR_VALUE 51

// .. or choose a baud rate and let the driver do the calculations
#define USART_BAUD_RATE 9600


// Use double speed mode?
// (see datasheet 17.4.2 Double Speed Operation (U2X))
// If you are unsure, use the default setting of 0 (off).
// valid values: 0 -> off, 1 -> on
#define USART_U2X 0


// mode
// This is the value for the UCSRxC register. You may set
// the parity (UPMn1:0), stop bit (USBSn) and character
// size (UCSZn1:0; 9 bit not possible).
#define USART_MODE USART_MODE_8N1


#endif /* USART_FAST_CONFIG_H_ */
