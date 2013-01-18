#include <avr/io.h>
#include <util/delay.h>

#include <avr/interrupt.h>
#include <avr/wdt.h>
#include <avr/pgmspace.h>

#include <rs232.h>
#include <rs232-helpers.h>

#include "main.h"

#include "can-helper-master.c.inc"

#include "gen/can.h"
#include "gen/global_variables.h"

void testA(void) {
	send_TestMsg1_nowait();
	send_TestMsg1_nowait();
	send_TestMsg1_wait();

	send_TestMsg2_wait(42);
	send_TestMsg2_wait(0x55);
	send_TestMsg2_wait(0x42);

	send_TestMsg3_wait(42, 7);

	send_TestMsg4_wait(0x1234);

	send_TestMsg5_nowait();

	send_TestMsg6_nowait(0x1234);
	send_TestMsg6_nowait(0x5678);

	send_TestMsg7_nowait(0x1730, 0xfa);
	send_TestMsg7_nowait(0x2231, 0xae);

	send_TestMsg8_nowait(0x1730, 0xfa);
	send_TestMsg8_nowait(0x2231, 0xae);
}

void testB(void) {
	//TODO send some messages and invoke testB to find out whether that has worked
	// Do it again with different values.
	//TODO Message counters, ...

	// "- Msg:Signal = 0xabc\r\n"
#	define PRINT_VAR(msg, signal) \
			usart_send_str("- " #msg ":" #signal " = "); \
			usart_send_number(get##msg##_##signal(), 16, 0); \
			usart_send_str("\r\n");
	PRINT_VAR(TestMsg2, Test1);
	PRINT_VAR(TestMsg3, Test1);
	PRINT_VAR(TestMsg3, Test2);
	PRINT_VAR(TestMsg4, Test1);
	PRINT_VAR(TestMsg6, Test1);
	PRINT_VAR(TestMsg7, Test1);
	PRINT_VAR(TestMsg7, Test2);
	PRINT_VAR(TestMsg8, Test1);
	PRINT_VAR(TestMsg8, Test2);
#	undef PRINT_VAR

	usart_send_str("\r\n");
}

typedef void (*test_ptr)(void);

test_ptr tests[] PROGMEM = { testA, testB };

int main(void) {
	// init GPIOs
	DDRA = 0xff; // Set LED pins as output
	DDRE = 0x00; // Set button pins as input
	PORTE = 0xf4; // Enable pull ups for buttons

	PORTA = 0x01;

	// init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\r\nStarting CAN master.\r\n");

	usart_send_str("Initialize CAN with 500kbps.\r\n");
	can_init_500kbps();

	usart_send_str("Initialize CAN mobs.\r\n");
	can_init_mobs();
	can_helper_master_init();

	_delay_ms(100);

	usart_send_str("Ready.\r\n");

	// Interrupts are needed to receive can messages
	sei();

	while(1) {
		if (usart_recv_char_available()) {
			uint8_t c = usart_recv();
			if (!can_helper_master_handle_char(c)) {
				// we may use the char
				if (c >= 'A' && c - 'A' < sizeof(tests) / sizeof(*tests)) {
					usart_send_str_P(PSTR("Running test ")); usart_send(c); usart_send_str_P(PSTR("\r\n"));

					// read test number (c-'A') from array in program space
					test_ptr test = (test_ptr)pgm_read_word(&tests[c-'A']);
					// invoke it
					test();
				} else if (c == '\r' || c == '\n') {
					// ignore
				} else
					usart_send('?');
			}
		}
	}
}
