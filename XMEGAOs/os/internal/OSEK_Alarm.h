/*
 * OSEK_Alarm.h
 *
 * Created: 27.12.2011 21:08:17
 *  Author: peer
 */ 


#ifndef OSEK_ALARM_H_
#define OSEK_ALARM_H_

#include "datatypes/OSEK_StatusTypes.h"
#include "datatypes/OSEK_AlarmTypes.h"
#include "config/Os_config.h"

//extern volatile Os_Alarm os_alarms[];
extern volatile uint16_t os_counter;

void RunAlarm(volatile Os_Alarm * alarm);


#endif /* OSEK_ALARM_H_ */
