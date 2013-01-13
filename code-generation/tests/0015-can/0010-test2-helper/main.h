#ifndef MAIN_H_
#define MAIN_H_

typedef enum {
	MOB_RECEIVE,
	MOB_RELAY,
	MOB_SEND
} MessageObjectID;

// We don't have a general transmitter MOB
#define CAN_WITHOUT_GENERAL_TRANSMITTER_MOB

#include <can_at90.h>

#include "can-helper-config.h"

#endif /* MAIN_H_ */
