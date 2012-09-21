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


//////////////////////////////////////////////////
//               lock definitions               //
//////////////////////////////////////////////////


// simple_pc: no locking
// Use locks, if you call any method of this statemachine; or make
// sure they are only called from one thread (no interrupts!).
#define simple_pc_enter_critical() /* empty */
#define simple_pc_exit_critical() /* empty */


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
	simple_pc_off_state,
	simple_pc_on_state,
} simple_pc_state__state_t;
typedef enum {
	simple_pc_on_Desktop_not_running_state,
	simple_pc_on_Desktop_loading_state,
	simple_pc_on_Desktop_running_state,
} simple_pc_state__states__on__states_on_states__on_states_Desktop_state_t;
typedef enum {
	simple_pc_on_Browser_not_running_state,
	simple_pc_on_Browser_empty_state,
	simple_pc_on_Browser_google_state,
	simple_pc_on_Browser_home_state,
} simple_pc_state__states__on__states_on_states__on_states_Browser_state_t;
typedef enum {
	simple_pc_on_Kernel_booting_state,
	simple_pc_on_Kernel_oops_state,
	simple_pc_on_Kernel_running_state,
} simple_pc_state__states__on__states_on_states__on_states_Kernel_state_t;
typedef enum {
	simple_pc_on_Desktop_running_null_Desktop1_state,
	simple_pc_on_Desktop_running_null_Desktop2_state,
} simple_pc_state__states__on__states_on_states__Desktop__on_states_Desktop_states__running__running_states__running_states_null_state_t;

typedef struct {
	simple_pc_state__state_t state;
	struct {
		simple_pc_state__states__on__states_on_states__on_states_Desktop_state_t on_states_Desktop_state;
		simple_pc_state__states__on__states_on_states__on_states_Browser_state_t on_states_Browser_state;
		simple_pc_state__states__on__states_on_states__on_states_Kernel_state_t on_states_Kernel_state;
		union {
			uint8_t on_states_Kernel_states_booting_wait_time;
		} on_states_Kernel_states;
		union {
			uint8_t loading_wait_time;
			struct {
				simple_pc_state__states__on__states_on_states__Desktop__on_states_Desktop_states__running__running_states__running_states_null_state_t running_states_null_state;
			} running_states;
		} on_states_Desktop_states;
	} states_on_states;
} simple_pc_state_var_t;

static simple_pc_state_var_t simple_pc_state;

//////////////////////////////////////////////////
//               action functions               //
//////////////////////////////////////////////////


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
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				if (simple_pc_state.states.on.states_on_states.Kernel.on_states_Kernel_states.on_states_Kernel_states_booting_wait_time >= 1000) {  // wait(10 sec)
					// booting -> running
					simple_pc_state.states.on.states_on_states.on_states_Kernel_state = simple_pc_on_Kernel_running_state;
				}

				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				if (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.loading_wait_time >= 150) {  // wait(1.5 sec)
					// loading -> running
					simple_pc_state.states.on.states_on_states.on_states_Desktop_state = simple_pc_on_Desktop_running_state;
					simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state = simple_pc_on_Desktop_running_null_Desktop1_state;
				}

				break;

			case simple_pc_on_Desktop_not_running_state:
				if ($:Kernel is running) {
					// not_running -> loading
					simple_pc_state.states.on.states_on_states.on_states_Desktop_state = simple_pc_on_Desktop_loading_state;
				}

				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.states.on.states_on_states.on_states_Desktop_state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

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
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				if (1) {
					// running -> oops
					simple_pc_state.states.on.states_on_states.on_states_Kernel_state = simple_pc_on_Kernel_oops_state;
				}

				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.states.on.states_on_states.on_states_Desktop_state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

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
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				if (1) {
					// empty -> home
					simple_pc_state.states.on.states_on_states.on_states_Browser_state = simple_pc_on_Browser_home_state;
				}

				break;

			case simple_pc_on_Browser_google_state:
				if (1) {
					// google -> home
					simple_pc_state.states.on.states_on_states.on_states_Browser_state = simple_pc_on_Browser_home_state;
				}

				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.states.on.states_on_states.on_states_Desktop_state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

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
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				if (1) {
					// empty -> google
					simple_pc_state.states.on.states_on_states.on_states_Browser_state = simple_pc_on_Browser_google_state;
				}

				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.states.on.states_on_states.on_states_Desktop_state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

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
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				if (1) {
					// home -> empty
					simple_pc_state.states.on.states_on_states.on_states_Browser_state = simple_pc_on_Browser_empty_state;
				}

				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.states.on.states_on_states.on_states_Desktop_state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

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
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				if ($:Desktop is running) {
					// not_running -> home
					simple_pc_state.states.on.states_on_states.on_states_Browser_state = simple_pc_on_Browser_home_state;
				}

				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.states.on.states_on_states.on_states_Desktop_state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

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
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.states.on.states_on_states.on_states_Desktop_state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						break;

					case simple_pc_on_Desktop_running_null_Desktop2_state:
						if (1) {
							// Desktop2 -> Desktop1
							simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state = simple_pc_on_Desktop_running_null_Desktop1_state;
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
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.states.on.states_on_states.on_states_Desktop_state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

					case simple_pc_on_Desktop_running_null_Desktop1_state:
						if (1) {
							// Desktop1 -> Desktop2
							simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state = simple_pc_on_Desktop_running_null_Desktop2_state;
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
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
			simple_pc_state.state = simple_pc_off_state;
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

				case simple_pc_on_Desktop_running_null_Desktop1_state:
					break;

				case simple_pc_on_Desktop_running_null_Desktop2_state:
					break;

				}
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:
				switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

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
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.states.on.states_on_states.on_states_Desktop_state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

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
			simple_pc_state.states.on.states_on_states.on_states_Kernel_state = simple_pc_on_Kernel_booting_state;
			simple_pc_state.states.on.states_on_states.on_states_Browser_state = simple_pc_on_Browser_not_running_state;
			simple_pc_state.states.on.states_on_states.on_states_Desktop_state = simple_pc_on_Desktop_not_running_state;
		}

		break;

	case simple_pc_on_state:

		// execute transitions for the children, unless some transition has changed the state
		if (simple_pc_state.state == simple_pc_on_state) {
			switch (simple_pc_state.states.on.states_on_states.on_states_Kernel_state) {

			case simple_pc_on_Kernel_booting_state:
				break;

			case simple_pc_on_Kernel_oops_state:
				break;

			case simple_pc_on_Kernel_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Browser_state) {

			case simple_pc_on_Browser_empty_state:
				break;

			case simple_pc_on_Browser_google_state:
				break;

			case simple_pc_on_Browser_home_state:
				break;

			case simple_pc_on_Browser_not_running_state:
				break;

			}
			switch (simple_pc_state.states.on.states_on_states.on_states_Desktop_state) {

			case simple_pc_on_Desktop_loading_state:
				break;

			case simple_pc_on_Desktop_not_running_state:
				break;

			case simple_pc_on_Desktop_running_state:

				// execute transitions for the children, unless some transition has changed the state
				if (simple_pc_state.states.on.states_on_states.on_states_Desktop_state == simple_pc_on_Desktop_running_state) {
					switch (simple_pc_state.states.on.states_on_states.Desktop.on_states_Desktop_states.running.running_states.running_states_null_state) {

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
