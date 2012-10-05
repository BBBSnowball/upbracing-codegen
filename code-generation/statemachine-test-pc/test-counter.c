/*
 * test-counter.c
 *
 *  Created on: Oct 5, 2012
 *      Author: benny
 */

#include <CUnit/Basic.h>

#include "fake-avr.h"
#include "gen/statemachines.h"

static int init_suite1(void) {
	DDRA = DDRB = DDRC = DDRD = DDRE = DDRF = 0;
	PORTA = PORTB = PORTC = PORTD = PORTE = PORTF = 0;
	PINA = PINB = PINC = PIND = PINE = PINF = 0;

	return 0;
}

static int clean_suite1(void) {
	return 0;
}

// code from statemachine.c -> we want to access some internal state

typedef enum {
	counter_stopped_state,
	counter_running_state,
} counter_state__state_t;

typedef struct counter_state {
	counter_state__state_t state;
	uint8_t states_running_wait_time;
} counter_state_var_t;

counter_state_var_t counter_state;

#define STATE counter_state.state


static void counter_test1(void) {
	int i;

	PORTA = 42;
	wdt_reset_token1++;
	counter_init();
	CU_ASSERT(PORTA == 0);
	CU_ASSERT(DDRB == 0xff);
	CU_ASSERT(wdt_reset_token1 == wdt_reset_token2);
	CU_ASSERT(wdt_reset_counter == 1);
	CU_ASSERT(PORTB == 1);
	CU_ASSERT(STATE == counter_stopped_state);


	wdt_reset_token1++;
	counter_tick();
	CU_ASSERT(PORTA == 0);
	CU_ASSERT(wdt_reset_token1 == wdt_reset_token2);
	CU_ASSERT(wdt_reset_counter == 2);
	CU_ASSERT(PORTB == 1);
	CU_ASSERT(STATE == counter_stopped_state);

	wdt_reset_token1++;
	counter_tick();
	CU_ASSERT(PORTA == 0);
	CU_ASSERT(wdt_reset_token1 == wdt_reset_token2);
	CU_ASSERT(wdt_reset_counter == 3);
	CU_ASSERT(PORTB == 1);
	CU_ASSERT(STATE == counter_stopped_state);

	wdt_reset_token1++;
	event_startstop_pressed();
	CU_ASSERT(PORTA == 0);
	CU_ASSERT(wdt_reset_token1 == wdt_reset_token2);
	CU_ASSERT(wdt_reset_counter == 4);
	CU_ASSERT(PORTB == 1);
	CU_ASSERT(DDRA == 0xff);
	CU_ASSERT(STATE == counter_running_state);

	PORTA = 42;
	event_reset();	// no effect because we are in state running
	CU_ASSERT(PORTA == 42);
	CU_ASSERT(STATE == counter_running_state);

	wdt_reset_token1++;
	event_startstop_pressed();
	CU_ASSERT(wdt_reset_token1 == wdt_reset_token2);
	CU_ASSERT(PORTB == 2);
	CU_ASSERT(DDRA == 0x00);
	CU_ASSERT(STATE == counter_stopped_state);

	PORTA = 42;
	event_reset();	// now reset works because we are in state stopped
	CU_ASSERT(PORTA == 0);
	CU_ASSERT(STATE == counter_stopped_state);
	CU_ASSERT(PORTB == 3);

	wdt_reset_token1++;
	event_startstop_pressed();
	CU_ASSERT(PORTA == 0);
	CU_ASSERT(wdt_reset_token1 == wdt_reset_token2);
	CU_ASSERT(PORTB == 3);
	CU_ASSERT(DDRA == 0xff);
	CU_ASSERT(STATE == counter_running_state);

	// we have just entered the running state
	// After 100ms (this is 100 ticks) a transition should fire and increment PORTA.
	CU_ASSERT(PORTA == 0);
	for (i=0;i<99;i++)
		counter_tick();
	// not yet
	CU_ASSERT(PORTA == 0);

	// ...but now
	counter_tick();
	CU_ASSERT(PORTA == 1);

	// after another 100 ticks it should be incremented again
	for (i=0;i<99;i++)
		counter_tick();
	CU_ASSERT(PORTA == 1);
	counter_tick();
	CU_ASSERT(PORTA == 2);

	// test whether state change resets the timer (it should)
	for (i=0;i<80;i++)
		counter_tick();
	CU_ASSERT(PORTA == 2);
	event_startstop_pressed();
	for (i=0;i<80;i++)
		counter_tick();
	event_startstop_pressed();
	for (i=0;i<80;i++)
		counter_tick();
	CU_ASSERT(PORTA == 2);

	// unrelated events do not reset the timer
	event_reset();
	for (i=0;i<20;i++)
		counter_tick();
	CU_ASSERT(PORTA == 3);
}

int counter_test_add_suites(void) {
	CU_pSuite pSuite = NULL;

	// add a suite to the registry
	pSuite = CU_add_suite("counter-statemachine", init_suite1, clean_suite1);
	if (NULL == pSuite) {
		return 0;
	}

	// add the tests to the suite
#define ADD_TEST(suite, name, test_fn) if (NULL == CU_add_test(suite, name, test_fn)) { \
			return 0; \
		}
	ADD_TEST(pSuite, "simple test of counter statemachine", counter_test1);

	// successful
	return 1;
}
