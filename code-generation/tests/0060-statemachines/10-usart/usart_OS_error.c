/*
 * usart_OS_error.c
 *
 *  Created on: Jan 27, 2013
 *      Author: benny
 */

#include <internal/Os_Error.h>

#include <util/delay.h>
#include <avr/pgmspace.h>

// We use the simple USART driver to report errors.

// We cannot link to the implementation because the
// fast driver uses the same function names.
// -> We include them here and declare them static.
#define RS232_SPEC static

// include the headers
#include <rs232.h>
#include <rs232-helpers.h>

// include the implementation, as well
// (We cannot link to it 'cause we marked it static; see above)
#include <rs232.c>
#include <rs232-helpers.c>

extern void OS_error(OS_ERROR_CODE error) {
	//NOTE We could go on, if the error is non-fatal, but we should
	//     reset the USART in that case (which we can't) and our
	//     output might be scrolled from the screen too quickly.
	while (1) {
		usart_init();

		usart_send_str_P(PSTR("OS_error: "));
		usart_send_number(error, 16, 8);
		usart_send_str_P(PSTR("\r\n"));

		// 0x4242 is a test error code, so we print it once and continue
		if (error == 0x4242)
			return;

		_delay_ms(1000);
	}
}
