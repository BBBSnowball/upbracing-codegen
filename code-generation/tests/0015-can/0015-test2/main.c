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
	send_TestMsg01_nowait();
	send_TestMsg01_nowait();
	send_TestMsg01_wait();

	send_TestMsg02_wait(42);
	send_TestMsg02_wait(0x55);
	send_TestMsg02_wait(0x42);

	send_TestMsg03_wait(42, 7);

	send_TestMsg04_wait(0x1234);

	send_TestMsg05_nowait();

	send_TestMsg06_nowait(0x1234);
	send_TestMsg06_nowait(0x5678);

	send_TestMsg07_nowait(0x1730, 0xfa);
	send_TestMsg07_nowait(0x2231, 0xae);

	send_TestMsg08_nowait(0x1730, 0xfa);
	send_TestMsg08_nowait(0x2231, 0xae);

	send_TestMsg09_nowait(0x37);
	send_TestMsg10_nowait(0x38);
	send_TestMsg11_nowait(0x39);
	send_TestMsg12_nowait(0x40);
	send_TestMsg13_nowait(0x41);
	send_TestMsg14_nowait(0x42);

	send_TestMsg15_nowait(0x5790);
	send_TestMsg16_nowait(-10);
	send_TestMsg16_nowait( 10);
	send_TestMsg17_nowait(-2, -100, -100);
	send_TestMsg17_nowait(-1000, 100, 127);
}

void testB(void) {
	//TODO send some messages and invoke testB to find out whether that has worked
	// Do it again with different values.
	//TODO Message counters, ...

	// "- Msg:Signal = 0xabc\r\n"
#	define PRINT_VAR2(msg, signal, varname) \
			usart_send_str("- " #msg ":" #signal " = "); \
			usart_send_number(get##varname(), 16, 0); \
			usart_send_str("\r\n");
#	define PRINT_VAR(msg, signal) PRINT_VAR2(msg, signal, msg##_##signal)
	PRINT_VAR(TestMsg02, Test1);
	PRINT_VAR(TestMsg03, Test1);
	PRINT_VAR(TestMsg03, Test2);
	PRINT_VAR(TestMsg04, Test1);
	PRINT_VAR(TestMsg06, Test1);
	PRINT_VAR(TestMsg07, Test1);
	PRINT_VAR(TestMsg07, Test2);
	PRINT_VAR(TestMsg08, Test1);
	PRINT_VAR(TestMsg08, Test2);
	PRINT_VAR(TestMsg09, Test1);
	PRINT_VAR(TestMsg10, Test1);
	PRINT_VAR(TestMsg11, Test1);
	PRINT_VAR(TestMsg12, Test1);
	PRINT_VAR(TestMsg13, Test1);
	PRINT_VAR(TestMsg14, Test1);
	PRINT_VAR(TestMsg15, Test1);
	PRINT_VAR(TestMsg16, Test1);
	PRINT_VAR(TestMsg17, Test1);
	PRINT_VAR(TestMsg17, Test2);
	PRINT_VAR2(TestMsg17, Test3, Test3);
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
