#include "rs232-helpers.h"

void usart_send_number(int32_t number, uint8_t base, uint8_t min_places) {
	char chars[32 + 2 + 1];
	char* x = chars + sizeof(chars) / sizeof(*chars);
	*(--x) = 0;

	if (number < 0) {
		usart_send('-');
		number = -number;
	}

	// special case for zero which would otherwise yield an empty string
	if (number == 0) {
		*(--x) = '0';
		if (min_places)
			--min_places;
	}

	while (number || min_places) {
		uint8_t digit = number % base;
		if (digit < 10)
			digit += '0';
		else
			digit += 'a';
		*(--x) = digit;

		number /= base;
		if (min_places)
			--min_places;
	}

	switch (base) {
	case  2:	usart_send('0'); usart_send('b'); break;
	case  8:	usart_send('0');                  break;
	case 10:	                                  break;
	case 16:	usart_send('0'); usart_send('x'); break;
	default:
		usart_send_number(base, 10, 0);
		usart_send('#');
		break;
	}

	usart_send_str(x);
}
