/*
 * main.c
 *
 *  Created on: Mar 8, 2013
 *      Author: benny
 */

#include <avr/io.h>
#include <util/delay.h>

#include <common.h>

#include "gen/can.h"
#include "gen/pins.h"

// return true, iff the button has been pressed recently
// old_state: state variable for this button
// new_state: current state of the button
inline static bool pressed(bool* old_state, bool new_state) {
	// pressed now, but not before?
	bool result = !*old_state && new_state;

	// remember current state
	*old_state = new_state;

	return result;
}

int main_simple(void) {
	// turn on some LEDs
	LED_OUTPUT();
	SET_LED('B');

	// configure buttons pins as input with pullup
	INPUT(BUTTON_CENTER);
	INPUT(BUTTON_CENTER_ALT);
	INPUT(BUTTON_NORTH);
	INPUT(BUTTON_EAST);
	INPUT(BUTTON_WEST);
	INPUT(BUTTON_SOUTH);
	PULLUP(BUTTON_CENTER);
	PULLUP(BUTTON_CENTER_ALT);
	PULLUP(BUTTON_NORTH);
	PULLUP(BUTTON_EAST);
	PULLUP(BUTTON_WEST);
	PULLUP(BUTTON_SOUTH);

	// init CAN bus
	can_init_500kbps();
	can_init_mobs();
	sei();

	bool center_pressed, north_pressed, east_pressed,
		west_pressed, south_pressed;
	center_pressed = north_pressed = east_pressed = false;
	west_pressed = south_pressed = false;
	while (1) {
		if (pressed(&center_pressed, !IS_SET(BUTTON_CENTER)
				|| !IS_SET(BUTTON_CENTER_ALT))) {
			// blink LED3 a few times
			for (uint8_t i=0;i<5;i++) {
				HIGH(LED3);
				_delay_ms(100);
				LOW(LED3);
				_delay_ms(300);
			}
		}

		if (pressed(&west_pressed, !IS_SET(BUTTON_WEST))) {
			// accept the meeting
			TOGGLE(LED3);
			send_AcceptMeeting_wait(true);
			TOGGLE(LED3);
		}

		if (pressed(&east_pressed, !IS_SET(BUTTON_EAST))) {
			// decline the meeting
			TOGGLE(LED3);
			send_AcceptMeeting_wait(false);
			TOGGLE(LED3);
		}

		if (pressed(&south_pressed, !IS_SET(BUTTON_SOUTH))) {
			// cancel meeting
			TOGGLE(LED3);
			send_CancelMeeting_wait(0);
			TOGGLE(LED3);
		}

		if (pressed(&north_pressed, !IS_SET(BUTTON_NORTH))) {
			// request a meeting
			TOGGLE(LED3);
			send_RequestMeeting_wait(42);
			TOGGLE(LED3);
		}

		_delay_ms(100);
	}
}
