/*
 * main.c
 *
 *  Created on: Dec 07, 2012
 *      Author: sven
 */

#include <avr/io.h>

#include "Os.h"

#include "gen/Os_cfg_application.h"
#include "rs232.h"
#include "rs232-helpers.h"

#define NUMTASKS 100

uint16_t c[NUMTASKS];

int main(void) {
	//Init variables
	for(int i=0; i<NUMTASKS; i++) c[i] = 0;

	DDRA = 0xff; // Set LED Pins as output
	PORTA = 0x01;

	// Init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\nStarting onehundred tasks test.\n");

	StartOS();

	while(1);
}

TASK(Monitor) {

	uint16_t sum = 0;

	usart_send_str("\nSum: ");
	OS_ENTER_CRITICAL();
	for (int i=0; i<NUMTASKS; i++) sum += c[i];
	usart_send_number(sum, 10, 3);
	usart_send_str("\n");

	for (int i=0; i<NUMTASKS; i++) {
		usart_send_str("Task ");
		usart_send_number(i, 10, 1);
		usart_send_str(": ");
		usart_send_number(c[i], 10, 3);
		usart_send_str("\n");
		c[i] = 0;
	}
	OS_EXIT_CRITICAL();

	TerminateTask();
}

void TaskCounter(uint8_t i) {
	c[i]++;
}


TASK(Task0)  { TaskCounter(0); TerminateTask();}
TASK(Task1)  { TaskCounter(1); TerminateTask();}
TASK(Task2)  { TaskCounter(2); TerminateTask();}
TASK(Task3)  { TaskCounter(3); TerminateTask();}
TASK(Task4)  { TaskCounter(4); TerminateTask();}
TASK(Task5)  { TaskCounter(5); TerminateTask();}
TASK(Task6)  { TaskCounter(6); TerminateTask();}
TASK(Task7)  { TaskCounter(7); TerminateTask();}
TASK(Task8)  { TaskCounter(8); TerminateTask();}
TASK(Task9)  { TaskCounter(9); TerminateTask();}
TASK(Task10) { TaskCounter(10); TerminateTask();}
TASK(Task11) { TaskCounter(11); TerminateTask();}
TASK(Task12) { TaskCounter(12); TerminateTask();}
TASK(Task13) { TaskCounter(13); TerminateTask();}
TASK(Task14) { TaskCounter(14); TerminateTask();}
TASK(Task15) { TaskCounter(15); TerminateTask();}
TASK(Task16) { TaskCounter(16); TerminateTask();}
TASK(Task17) { TaskCounter(17); TerminateTask();}
TASK(Task18) { TaskCounter(18); TerminateTask();}
TASK(Task19) { TaskCounter(19); TerminateTask();}
TASK(Task20) { TaskCounter(20); TerminateTask();}
TASK(Task21) { TaskCounter(21); TerminateTask();}
TASK(Task22) { TaskCounter(22); TerminateTask();}
TASK(Task23) { TaskCounter(23); TerminateTask();}
TASK(Task24) { TaskCounter(24); TerminateTask();}
TASK(Task25) { TaskCounter(25); TerminateTask();}
TASK(Task26) { TaskCounter(26); TerminateTask();}
TASK(Task27) { TaskCounter(27); TerminateTask();}
TASK(Task28) { TaskCounter(28); TerminateTask();}
TASK(Task29) { TaskCounter(29); TerminateTask();}
TASK(Task30) { TaskCounter(30); TerminateTask();}
TASK(Task31) { TaskCounter(31); TerminateTask();}
TASK(Task32) { TaskCounter(32); TerminateTask();}
TASK(Task33) { TaskCounter(33); TerminateTask();}
TASK(Task34) { TaskCounter(34); TerminateTask();}
TASK(Task35) { TaskCounter(35); TerminateTask();}
TASK(Task36) { TaskCounter(36); TerminateTask();}
TASK(Task37) { TaskCounter(37); TerminateTask();}
TASK(Task38) { TaskCounter(38); TerminateTask();}
TASK(Task39) { TaskCounter(39); TerminateTask();}
TASK(Task40) { TaskCounter(40); TerminateTask();}
TASK(Task41) { TaskCounter(41); TerminateTask();}
TASK(Task42) { TaskCounter(42); TerminateTask();}
TASK(Task43) { TaskCounter(43); TerminateTask();}
TASK(Task44) { TaskCounter(44); TerminateTask();}
TASK(Task45) { TaskCounter(45); TerminateTask();}
TASK(Task46) { TaskCounter(46); TerminateTask();}
TASK(Task47) { TaskCounter(47); TerminateTask();}
TASK(Task48) { TaskCounter(48); TerminateTask();}
TASK(Task49) { TaskCounter(49); TerminateTask();}
TASK(Task50) { TaskCounter(50); TerminateTask();}
TASK(Task51) { TaskCounter(51); TerminateTask();}
TASK(Task52) { TaskCounter(52); TerminateTask();}
TASK(Task53) { TaskCounter(53); TerminateTask();}
TASK(Task54) { TaskCounter(54); TerminateTask();}
TASK(Task55) { TaskCounter(55); TerminateTask();}
TASK(Task56) { TaskCounter(56); TerminateTask();}
TASK(Task57) { TaskCounter(57); TerminateTask();}
TASK(Task58) { TaskCounter(58); TerminateTask();}
TASK(Task59) { TaskCounter(59); TerminateTask();}
TASK(Task60) { TaskCounter(50); TerminateTask();}
TASK(Task61) { TaskCounter(61); TerminateTask();}
TASK(Task62) { TaskCounter(62); TerminateTask();}
TASK(Task63) { TaskCounter(63); TerminateTask();}
TASK(Task64) { TaskCounter(64); TerminateTask();}
TASK(Task65) { TaskCounter(65); TerminateTask();}
TASK(Task66) { TaskCounter(66); TerminateTask();}
TASK(Task67) { TaskCounter(67); TerminateTask();}
TASK(Task68) { TaskCounter(68); TerminateTask();}
TASK(Task69) { TaskCounter(69); TerminateTask();}
TASK(Task70) { TaskCounter(70); TerminateTask();}
TASK(Task71) { TaskCounter(71); TerminateTask();}
TASK(Task72) { TaskCounter(72); TerminateTask();}
TASK(Task73) { TaskCounter(73); TerminateTask();}
TASK(Task74) { TaskCounter(74); TerminateTask();}
TASK(Task75) { TaskCounter(75); TerminateTask();}
TASK(Task76) { TaskCounter(76); TerminateTask();}
TASK(Task77) { TaskCounter(77); TerminateTask();}
TASK(Task78) { TaskCounter(78); TerminateTask();}
TASK(Task79) { TaskCounter(79); TerminateTask();}
TASK(Task80) { TaskCounter(70); TerminateTask();}
TASK(Task81) { TaskCounter(81); TerminateTask();}
TASK(Task82) { TaskCounter(82); TerminateTask();}
TASK(Task83) { TaskCounter(83); TerminateTask();}
TASK(Task84) { TaskCounter(84); TerminateTask();}
TASK(Task85) { TaskCounter(85); TerminateTask();}
TASK(Task86) { TaskCounter(86); TerminateTask();}
TASK(Task87) { TaskCounter(87); TerminateTask();}
TASK(Task88) { TaskCounter(88); TerminateTask();}
TASK(Task89) { TaskCounter(89); TerminateTask();}
TASK(Task90) { TaskCounter(90); TerminateTask();}
TASK(Task91) { TaskCounter(91); TerminateTask();}
TASK(Task92) { TaskCounter(92); TerminateTask();}
TASK(Task93) { TaskCounter(93); TerminateTask();}
TASK(Task94) { TaskCounter(94); TerminateTask();}
TASK(Task95) { TaskCounter(95); TerminateTask();}
TASK(Task96) { TaskCounter(96); TerminateTask();}
TASK(Task97) { TaskCounter(97); TerminateTask();}
TASK(Task98) { TaskCounter(98); TerminateTask();}
TASK(Task99) { TaskCounter(99); TerminateTask();}
