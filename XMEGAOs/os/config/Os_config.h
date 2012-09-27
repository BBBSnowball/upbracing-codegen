/*
 * Os_Cfg.h
 *
 * Created: 27.12.2011 18:16:36
 *  Author: peer
 */ 

// This file contains declarations for variables, constants and functions
// that must be defined in OS_cfg_application.c
// This header file is used to compile the OS library, but the variables
// themselves will not be part of the library. They are put into the application
// and the linker provides them to the OS.

#ifndef OS_CONFIG_H_
#define OS_CONFIG_H_

#include "Os_StatusTypes.h"
#include "Os_AlarmTypes.h"
#include "Os_TaskTypes.h"

#include "Os_cfg_features.h"

extern volatile Os_Alarm os_alarms[];
extern volatile Os_Tcb os_tcbs[];

extern const uint8_t OS_NUMBER_OF_TCBS;
extern const uint8_t OS_NUMBER_OF_ALARMS;

#endif /* OS_CONFIG_H_ */
