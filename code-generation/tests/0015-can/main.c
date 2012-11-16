/*
 * main.c
 *
 *  Created on: Oct 09, 2012
 *      Author: sven
 */

#define CANTEST_VERSION 0x01
#define CANTEST_INITREQUEST 0x01
#define CANTEST_INITANSWER 0x02
#define CANTEST_TEST1_VALUE 0x55
#define CANTEST_TEST2A_VALUE 0x1337
#define CANTEST_TEST2B_VALUE 0x4242
#define CANTEST_TEST2C_VALUE 0x7fff
#define CANTEST_TEST3A_VALUE 0x3434
#define CANTEST_TEST3B_VALUE 0xf24b



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

BOOL testleader = FALSE;

void followTests();
void leadTests();

int main(void) {
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting CAN communication test program.");

	usart_send_str("\nInitialize CAN mobs.");
	can_init_mobs();

	_delay_ms(100);

	// Reset variables (are variables initialized as 0?)
	/*setTestVersion(0);
	setTestNumber(0);
	setTestSignal(0);
	setTestSignalA2(0);
	setTestSignalB2(0);
	setTestSignalC2(0);*/


	usart_send_str("\nSend init testphase request.");
	send_InitTestphase_nowait(CANTEST_VERSION, CANTEST_INITREQUEST);

	usart_send_str("\nWaiting for other board...");
	while(!getTestNumber()); // Wait for a respone

	if (getTestVersion() != CANTEST_VERSION) {
		usart_send_str("\nERROR: Other board reports wrong program version.");
		usart_send_str("\n       Reset to restart program.");
		while(1); // Do nothing
	}

	//We now either receive an initrequest or an initanswer, depending on which board is powered up first
	if (getTestNumber() == CANTEST_INITREQUEST) {
		send_InitTestphase_wait(CANTEST_VERSION, CANTEST_INITANSWER);
		testleader = FALSE;
		followTests();
	} else if (getTestNumber() == CANTEST_INITANSWER) {
		testleader = TRUE;
		leadTests();
	} else {
		usart_send_str("\nERROR: Received invalid test number.");
		usart_send_str("\n       Reset to restart program.");
		while(1); // Do nothing
	}

	// Start OS to test the task based sending
	StartOS();

	while(1);
}

void leadTests() {
	usart_send_str("\nStarting tests as test leader");

	usart_send_str("\n\nTest 1/7: Simple 1 byte reply test");
	send_TestMessage1_nowait(CANTEST_TEST1_VALUE);
	while(!getTestSignal()); // Wait for the reply
	if (getTestSignal() != CANTEST_TEST1_VALUE) {
		usart_send_str("\nTest 1/7: Failed. Expected ??, but received ??");
		while(1); // Do nothing
	}
	usart_send_str("\nTest 1/7: Success!");

	usart_send_str("\n\nTest 2/7: Multiple signals and endianness test");
	//The 0x666 is a dummy value that is not used by the other board
	send_TestMessage2C_nowait(CANTEST_TEST2A_VALUE, 0x0666, CANTEST_TEST2B_VALUE, CANTEST_TEST2C_VALUE);
	while(!getTestSignalA2() && !getTestSignalB2() && !getTestSignalC2()){ // Wait for the reply
		_delay_ms(100); // don't stay in critical sections all the time
	}
	if (getTestSignalA2() != CANTEST_TEST2A_VALUE ||
		getTestSignalB2() != CANTEST_TEST2B_VALUE ||
		getTestSignalC2() != CANTEST_TEST2C_VALUE) {
		usart_send_str("\nTest 2/7: Failed. Expected ??, but received ??");
		while(1); // Do nothing
	}
	usart_send_str("\nTest 2/7: Success!");


	usart_send_str("\n\nTest 3/7: Multiple messages in one MOB");
	send_TestMessage3A_nowait(CANTEST_TEST3A_VALUE);
	send_TestMessage3B_nowait(CANTEST_TEST3B_VALUE);
	while(!getTestSignalA() && !getTestSignalB()){ // Wait for the reply
		_delay_ms(100); // don't stay in critical sections all the time
	}
	if (getTestSignalA() != CANTEST_TEST3A_VALUE ||
		getTestSignalB() != CANTEST_TEST3B_VALUE) {
		usart_send_str("\nTest 3/7: Failed. Expected ??, but received ??");
		while(1); // Do nothing
	}
	usart_send_str("\nTest 3/7: Success!");


	usart_send_str("\n\nTest 4/7: Using the general MOB transmitter");
	send_TestMessage4A_nowait(CANTEST_TEST4A_VALUE);
	send_TestMessage4B_nowait(CANTEST_TEST4B_VALUE);
	while(!getTestSignal4A() && !getTestSignal4B()); // Wait for the reply
	if (getTestSignal4A() != CANTEST_TEST4A_VALUE ||
		getTestSignal4B() != CANTEST_TEST4B_VALUE) {
		usart_send_str("\nTest 4/7: Failed. Expected ??, but received ??");
		while(1); // Do nothing
	}
	usart_send_str("\nTest 4/7: Success!");


}

void followTests() {
	usart_send_str("\nStarting tests as test follower");
	usart_send_str("\nWARNING: This board does not output the test results.");
	usart_send_str("\n         To restart in the right order, reset board 2 and then board 1");


	usart_send_str("\n\nTest 1/7: Simple reply test");
	while(!getTestSignal()); // Wait for the test value
	usart_send_str("\nReceived: ?? - Send reply");
	send_TestMessage1_wait(getTestSignal());


	usart_send_str("\n\nTest 2/7: Multiple signals and endianness test");
	while(!getTestSignalA1() && !getTestSignalB1() && !getTestSignalC1()){ // Wait for the reply
		_delay_ms(100); // don't stay in critical sections all the time
	}
	usart_send_str("\nReceived: ?? - Send reply");
	send_TestMessage2R_wait(getTestSignalA1(), getTestSignalB1(), getTestSignalC1());


	usart_send_str("\n\nTest 3/7: Multiple messages in one MOB");
	while(!getTestSignalA() && !getTestSignalB()){ // Wait for the reply
		_delay_ms(100); // don't stay in critical sections all the time
	}
	usart_send_str("\nReceived: ?? - Send reply");
	send_TestMessage3A_wait(getTestSignalA());
	send_TestMessage3B_wait(getTestSignalB());


	usart_send_str("\n\nTest 4/7: Using the general MOB transmitter");
	while(!getTestSignal4A() && !getTestSignal4B()); // Wait for the reply
	usart_send_str("\nReceived: ?? - Send reply");
	send_TestMessage4A_wait(getTestSignal4A());
	send_TestMessage4B_wait(getTestSignal4B());

}


TASK(Bla) {

	usart_send_str("\nTask Bla");

	TerminateTask();
}
