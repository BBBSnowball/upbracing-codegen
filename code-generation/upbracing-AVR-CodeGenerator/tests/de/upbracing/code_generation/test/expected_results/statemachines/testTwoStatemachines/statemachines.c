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

// simple_pc: no locking
// Use locks, if you call any method of this statemachine; or make
// sure they are only called from one thread (no interrupts!).
#define simple_pc_enter_critical() /* empty */
#define simple_pc_exit_critical() /* empty */


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


//////////////////////////////////////////////////
//////////////////////////////////////////////////
/////                                        /////
//                  simple_pc                   //
/////                                        /////
//////////////////////////////////////////////////
//////////////////////////////////////////////////


//////////////////////////////////////////////////
//                     data                     //
//////////////////////////////////////////////////

typedef enum {
	simple_pc_on_Browser_not_running_state,
	simple_pc_on_Browser_empty_state,
	simple_pc_on_Browser_google_state,
	simple_pc_on_Browser_home_state,
} simple_pc_state__on__Browser__state_t;
typedef enum {
	simple_pc_on_Desktop_not_running_state,
	simple_pc_on_Desktop_loading_state,
	simple_pc_on_Desktop_running_state,
} simple_pc_state__on__Desktop__state_t;
typedef enum {
	simple_pc_on_Kernel_booting_state,
	simple_pc_on_Kernel_oops_state,
	simple_pc_on_Kernel_running_state,
} simple_pc_state__on__Kernel__state_t;
typedef enum {
	simple_pc_off_state,
	simple_pc_on_state,
} simple_pc_state__state_t;
typedef enum {
	simple_pc_on_Desktop_running_null_Desktop1_state,
	simple_pc_on_Desktop_running_null_Desktop2_state,
} simple_pc_state__on__Desktop__running__null__state_t;

typedef struct {
	simple_pc_state__on__Browser__state_t on__Browser__state;
	simple_pc_state__on__Desktop__state_t on__Desktop__state;
	uint8_t on__Kernel__booting__wait_time;
	simple_pc_state__on__Kernel__state_t on__Kernel__state;
	simple_pc_state__state_t state;
	union {
		uint8_t loading__wait_time;
		simple_pc_state__on__Desktop__running__null__state_t running__null__state;
	} on__Desktop;
} simple_pc_state_var_t;

static simple_pc_state_var_t simple_pc_state;

//////////////////////////////////////////////////
//               action functions               //
//////////////////////////////////////////////////


static void simple_pc_on_Kernel_booting_during() {
	// increment time for wait(...)
	++simple_pc_state.on__Kernel__booting__wait_time;
}

static void simple_pc_on_Kernel_booting_enter() {
	// reset time for wait(...)
	simple_pc_state.on__Kernel__booting__wait_time = 0;
}

static void simple_pc_on_Desktop_loading_during() {
	// increment time for wait(...)
	++simple_pc_state.on__Desktop.loading__wait_time;
}

static void simple_pc_on_Desktop_loading_enter() {
	// reset time for wait(...)
	simple_pc_state.on__Desktop.loading__wait_time = 0;
}

//////////////////////////////////////////////////
//                init function                 //
//////////////////////////////////////////////////

void simple_pc_init() {
	simple_pc_enter_critical();

	simple_pc_state.state = simple_pc_off_state;

	simple_pc_exit_critical();
}

//////////////////////////////////////////////////
//                tick function                 //
//////////////////////////////////////////////////

void simple_pc_tick() {
	simple_pc_enter_critical();

	switch (simple_pc_state.state) {

	case simple_pc_off_state:
		break;

	case simple_pc_on_state:

		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				if (simple_pc_state.on__Kernel__booting__wait_time >= 1000) {  // wait(10 sec)
					// booting -> running
					simple_pc_state.on__Kernel__state = simple_pc_on_Kernel_running_state;
				}

				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				if (simple_pc_state.on__Desktop.loading__wait_time >= 150) {  // wait(1.5 sec)
					// loading -> running
					simple_pc_state.on__Desktop__state = simple_pc_on_Desktop_running_state;
					simple_pc_state.on__Desktop.running__null__state = simple_pc_on_Desktop_running_null_Desktop1_state;
				}

				break;

			case simple_pc_on_Desktop_not_running_state:
				if ($:Kernel is running) {
					// not_running -> loading
					simple_pc_state.on__Desktop__state = simple_pc_on_Desktop_loading_state;
				}

				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.on__Desktop__state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.on__Desktop.running__null__state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						break;

					}
				}
				break;

			}
		}
		break;

	}

	simple_pc_exit_critical();
}

//////////////////////////////////////////////////
//               event functions                //
//////////////////////////////////////////////////

void simple_pc_event_cosmic_ray() {
	simple_pc_enter_critical();

	switch (simple_pc_state.state) {

	case simple_pc_off_state:
		break;

	case simple_pc_on_state:

		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				if (1) {
					// running -> oops
					simple_pc_state.on__Kernel__state = simple_pc_on_Kernel_oops_state;
				}

				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.on__Desktop__state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.on__Desktop.running__null__state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						break;

					}
				}
				break;

			}
		}
		break;

	}

	simple_pc_exit_critical();
}

void simple_pc_event_home() {
	simple_pc_enter_critical();

	switch (simple_pc_state.state) {

	case simple_pc_off_state:
		break;

	case simple_pc_on_state:

		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				if (1) {
					// empty -> home
					simple_pc_state.on__Browser__state = simple_pc_on_Browser_home_state;
				}

				break;

			case simple_pc_on_Browser_google_state:
				if (1) {
					// google -> home
					simple_pc_state.on__Browser__state = simple_pc_on_Browser_home_state;
				}

				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.on__Desktop__state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.on__Desktop.running__null__state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						break;

					}
				}
				break;

			}
		}
		break;

	}

	simple_pc_exit_critical();
}

void simple_pc_event_open_search() {
	simple_pc_enter_critical();

	switch (simple_pc_state.state) {

	case simple_pc_off_state:
		break;

	case simple_pc_on_state:

		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				if (1) {
					// empty -> google
					simple_pc_state.on__Browser__state = simple_pc_on_Browser_google_state;
				}

				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.on__Desktop__state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.on__Desktop.running__null__state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						break;

					}
				}
				break;

			}
		}
		break;

	}

	simple_pc_exit_critical();
}

void simple_pc_event_open_tab() {
	simple_pc_enter_critical();

	switch (simple_pc_state.state) {

	case simple_pc_off_state:
		break;

	case simple_pc_on_state:

		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				if (1) {
					// home -> empty
					simple_pc_state.on__Browser__state = simple_pc_on_Browser_empty_state;
				}

				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.on__Desktop__state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.on__Desktop.running__null__state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						break;

					}
				}
				break;

			}
		}
		break;

	}

	simple_pc_exit_critical();
}

void simple_pc_event_start_browser() {
	simple_pc_enter_critical();

	switch (simple_pc_state.state) {

	case simple_pc_off_state:
		break;

	case simple_pc_on_state:

		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				if ($:Desktop is running) {
					// not_running -> home
					simple_pc_state.on__Browser__state = simple_pc_on_Browser_home_state;
				}

				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.on__Desktop__state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.on__Desktop.running__null__state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						break;

					}
				}
				break;

			}
		}
		break;

	}

	simple_pc_exit_critical();
}

void simple_pc_event_swich_desktop() {
	simple_pc_enter_critical();

	switch (simple_pc_state.state) {

	case simple_pc_off_state:
		break;

	case simple_pc_on_state:

		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.on__Desktop__state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.on__Desktop.running__null__state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						if (1) {
							// Desktop2 -> Desktop1
							simple_pc_state.on__Desktop.running__null__state = simple_pc_on_Desktop_running_null_Desktop1_state;
						}

						break;

					}
				}
				break;

			}
		}
		break;

	}

	simple_pc_exit_critical();
}

void simple_pc_event_switch_desktop() {
	simple_pc_enter_critical();

	switch (simple_pc_state.state) {

	case simple_pc_off_state:
		break;

	case simple_pc_on_state:

		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.on__Desktop__state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.on__Desktop.running__null__state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						if (1) {
							// Desktop1 -> Desktop2
							simple_pc_state.on__Desktop.running__null__state = simple_pc_on_Desktop_running_null_Desktop2_state;
						}

						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						break;

					}
				}
				break;

			}
		}
		break;

	}

	simple_pc_exit_critical();
}

void simple_pc_event_turn_off() {
	simple_pc_enter_critical();

	switch (simple_pc_state.state) {

	case simple_pc_off_state:
		break;

	case simple_pc_on_state:
		if (1) {
			// on -> off
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.on__Desktop.running__null__state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.on__Desktop.running__null__state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
			simple_pc_state.state = simple_pc_off_state;
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.on__Desktop.running__null__state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.on__Desktop.running__null__state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.on__Desktop.running__null__state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.on__Desktop.running__null__state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
		}


		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.on__Desktop__state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.on__Desktop.running__null__state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						break;

					}
				}
				break;

			}
		}
		break;

	}

	simple_pc_exit_critical();
}

void simple_pc_event_turn_on() {
	simple_pc_enter_critical();

	switch (simple_pc_state.state) {

	case simple_pc_off_state:
		if (1) {
			// off -> on
			simple_pc_state.state = simple_pc_on_state;
			simple_pc_state.on__Kernel__state = simple_pc_on_Kernel_booting_state;
			simple_pc_state.on__Browser__state = simple_pc_on_Browser_not_running_state;
			simple_pc_state.on__Desktop__state = simple_pc_on_Desktop_not_running_state;
		}

		break;

	case simple_pc_on_state:

		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.on__Kernel__state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.on__Browser__state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.on__Desktop__state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.on__Desktop__state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.on__Desktop.running__null__state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						break;

					}
				}
				break;

			}
		}
		break;

	}

	simple_pc_exit_critical();
}
