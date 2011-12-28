/*
 * OSEK_AlarmTypes.h
 *
 * Created: 27.12.2011 20:53:33
 *  Author: peer
 */ 


#ifndef OSEK_ALARMTYPES_H_
#define OSEK_ALARMTYPES_H_

#include "OSEK_TaskTypes.h"
#include "Platform_Types.h"

#define ALARMCALLBACK(id) void id(void)

typedef uint16_t TickType;
typedef TickType * TickRefType;
typedef struct 
{
	volatile TickType maxallowedvalue;
	volatile TickType ticksperbase;
	volatile TickType mincycle;	// Optional
} AlarmBaseType;
typedef AlarmBaseType * AlarmBaseRefType;
typedef struct 
{
	volatile TickType tick;
	AlarmBaseType basetype;
	AlarmFunctionPointerType callback;
	uint8_t active;
	Os_Tcb *tcb;
} Os_Alarm;
typedef uint8_t AlarmType;

#endif /* OSEK_ALARMTYPES_H_ */