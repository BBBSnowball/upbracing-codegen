/*
 * statemachines.c
 *
 * This file defines all statemachines.
 *
 * Generated automatically. DO NOT MODIFY! Change config.rb instead.
 */

#include "statemachines.h"

//////////////////////////////////////////////////
//            code for statemachines            //
//////////////////////////////////////////////////


// code from global code boxes in statemachine counter

#include <avr/io.h>
#include <avr/wdt.h>

//////////////////////////////////////////////////
//               lock definitions               //
//////////////////////////////////////////////////


// counter: no locking
// Use locks, if you call any method of this statemachine; or make
// sure they are only called from one thread (no interrupts!).
#define counter_enter_critical() /* empty */
#define counter_exit_critical() /* empty */


//////////////////////////////////////////////////
//////////////////////////////////////////////////
/////                                        /////
//                   counter                    //
/////                                        /////
//////////////////////////////////////////////////
//////////////////////////////////////////////////


//////////////////////////////////////////////////
//                     data                     //
//////////////////////////////////////////////////

typedef enum {
	counter_stopped_state,
	counter_running_state,
} counter_state__state_t;

typedef struct {
	uint8_t running__wait_time;
	counter_state__state_t state;
} counter_state_var_t;

static counter_state_var_t counter_state;

//////////////////////////////////////////////////
//               action functions               //
//////////////////////////////////////////////////


static void counter_running_exit() {
	DDRA = 0x00;
}

static void counter_running_during() {
	// increment time for wait(...)
	++counter_state.running__wait_time;
}

static void counter_running_always() {
	wdt_reset();
}

static void counter_running_enter() {
	DDRA = 0xff;

	// reset time for wait(...)
	counter_state.running__wait_time = 0;
}

static void counter_stopped_always() {
	wdt_reset();
}

static void counter_stopped_enter() {
	DDRB = 0xff;

	PORTB++;
}

//////////////////////////////////////////////////
//                init function                 //
//////////////////////////////////////////////////

void counter_init() {
	counter_enter_critical();

	counter_state.state = counter_stopped_state;
	PORTA = 0;
	counter_stopped_always();
	counter_stopped_enter();

	counter_exit_critical();
}

//////////////////////////////////////////////////
//                tick function                 //
//////////////////////////////////////////////////

void counter_tick() {
	counter_enter_critical();

	switch (counter_state.state) {

	case counter_running_state:
		counter_running_during();
		counter_running_always();

		if (counter_state.running__wait_time >= 100) {  // wait(100 ms)
			// running -> running
			counter_running_exit();
			PORTA++;
			counter_running_enter();
		} else if (PORTA >= 128) {
			// running -> stopped
			counter_running_exit();
			counter_state.state = counter_stopped_state;
			counter_stopped_always();
			counter_stopped_enter();
		}

		break;

	case counter_stopped_state:
		counter_stopped_always();

		break;

	}

	counter_exit_critical();
}

//////////////////////////////////////////////////
//               event functions                //
//////////////////////////////////////////////////

void counter_event_reset() {
	counter_enter_critical();

	switch (counter_state.state) {

	case counter_running_state:
		break;

	case counter_stopped_state:
		if (1) {
			// stopped -> stopped
			PORTA = 0;
			counter_stopped_enter();
		}

		break;

	}

	counter_exit_critical();
}

void counter_event_startstop_pressed() {
	counter_enter_critical();

	switch (counter_state.state) {

	case counter_running_state:
		if (1) {
			// running -> stopped
			counter_running_exit();
			counter_state.state = counter_stopped_state;
			counter_stopped_always();
			counter_stopped_enter();
		}

		break;

	case counter_stopped_state:
		if (1) {
			// stopped -> running
			counter_state.state = counter_running_state;
			counter_running_always();
			counter_running_enter();
		}

		break;

	}

	counter_exit_critical();
}
