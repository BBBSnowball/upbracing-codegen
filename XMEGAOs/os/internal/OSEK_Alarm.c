/*
 * OSEK_Alarm.c
 *
 * Created: 27.12.2011 22:19:58
 *  Author: peer
 */ 

#include "Os_cfg_generated.h"
#include "OSEK_Alarm.h"

void RunAlarm(volatile Os_Alarm * alarm) __attribute__ ( (naked) );
void RunAlarm(volatile Os_Alarm * alarm) 
{
	// Decide, what to do...
	// Activate Task?
	if (alarm->taskid != 0) 
	{
		ActivateTask(alarm->taskid);
	}
	//// Run Callback function?
	//else if (alarm->callback != NULL) 
	//{
		//alarm->callback();
	//}
	
	alarm->tick = 0;
	
	asm volatile ("ret");	
}