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
#include "gen/statemachines.h"

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

int main(void) {
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

	// initialize the statemachine
	bob_init();

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
			TOGGLE(LED3);
			event_accept();
			TOGGLE(LED3);
		}

		if (pressed(&east_pressed, !IS_SET(BUTTON_EAST))) {
			TOGGLE(LED3);
			event_decline();
			TOGGLE(LED3);
		}

		if (pressed(&south_pressed, !IS_SET(BUTTON_SOUTH))) {
			TOGGLE(LED3);
			event_cancel();
			TOGGLE(LED3);
		}

		if (pressed(&north_pressed, !IS_SET(BUTTON_NORTH))) {
			TOGGLE(LED3);
			event_request();
			TOGGLE(LED3);
		}

		// let the statemachine do its work
		bob_tick();

		// wait 100ms because that's what the statemachine expects
		_delay_ms(100);
	}
}
