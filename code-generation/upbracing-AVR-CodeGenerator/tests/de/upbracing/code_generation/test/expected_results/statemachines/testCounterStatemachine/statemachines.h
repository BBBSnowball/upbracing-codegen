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


////////////////////////////////
///     event functions      ///
////////////////////////////////


// statemachine counter

void counter_event_ISR_INT0(void);
void counter_event_reset(void);
void counter_event_startstop_pressed(void);

// accumulated event functions

inline static void event_ISR_INT0() {
	counter_event_ISR_INT0();
}

inline static void event_reset() {
	counter_event_reset();
}

inline static void event_startstop_pressed() {
	counter_event_startstop_pressed();
}


#endif	// STATEMACHINES_H_
