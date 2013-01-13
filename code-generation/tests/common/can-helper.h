#ifndef CAN_HELPER_H
#define CAN_HELPER_H

//NOTE Before including that file, make sure that the
//     following requirements are fulfilled:
//     - MOB_RECEIVE_RELAY and MOB_SEND_INSTRUCTION are
//       defined (constant, enum label or macro).
//     - MOB_RECEIVE_RELAY is the number of a MOb that
//       isn't used by anyone else.
//     - MOB_SEND_INSTRUCTION will used to send messages,
//       so its settings might be changed at any time. It
//       is ok to share this with other sending, but make
//       sure to disable interrupts while you use it. This
//       library will wait for an ongoing transmission on
//       this MOb to finish before using it and it expects
//       you do the same. It is ok to use
//       MOB_GENERAL_TRANSMITTER.
//     - <can_at90.h> has been included.

#include <common.h>

#include "can-helper-config.h"

typedef struct {
	uint8_t instruction;
	union {
		struct {
			union {
				uint8_t  id_bytes[4];
				uint32_t id;
			};
			uint8_t dlc;
			uint8_t data[2];
		} instruction0;
		struct {
			uint8_t data[6];
			uint8_t dummy;
		} instruction1;
	};
} instruction_t;


// methods for the master

// They are static (for performance reasons), so make sure you include
// can-helper.c.inc in any file you use them in. Each of the method,
// must be used in exactly one file!

// NOTE We don't really declare them here to avoid warnings. You have
//      to include the implementation anyway.
#if 0

// Must be called after USART and CAN have been initialized, but before
// any of the other methods is called.
static void can_helper_master_init(void);

// Pass all chars received on the USART to this method. If it returns
// false, you are free to use that char yourself.
static bool can_helper_master_handle_char(uint8_t c);

// Call this method from the ISR(SIG_CAN_INTERRUPT1), if the interrupt
// is for MOB_RECEIVE_RELAY. You should use something along the lines of:
// if (can_caused_interrupt(MOB_RECEIVE_RELAY))
//   can_helper_master_receive_relay();
static void can_helper_master_receive_relay(void);

#endif	// not declared; only as documentation

#endif	// not defined CAN_HELPER_H
