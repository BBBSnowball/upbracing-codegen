/*
 * can-slave-config.h
 *
 *  Created on: Jan 13, 2013
 *      Author: benny
 */

#ifndef CAN_SLAVE_CONFIG_H_
#define CAN_SLAVE_CONFIG_H_

// CAN ID to receive instructions
#define CAN_INSTRUCTION_ID (CAN_ID_FLAG_EXTENDED | 0x1371)

// CAN ID to relay messages to
#define CAN_RELAY_ID (CAN_ID_FLAG_EXTENDED | 0x1372)

#endif /* CAN_SLAVE_CONFIG_H_ */
