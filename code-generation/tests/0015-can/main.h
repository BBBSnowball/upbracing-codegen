/*
 * main.h
 *
 *  Created on: 29.11.2012
 *      Author: sven
 */

#ifndef MAIN_H_
#define MAIN_H_

#include "datatypes/Platform_Types.h"


#define CANTEST_VERSION 0x01
#define CANTEST_INIT_REQUEST 0x01
#define CANTEST_INIT_ACK 0x02
#define CANTEST_TEST1_VALUE 0x55
#define CANTEST_TEST2A_VALUE 0xa1
#define CANTEST_TEST2B_VALUE 0xbe
#define CANTEST_TEST3A_VALUE 0x3434
#define CANTEST_TEST3B_VALUE 0xf24b
#define CANTEST_TEST4A_VALUE 0x1337
#define CANTEST_TEST4B_VALUE 0x4242
#define CANTEST_TEST4C_VALUE 0x7fff
#define CANTEST_TEST5A_VALUE 0x55ff
#define CANTEST_TEST5B_VALUE 0xa1f9
#define CANTEST_TEST5C_VALUE 0x1f03
#define CANTEST_TEST5D_VALUE 0x48a6
#define CANTEST_TEST6_VALUE 0x69


BOOL testmaster;
int counter;

void modeSetup();

void assertValue(int32_t expected, int32_t compare);
void assert2Values(int32_t expected1, int32_t expected2, int32_t compare1, int32_t compare2);
void assert3Values(int32_t expected1, int32_t expected2, int32_t expected3,
				   int32_t compare1, int32_t compare2, int32_t compare3);

void testMaster();

void InitTestphase_onReceive();
void TestMessage1_onReceive();
void TestMessage2A_onReceive();
void TestMessage2B_onReceive();
void TestMessage3A_onReceive();
void TestMessage3B_onReceive();
void TestMessage4C_onReceive();
void TestMessage5C_onReceive();
void TestMessage6A_onReceive();
void TestMessage6B_onReceive();

void sendMessage5B(uint8_t error);
void sendMessage5C();
void TestMessage5A_receiveHandler();
void TestMessage5D_receiveHandler();


#endif /* MAIN_H_ */
