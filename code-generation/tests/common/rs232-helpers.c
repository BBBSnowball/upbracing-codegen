#include "rs232-helpers.h"

// If the functions are marked static with RS232_SPEC,
// the compiler print a warning for the ones that aren't
// used. We suppress this warning.
#ifdef __GNUC__
#	define PROBABLY_UNUSED __attribute__ ((unused))
#else
#	define PROBABLY_UNUSED
#endif

RS232_SPEC void usart_send_number(int32_t number, uint8_t base, uint8_t min_places) PROBABLY_UNUSED;


RS232_SPEC void usart_send_number(int32_t number, uint8_t base, uint8_t min_places) {
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
		if (number == 0 && base == 10)
			// cannot use '0' to fill the space because that would
			// make it an octal number
			digit = ' ';
		else if (digit < 10)
			digit += '0';
		else
			digit += 'a' - 10;
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
