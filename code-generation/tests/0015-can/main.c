/*
 * main.c
 *
 *  Created on: Oct 09, 2012
 *      Author: sven
 */

#define CANTEST_VERSION 0x01
#define CANTEST_INIT_REQUEST 0x01
#define CANTEST_INIT_ACK 0x02
#define CANTEST_TEST1_VALUE 0x55
#define CANTEST_TEST2A_VALUE 0x1337
#define CANTEST_TEST2B_VALUE 0x4242
#define CANTEST_TEST2C_VALUE 0x7fff
#define CANTEST_TEST3A_VALUE 0x3434
#define CANTEST_TEST3B_VALUE 0xf24b
#define CANTEST_TEST4A_VALUE 0xa1
#define CANTEST_TEST4B_VALUE 0xbe


#include <util/delay.h>

#include <avr/io.h>
#include <avr/pgmspace.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>

#include "Os.h"
#include "drivers/Gpio.h"
#include "semaphores/semaphore.h"
#include "internal/Os_Error.h"

#include "gen/can.h"
#include "gen/global_variables.h"
#include "gen/Os_cfg_application.h"

#include "rs232.h"
#include "rs232-helpers.h"

BOOL testmaster = FALSE;

void modeSetup();
void testMaster();

int main(void) {
	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting CAN test.");

	usart_send_str("\nInitialize CAN mobs.");
	can_init_mobs();

	_delay_ms(100);

	// Set up as either master or slave mode
	modeSetup();

	if (testmaster) {
		testMaster();

		// Start OS to test the task based sending
		StartOS();
	}

	while(1);
}

void modeSetup() {
	// Assume slave mode and wait until either the master sends a message to start the test
	// or the control pc sends the ASCII value "M" over RS232 and change to master mode
	usart_send_str("\nWaiting for mode setup.");

	char c;

	while(!getTestNumber()) {
		if (usart_recv_char_available()) {
			c = usart_recv();
			if (c == 'M') {
				testmaster = true;
				usart_send_str("\nSet board to master mode.");
				send_InitTestphase_nowait(CANTEST_VERSION, CANTEST_INIT_REQUEST);
				usart_send_str("\nWaiting for slave board...");
			} else {
				usart_send_str("\nERROR: Unknown command.");
			}
		}
	}

	if (testmaster) {
		if (getTestNumber() != CANTEST_INIT_ACK)
			usart_send_str("\nERROR: Slave board reply is not an ACK.");
	} else {
		if (getTestNumber() == CANTEST_INIT_REQUEST) {
			PORTA = 0x81; // Two edge LEDs on
			usart_send_str("\nSet board to slave mode");
			usart_send_str("\nWARNING: This board does not output the test results.");
			send_InitTestphase_nowait(CANTEST_VERSION, CANTEST_INIT_ACK);
		}
	}

	if (getTestVersion() != CANTEST_VERSION) {
		usart_send_str("\nERROR: Other board uses different test version.");
	}
}

void assertValue(int32_t expected, int32_t compare) {
	if (expected == compare) {
		usart_send_str("\nTest successful!");
	} else {
		usart_send_str("\nTest failed. Expected ");
		usart_send_number(expected, 16, 2);
		usart_send_str(", but received ");
		usart_send_number(compare, 16, 2);
	}
}

void assert2Values(int32_t expected1, int32_t expected2, int32_t compare1, int32_t compare2) {
	if (expected1 == compare1 && expected2 == compare2) {
		usart_send_str("\nTest successful!");
	} else {
		usart_send_str("\nTest failed. Expected (");
		usart_send_number(expected1, 16, 2);
		usart_send_str(", ");
		usart_send_number(expected2, 16, 2);

		usart_send_str("), but received (");
		usart_send_number(compare1, 16, 2);
		usart_send_str(", ");
		usart_send_number(compare2, 16, 2);
		usart_send_str(")");
	}
}

void assert3Values(int32_t expected1, int32_t expected2, int32_t expected3,
				   int32_t compare1, int32_t compare2, int32_t compare3) {
	if (expected1 == compare1 && expected2 == compare2 && expected3 == compare3) {
		usart_send_str("\nTest successful!");
	} else {
		usart_send_str("\nTest failed. Expected (");
		usart_send_number(expected1, 16, 2);
		usart_send_str(", ");
		usart_send_number(expected2, 16, 2);
		usart_send_str(", ");
		usart_send_number(expected3, 16, 2);

		usart_send_str("), but received (");
		usart_send_number(compare1, 16, 2);
		usart_send_str(", ");
		usart_send_number(compare2, 16, 2);
		usart_send_str(", ");
		usart_send_number(compare3, 16, 2);
		usart_send_str(")");
	}
}

void testMaster() {
	PORTA = 0x18; // Two middle LEDs on

	usart_send_str("\nStarting tests as master");

	usart_send_str("\n\nTest 1/7: Simple 1 byte reply test");
	send_TestMessage1_nowait(CANTEST_TEST1_VALUE);
	while(!getTestSignal()); // Wait for the reply
	assertValue(CANTEST_TEST1_VALUE, getTestSignal());

	usart_send_str("\n\nTest 2/7: Multiple signals and endianness test");
	//The 0x666 is a dummy value that is not used by the other board
	send_TestMessage2C_nowait(CANTEST_TEST2A_VALUE, 0x0666, CANTEST_TEST2B_VALUE, CANTEST_TEST2C_VALUE);
	while(!getTestSignalA2() && !getTestSignalB2() && !getTestSignalC2()){ // Wait for the reply
		_delay_ms(100); // don't stay in critical sections all the time
	}
	assert3Values(CANTEST_TEST2A_VALUE, CANTEST_TEST2B_VALUE, CANTEST_TEST2C_VALUE,
				  getTestSignalA2(), getTestSignalB2(), getTestSignalC2());


	usart_send_str("\n\nTest 3/7: Multiple messages in one MOB");
	send_TestMessage3A_nowait(CANTEST_TEST3A_VALUE);
	send_TestMessage3B_nowait(CANTEST_TEST3B_VALUE);
	while(!getTestSignalA() && !getTestSignalB()){ // Wait for the reply
		_delay_ms(100); // don't stay in critical sections all the time
	}
	assert2Values(CANTEST_TEST3A_VALUE, CANTEST_TEST3B_VALUE,
				 getTestSignalA(), getTestSignalB());


	usart_send_str("\n\nTest 4/7: Using the general MOB transmitter");
	send_TestMessage4A_nowait(CANTEST_TEST4A_VALUE);
	send_TestMessage4B_nowait(CANTEST_TEST4B_VALUE);
	while(!getTestSignal4A() && !getTestSignal4B()); // Wait for the reply
	assert2Values(CANTEST_TEST4A_VALUE, CANTEST_TEST4B_VALUE,
				 getTestSignal4A(), getTestSignal4B());
}


// Receive Handlers for slave mode

void onReceive_TestMessage1() {
	if (!testmaster) {
		send_TestMessage1_nowait(getTestSignal());
	}
}

void onReceive_TestMessage2C() {
	if (!testmaster) {
		send_TestMessage2R_nowait(getTestSignalA1(), getTestSignalB1(), getTestSignalC1());
	}
}

void onReceive_TestMessage3A() {
	if (!testmaster) {
		send_TestMessage3A_nowait(getTestSignalA());
	}
}

void onReceive_TestMessage3B() {
	if (!testmaster) {
		send_TestMessage3B_nowait(getTestSignalB());
	}
}

void onReceive_TestMessage4A() {
	if (!testmaster) {
		send_TestMessage4A_nowait(getTestSignal4A());
	}
}

void onReceive_TestMessage4B() {
	if (!testmaster) {
		send_TestMessage4B_nowait(getTestSignal4B());
	}
}


TASK(Bla) {

	usart_send_str("\nTask Bla");

	TerminateTask();
}
