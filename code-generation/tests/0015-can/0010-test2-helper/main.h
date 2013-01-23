#ifndef MAIN_H_
#define MAIN_H_

typedef enum {
	MOB_RELAY = 0,
	MOB_SEND = 1,
	MOB_RECEIVE = 2,
} MessageObjectID;

//NOTE We can use up to (15-2) MObs, but
//     the MObs won't be re-enabled before
//     the last one is used. If this happens
//     the ISR must re-enable the MObs very
//     early. Therefore, we mustn't use too
//     many MObs for buffer because all of
//     them would be checked before enabling
//     them again.
#define MOB_RECEIVE_COUNT 4

// We don't have a general transmitter MOB
#define CAN_WITHOUT_GENERAL_TRANSMITTER_MOB

#include <can_at90.h>

#include "can-helper-config.h"

#endif /* MAIN_H_ */
