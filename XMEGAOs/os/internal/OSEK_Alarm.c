/*
 * OSEK_Alarm.c
 *
 * Created: 27.12.2011 22:19:58
 *  Author: peer
 */ 

#include "Os_cfg_generated.h"
#include "OSEK_Alarm.h"

StatusType GetAlarmBase(AlarmType alarmId, AlarmBaseRefType info)
{
	if (alarmId > (OS_NUMBER_OF_ALARMS - 1))
	{
		return E_OS_ID;
	}
	
	*info = os_alarms[alarmId].basetype;
	
	return E_OK;
}

StatusType GetAlarm(AlarmType alarmId, TickRefType tick)
{
	if (alarmId > (OS_NUMBER_OF_ALARMS - 1))
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
	
	if (alarmId > (OS_NUMBER_OF_ALARMS - 1))
	{
		return E_OS_ID;
	}
	
	//QUESTION(Benjamin): Does that copy the struct? Could we use a pointer instead?
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
	//QUESTION(Benjamin): Why do you change the global counter here?
	os_counter += increment;
	
	// If increment would lead to more than one alarm,
	// only one alarm will be triggered
	//QUESTION(Benjamin): Is this behaviour a bug or a feature?
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
//QUESTION(Benjamin): Do we have to implement them? ^^

void RunAlarm(volatile Os_Alarm * alarm) __attribute__ ( (naked) );
//QUESTION(Benjamin): We can we use a naked function here? It is called from normal C
//                    code. Therefore, it mustn't overwrite any register. How can we
//                    guarantee that here?
void RunAlarm(volatile Os_Alarm * alarm) 
{
	// Decide, what to do...
	// Activate Task?
	if (alarm->tcb != NULL) 
	{
		ActivateTask(((Os_Tcb *)alarm->tcb)->id);
	}
	// Run Callback function?
	else if (alarm->callback != NULL) 
	{
		alarm->callback();
	}
	
	asm volatile ("ret");	
}
