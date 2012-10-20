/*
 * Os_Error.c
 *
 *  Created on: Oct 19, 2012
 *      Author: benny
 */

#include "Os_Error.h"

#include <avr/wdt.h>

void OS_error_reset_processor(void) {
	// We have several options for resetting the processor:
	// - Jump to reset vector which is at address 0x0000. This doesn't
	//   reset the hardware, so we would have to clear the memory and
	//   probably further reset the state of some hardware. Some bits
	//   in IO registers must be cleared by writing a 1 into them (e.g.
	//   interrupt flags) or reading (e.g. ADCW).
	//   We could read, write 0xff and then 0x00, but still we might
	//   miss something and the order and timing matters, if we trigger
	//   actions (e.g. start ADC conversion -> ADCW gets set sometime
	//   later).
	// - use watchdog -> doesn't reset a few things - at least the
	//   reset source (obviously... *g*) and probably a few more (see
	//   errata?). It may stay active after the reset at the highest
	//   rate. This is a problem - must be handled by the application!
	//   (see <avr/wdt.h>)
	//   Nevertheless, IMHO this is the best solution.
	//   TODO In handle_bootloader_selectnode I use the wdt to reset
	//         the processor, but the register values are NOT reset to
	//         their default values!!!
	// - Use a GPIO that is connected to the reset pin. Obviously, this
	//   is the only way to do a real hard reset, but in general, we
	//   don't have the hardware to support this.
	//
	// Quote from <avr/wdt.h>
	// """
	// Note that for newer devices (ATmega88 and newer, effectively any
    // AVR that has the option to also generate interrupts), the watchdog
	// timer remains active even after a system reset (except a power-on
	// condition), using the fastest prescaler value (approximately 15
	// ms).  It is therefore required to turn off the watchdog early
	// during program startup [...]
	// """


	// implementation: jump to reset vector
	// In addition, we have to reset the processor state!
	// asm volatile("jmp 0xf000");


	// implementation: enable watchdog-timer and let it time out

	// disable interrupts to make sure that nobody can interrupt us
	cli();

	// enable watchdog timer with shortest duration
	wdt_enable(WDTO_15MS);

	// wait forever -> in practice: until reset
	while (1);
}
