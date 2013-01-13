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



// CAN ID to receive instructions
#define CAN_INSTRUCTION_ID (CAN_ID_FLAG_EXTENDED | 0x1371)

// CAN ID to relay messages to
#define CAN_RELAY_ID (CAN_ID_FLAG_EXTENDED | 0x1372)

#endif /* MAIN_H_ */
