/*
 * Os_cfg_application.h
 *
 * This file defines application specific stuff, e.g. the task
 * entry functions.
 *
 * Generated automatically. DO NOT MODIFY! Change config.rb instead.
 */

#ifndef OS_CFG_APPLICATION_H_
#define OS_CFG_APPLICATION_H_

#include "config/Os_config.h"

extern TASK(Idle);
extern TASK(Update);
extern TASK(Increment);
extern TASK(Shift);

#define TASK_ID_Idle 0
#define TASK_ID_Update 1
#define TASK_ID_Increment 2
#define TASK_ID_Shift 3

#define OS_NUMBER_OF_TCBS_DEFINE   4
#define OS_NUMBER_OF_ALARMS_DEFINE 3

// category: drivers/usart

// length of the usart transmit buffer
#define USART_TRANSMIT_QUEUE_LENGTH 10

#endif /* OS_CFG_APPLICATION_H_ */
