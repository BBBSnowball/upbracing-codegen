/*
 * Os_Cfg.h
 *
 * Created: 27.12.2011 18:16:36
 *  Author: peer
 */ 


#ifndef OS_CFG_H_
#define OS_CFG_H_

#include "../datatypes/OSEK_StatusTypes.h"
#include "../datatypes/OSEK_AlarmTypes.h"
#include "../datatypes/OSEK_TaskTypes.h"

extern TASK(Task_Increment);
extern TASK(Task_Update);
extern TASK(Task_Idle);
extern ALARMCALLBACK(Alarm_Test);

#define OSEK_CONFORMANCE_CLASS BCC1

#define OSEK_NUMBER_OF_TCBS 3
#define OSEK_NUMBER_OF_ALARMS 3

#endif /* OS_CFG_H_ */