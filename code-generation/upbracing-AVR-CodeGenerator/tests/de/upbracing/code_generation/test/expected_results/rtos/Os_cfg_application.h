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

extern TASK(Task_Idle);
extern TASK(Task_Update);
extern TASK(Task_Increment);
extern TASK(Task_Shift);

#define TASK_ID_Idle 0
#define TASK_ID_Update 1
#define TASK_ID_Increment 2
#define TASK_ID_Shift 3

//TODO I don't see how one could set an alarm callback for an alarm. What is this supposed to do?
//extern ALARMCALLBACK(Alarm_Test);

#define OS_NUMBER_OF_TCBS_DEFINE   4
#define OS_NUMBER_OF_ALARMS_DEFINE 3

#endif /* OS_CFG_APPLICATION_H_ */
