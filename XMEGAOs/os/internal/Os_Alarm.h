/*
 * OSEK_Alarm.h
 *
 * Created: 27.12.2011 21:08:17
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 


#ifndef OSEK_ALARM_H_
#define OSEK_ALARM_H_

#include "Os_StatusTypes.h"
#include "Os_AlarmTypes.h"
#include "config/Os_config.h"

//extern volatile Os_Alarm os_alarms[];
extern volatile uint16_t os_counter;

void RunAlarm(volatile Os_Alarm * alarm);


#endif /* OSEK_ALARM_H_ */
