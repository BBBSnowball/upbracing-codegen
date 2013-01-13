#ifndef CAN_SLAVE_H
#define CAN_SLAVE_H

#include <common.h>

typedef enum {
	MOB_RECEIVE_RELAY,
	MOB_SEND_INSTRUCTION
} MessageObjectID;

// We don't have a general transmitter MOB
#define CAN_WITHOUT_GENERAL_TRANSMITTER_MOB

#include <can_at90.h>

#include "can-slave-config.h"

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

#endif	// not defined CAN_SLAVE_H
