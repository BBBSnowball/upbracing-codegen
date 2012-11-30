/*
 * main.c
 *
 *  Created on: Oct 09, 2012
 *      Author: sven
 */

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

#include "main.h"

int main(void) {
	//Init variables
	testmaster = FALSE;
	counter = 0;

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

	usart_send_str("\n\nTest 1/6: Simple 1 byte reply test");
	send_TestMessage1_nowait(CANTEST_TEST1_VALUE);
	while(!getTestSignal()); // Wait for the reply
	assertValue(CANTEST_TEST1_VALUE, getTestSignal());

	usart_send_str("\n\nTest 2/6: Using the general MOB transmitter");
	send_TestMessage2A_nowait(CANTEST_TEST2A_VALUE);
	send_TestMessage2B_nowait(CANTEST_TEST2B_VALUE);
	while(!getTestSignal2A() && !getTestSignal2B()); // Wait for the reply
	assert2Values(CANTEST_TEST2A_VALUE, CANTEST_TEST2B_VALUE,
				 getTestSignal2A(), getTestSignal2B());

	usart_send_str("\n\nTest 3/6: Multiple messages in one MOB");
	send_TestMessage3A_nowait(CANTEST_TEST3A_VALUE);
	send_TestMessage3B_nowait(CANTEST_TEST3B_VALUE);
	while(!getTestSignal3A() && !getTestSignal3B()){ // Wait for the reply
		_delay_ms(100); // don't stay in critical sections all the time
	}
	assert2Values(CANTEST_TEST3A_VALUE, CANTEST_TEST3B_VALUE,
				 getTestSignal3A(), getTestSignal3B());

	usart_send_str("\n\nTest 4/6: Multiple signals and endianness test");
	//The 0x666 is a dummy value that is not used by the other board
	send_TestMessage4C_nowait(CANTEST_TEST4A_VALUE, 0x0666, CANTEST_TEST4B_VALUE, CANTEST_TEST4C_VALUE);
	while(!getTestSignalA2() && !getTestSignalB2() && !getTestSignalC2()){ // Wait for the reply
		_delay_ms(100); // don't stay in critical sections all the time
	}
	assert3Values(CANTEST_TEST4A_VALUE, CANTEST_TEST4B_VALUE, CANTEST_TEST4C_VALUE,
				  getTestSignalA2(), getTestSignalB2(), getTestSignalC2());

	usart_send_str("\n\nTest 5/6: Testing sending and receiving partly without generated code");
	send_TestMessage5A_nowait(CANTEST_TEST5A_VALUE);
	while(!getTestSignal5B()){ // Wait for the reply
		_delay_ms(100); // don't stay in critical sections all the time
	}
	assertValue(CANTEST_TEST5B_VALUE, getTestSignal5B());
	// Now send message 5C manually and wait for the reply. (The receive handler also works manually)
	sendMessage5C();
	while(!getTestSignal5D()){ // Wait for the reply
		_delay_ms(100); // don't stay in critical sections all the time
	}
	assertValue(CANTEST_TEST5D_VALUE, getTestSignal5D());

	// Set the test signal to be send later by the periodic task
	usart_send_str("\n\nTest 6/6: Sending periodic messages with an OS task");
	setTestSignal6A(CANTEST_TEST6_VALUE);
}


// Receive methods for slave mode

void TestMessage1_onReceive() {
	if (!testmaster) {
		send_TestMessage1_nowait(getTestSignal());
	}
}

void TestMessage2A_onReceive() {
	if (!testmaster) {
		send_TestMessage2A_nowait(getTestSignal2A());
	}
}

void TestMessage2B_onReceive() {
	if (!testmaster) {
		send_TestMessage2B_nowait(getTestSignal2B());
	}
}

void TestMessage3A_onReceive() {
	if (!testmaster) {
		send_TestMessage3A_nowait(getTestSignal3A());
	}
}

void TestMessage3B_onReceive() {
	if (!testmaster) {
		send_TestMessage3B_nowait(getTestSignal3B());
	}
}

void TestMessage4C_onReceive() {
	if (!testmaster) {
		send_TestMessage4R_nowait(getTestSignalA1(), getTestSignalB1(), getTestSignalC1());
	}
}

void TestMessage5C_onReceive() {
	if (!testmaster) {
		// The slave checks the value and sends an invalid reply if the value is wrong
		if (getTestSignal5C() == CANTEST_TEST5C_VALUE) {
			send_TestMessage5D_nowait(CANTEST_TEST5D_VALUE);
		} else {
			send_TestMessage5D_nowait(0x00);
		}
	}
}

void TestMessage6A_onReceive() {
	if (!testmaster) {
		send_TestMessage6B_nowait(getTestSignal6A());
	}
}

void TestMessage6B_onReceive() {
	if (testmaster) { // This one is for the master
		if (getTestSignal6B() == CANTEST_TEST6_VALUE + counter) {
			usart_send_str("\nReceived periodic message ");
			usart_send_number(counter+1, 10, 1);
			usart_send_str("of 10");
			if (counter == 9) {
				usart_send_str("\nTest successful!");
			}

			counter++;
		} else {
			usart_send_str("\nTest failed. Expected ");
			usart_send_number(CANTEST_TEST6_VALUE, 16, 2);
			usart_send_str(", but received ");
			usart_send_number(getTestSignal6B(), 16, 2);
		}
	}
}


void sendMessage5B(BOOL error) {
	CANPAGE = (MOB_GENERAL_MESSAGE_TRANSMITTER<<4);
	can_mob_wait_for_transmission_of_current_mob();
	CANSTMOB = 0;
	can_mob_init_transmit2(MOB_GENERAL_MESSAGE_TRANSMITTER, CAN_TestMessage5D, CAN_TestMessage5D_IsExtended);
	CANCDMOB = (CANCDMOB&0x30) | ((2&0xf)<<DLC0);

	if (error) {
		// In case of an error simply send 0x0000
		CANMSG = 0x00;
		CANMSG = 0x00;
	} else {
		// Message 5B is "0" = big endian. (MSB first)
		CANMSG = (CANTEST_TEST5B_VALUE && 0xFF00) >> 8;
		CANMSG = CANTEST_TEST5B_VALUE && 0x00FF;
	}

	can_mob_transmit_nowait(MOB_GENERAL_MESSAGE_TRANSMITTER);
}

void sendMessage5C() {
	CANPAGE = (MOB_GENERAL_MESSAGE_TRANSMITTER<<4);
	can_mob_wait_for_transmission_of_current_mob();
	CANSTMOB = 0;
	can_mob_init_transmit2(MOB_GENERAL_MESSAGE_TRANSMITTER, CAN_TestMessage5C, CAN_TestMessage5C_IsExtended);
	CANCDMOB = (CANCDMOB&0x30) | ((2&0xf)<<DLC0);

	// Message 5C is "1" = little endian. (LSB first)
	CANMSG = CANTEST_TEST5C_VALUE && 0x00FF;
	CANMSG = (CANTEST_TEST5C_VALUE && 0xFF00) >> 8;

	can_mob_transmit_nowait(MOB_GENERAL_MESSAGE_TRANSMITTER);
}

// Receive handlers for manual (non code-generated) receive function
void TestMessage5A_receiveHandler() {
	uint8_t byte;
	// Message 5A is "1" = little endian. (LSB first)

	// First byte should be the least significant byte
	byte = CANMSG;
	if (byte == CANTEST_TEST5A_VALUE && 0x00FF) {

		// Second byte should be the most significant byte
		byte = CANMSG;
		if (byte == (CANTEST_TEST5A_VALUE && 0xFF00) >> 8) {

			// Test passed. Send message B back manually
			sendMessage5B(FALSE);
			return;
		}
	}

	// send empty message B as error
	sendMessage5B(TRUE);
}

void TestMessage5D_receiveHandler() {
	uint8_t byte;
	uint16_t result;
	// Message 5D is "0" = big endian. (MSB first)

	// First byte should be the most significant byte
	byte = CANMSG;
	result = byte;
	result <<= 8;

	// Second byte should be the least significant byte
	byte = CANMSG;
	result += byte;

	setTestSignal5D(result);
}
