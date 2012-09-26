/*
 * Os_Alarm.c
 *
 * Created: 27.12.2011 22:19:58
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 

#include "config/Os_config.h"
#include "Os_Alarm.h"
#include "Os_Task.h"

void RunAlarm(volatile Os_Alarm * alarm);
//QUESTION(Benjamin): We can we use a naked function here? It is called from normal C
//                    code. Therefore, it mustn't overwrite any register. How can we
//                    guarantee that here?
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
}
