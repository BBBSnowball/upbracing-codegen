/*
 * Os_Alarm.c
 *
 * Created: 27.12.2011 22:19:58
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 

#include "Os_Alarm.h"
#include "Os_Task.h"

void RunAlarm(Os_Alarm * alarm) 
{
	// Idle Task (ID 0) is always ready and
	// does not need to be activated
	if (alarm->taskid != 0) 
	{
		ActivateTask(alarm->taskid);
	}
	
	// Reset alarm ticks after run
	alarm->tick = 0;
	
	// Previous concept:
	// -> Alarms were able to call functions
	// -> Disabled for now
	//// Run Callback function?
	//else if (alarm->callback != NULL) 
	//{
		//alarm->callback();
	//}
}
