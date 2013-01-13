#ifndef MAIN_H_
#define MAIN_H_

typedef enum {
	MOB_RECEIVE_RELAY,
	MOB_SEND_INSTRUCTION
} MessageObjectID;

// We don't have a general transmitter MOB
#define CAN_WITHOUT_GENERAL_TRANSMITTER_MOB

#include <can_at90.h>

#include <can-helper.h>

#endif /* MAIN_H_ */
