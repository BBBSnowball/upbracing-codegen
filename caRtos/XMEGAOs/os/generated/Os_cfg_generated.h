/*
 * Os_Cfg.h
 *
 * Created: 27.12.2011 18:16:36
 *  Author: peer
 */ 


#ifndef OS_CFG_H_
#define OS_CFG_H_

#include "OSEK_StatusTypes.h"
#include "OSEK_AlarmTypes.h"
#include "OSEK_TaskTypes.h"

extern TASK(Task_Increment);
extern TASK(Task_Update);
extern TASK(Task_Idle);
extern TASK(Task_Shift);
extern ALARMCALLBACK(Alarm_Test);

extern volatile Os_Alarm os_alarms[];
extern volatile Os_Tcb os_tcbs[];

#define OS_CFG_CC BCC1

#define OS_NUMBER_OF_TCBS			4
#define OS_NUMBER_OF_ALARMS			3

#define OS_TIMER_PRESCALE			TIMER_PRESCALE_1_bm
#define OS_TIMER_COMPARE_VALUE		0x7D00
 
#endif /* OS_CFG_H_ */