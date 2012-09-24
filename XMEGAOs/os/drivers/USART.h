/*
 * USART.h
 *
 * Created: 24.09.2012 23:49:24
 *  Author: peer
 */ 


#ifndef USART_H_
#define USART_H_

#include "queue.h"

void USARTInit(uint16_t ubrr_value);
void USARTEnqueue(uint8_t length, const char * text);


#endif /* USART_H_ */