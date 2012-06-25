/*
 * OSEK_AlarmTypes.h
 *
 * Created: 27.12.2011 20:53:33
 *  Author: peer
 */ 


#ifndef OSEK_ALARMTYPES_H_
#define OSEK_ALARMTYPES_H_

#include "Platform_Types.h"

#define ALARMCALLBACK(id) void id(void)

typedef uint16_t TickType;
typedef TickType * TickRefType;
typedef struct 
{
	volatile uint8_t taskid;
	volatile TickType tick;
	volatile TickType ticksperbase;
} Os_Alarm;
typedef uint8_t AlarmType;

#endif /* OSEK_ALARMTYPES_H_ */