/*
 * Os_Alarm.h
 *
 * Created: 27.12.2011 21:08:17
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 

#ifndef OS_ALARM_H_
#define OS_ALARM_H_

#include "datatypes/Os_AlarmTypes.h"

//////////////////////////////////////////////////////////////////////////
// Function:  RunAlarm                                                  //
// Returns:   Pointer to alarm struct                                   //
//////////////////////////////////////////////////////////////////////////
// Description:                                                         //
// Activates the task or callback function (disabled)                   //
// associated with this alarm.                                          //
//////////////////////////////////////////////////////////////////////////
void RunAlarm(Os_Alarm * alarm);

#endif /* OS_ALARM_H_ */
