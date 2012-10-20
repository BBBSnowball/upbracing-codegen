/*
 * Os_Cfg.h
 *
 * Created: 27.12.2011 18:16:36
 *  Author: peer
 */ 


#ifndef OS_CFG_APPLICATION_H_
#define OS_CFG_APPLICATION_H_

#include "config/Os_config.h"

extern TASK(Task_Increment);
extern TASK(Task_Update);
extern TASK(Task_Idle);
extern TASK(Task_Shift);
extern ALARMCALLBACK(Alarm_Test);

#define OS_NUMBER_OF_TCBS_DEFINE   4
#define OS_NUMBER_OF_ALARMS_DEFINE 3

#endif /* OS_CFG_APPLICATION_H_ */
