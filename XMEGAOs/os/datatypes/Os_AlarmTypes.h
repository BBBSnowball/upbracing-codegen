/*
 * Os_AlarmTypes.h
 *
 * Created: 27.12.2011 20:53:33
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 


#ifndef OS_ALARMTYPES_H_
#define OS_ALARMTYPES_H_

#include "Platform_Types.h"

typedef uint16_t TickType;
typedef TickType * TickRefType;
#define TICK_TYPE_MAX 0xffff
typedef struct 
{
	volatile uint8_t taskid;
	volatile TickType tick;
	volatile TickType max;
} Os_Alarm;
typedef uint8_t AlarmType;

// use this macro to set tick, if you need to specify the phase of your alarm
// The alarm will fire at max, so we have to start below it for as many
// ticks as we want to (our phase). The smallest phase we can achieve is one below
// max. In that case, the alarm will fire at the next tick.
// This works for phases that are larger than max because tick will wrap
// around both in the phase calculation and while the OS is running. The maximum
// phase is TICK_TYPE_MAX.
#define ALARM_PHASE(phase, max) \
	(TickType) ((max) - 1 - (phase))

#endif /* OS_ALARMTYPES_H_ */
