/*
 * OSEK_Alarm.h
 *
 * Created: 27.12.2011 21:08:17
 *  Author: peer
 */ 


#ifndef OSEK_ALARM_H_
#define OSEK_ALARM_H_

#include "OSEK_StatusTypes.h"
#include "OSEK_AlarmTypes.h"
#include "Os_cfg_generated.h"

//extern volatile Os_Alarm os_alarms[];
extern volatile uint16_t os_counter;

void RunAlarm(volatile Os_Alarm * alarm);


#endif /* OSEK_ALARM_H_ */