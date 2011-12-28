/*
 * OSEK_Alarm.c
 *
 * Created: 27.12.2011 22:19:58
 *  Author: peer
 */ 

#include "OSEK_Alarm.h"

StatusType GetAlarmBase(AlarmType alarmId, AlarmBaseRefType info)
{
	if (alarmId > (OSEK_NUMBER_OF_ALARMS - 1))
	{
		return E_OS_ID;
	}
	
	*info = os_alarms[alarmId].basetype;
	
	return E_OK;
}

StatusType GetAlarm(AlarmType alarmId, TickRefType tick)
{
	if (alarmId > (OSEK_NUMBER_OF_ALARMS - 1))
	{
		return E_OS_ID;
	}
	
	Os_Alarm alarm = os_alarms[alarmId];
	
	if (!alarm.active)
	{
		return E_OS_NOFUNC;
	}
	
	*tick = os_counter % alarm.basetype.ticksperbase;
	return E_OK;	
}

StatusType SetRelAlarm(AlarmType alarmId, TickType increment, TickType cycle)
{
	TickType oldValue;
	
	if (alarmId > (OSEK_NUMBER_OF_ALARMS - 1))
	{
		return E_OS_ID;
	}
	
	Os_Alarm alarm = os_alarms[alarmId];	
	
	if (alarm.active)
	{
		return E_OS_STATE;
	}
	if (increment > alarm.basetype.maxallowedvalue)
	{
		return E_OS_VALUE;
	}
	if (cycle != 0 &&
	    (cycle < alarm.basetype.mincycle || cycle > alarm.basetype.maxallowedvalue))
	{
		return E_OS_VALUE;		
	}
	
	alarm.active = 1;
	alarm.basetype.ticksperbase = cycle;
	oldValue = os_counter;
	os_counter += increment;
	
	// If increment would lead to more than one alarm,
	// only one alarm will be triggered
	if ((oldValue % cycle) + increment >= alarm.basetype.ticksperbase)
	{
		if (alarm.active) 
		{
			RunAlarm(&alarm);
		}		
	}	
	
	return E_OK;
}

//StatusType SetAbsAlarm(AlarmType alarmId, TickType start, TickType cycle);
//StatusType CancelAlarm(AlarmType alarmId);

void RunAlarm(volatile Os_Alarm * alarm) __attribute__ ( (naked) );
void RunAlarm(volatile Os_Alarm * alarm) 
{
	// Decide, what to do...
	// Activate Task?
	if (alarm->tcb != NULL) 
	{
		ActivateTask(alarm->tcb->id);
	}
	// Run Callback function?
	else if (alarm->callback != NULL) 
	{
		alarm->callback();
	}
	
	asm volatile ("ret");	
}