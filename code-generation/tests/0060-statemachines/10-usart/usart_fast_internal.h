/*
 * usart_fast_internal.h
 *
 *  Created on: Jan 27, 2013
 *      Author: benny
 */

#ifndef USART_FAST_INTERNAL_H_
#define USART_FAST_INTERNAL_H_

#include "usart_fast.h"

void usart_init_for_statemachine(void);
void usart_disable_all(void);

void usart_receive_interrupt(void);

#endif /* USART_FAST_INTERNAL_H_ */
