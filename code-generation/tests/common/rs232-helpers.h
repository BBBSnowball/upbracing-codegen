#ifndef RS232_HELPERS_H
#define RS232_HELPERS_H

#include <stdint.h>
#include "rs232.h"

void usart_send_number(int32_t number, uint8_t base, uint8_t min_places);

inline static void usart_send_number_binary(uint32_t number) {
	usart_send_number(number, 2, 0);
}

#endif // not defined RS232_HELPERS_H
