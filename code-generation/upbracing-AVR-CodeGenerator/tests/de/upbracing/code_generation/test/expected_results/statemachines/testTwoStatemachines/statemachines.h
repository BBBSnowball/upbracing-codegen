/*
 * statemachines.h
 *
 * This file declares the public interface for all statemachines.
 *
 * Generated automatically. DO NOT MODIFY! Change config.rb instead.
 */

#ifndef STATEMACHINES_H_
#define STATEMACHINES_H_


////////////////////////////////
///  code for statemachines  ///
////////////////////////////////

////////////////////////////////
///  statemachine functions  ///
////////////////////////////////

void counter_init();
void counter_tick();


void simple_pc_init();
void simple_pc_tick();


////////////////////////////////
///     event functions      ///
////////////////////////////////


// statemachine counter

void counter_event_reset(void);
void counter_event_startstop_pressed(void);

// statemachine simple_pc

void simple_pc_event_cosmic_ray(void);
void simple_pc_event_home(void);
void simple_pc_event_open_search(void);
void simple_pc_event_open_tab(void);
void simple_pc_event_start_browser(void);
void simple_pc_event_swich_desktop(void);
void simple_pc_event_switch_desktop(void);
void simple_pc_event_turn_off(void);
void simple_pc_event_turn_on(void);

// accumulated event functions

inline static void event_cosmic_ray() {
	simple_pc_event_cosmic_ray();
}

inline static void event_home() {
	simple_pc_event_home();
}

inline static void event_open_search() {
	simple_pc_event_open_search();
}

inline static void event_open_tab() {
	simple_pc_event_open_tab();
}

inline static void event_reset() {
	counter_event_reset();
}

inline static void event_start_browser() {
	simple_pc_event_start_browser();
}

inline static void event_startstop_pressed() {
	counter_event_startstop_pressed();
}

inline static void event_swich_desktop() {
	simple_pc_event_swich_desktop();
}

inline static void event_switch_desktop() {
	simple_pc_event_switch_desktop();
}

inline static void event_turn_off() {
	simple_pc_event_turn_off();
}

inline static void event_turn_on() {
	simple_pc_event_turn_on();
}


#endif	// STATEMACHINES_H_
