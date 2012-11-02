/*
 * Os_StatusTypes.h
 *
 * Created: 21.12.2011 18:12:02
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 


#ifndef OS_STATUSTYPES_H_
#define OS_STATUSTYPES_H_

#include "Platform_Types.h"

// Os_ERROR_TYPES: ErrorCodes (see OS223.pdf -> p.48)
typedef enum
{
	E_OK =	0,
	//E_OS_ACCESS = 1,
	//E_OS_CALLLEVEL = 2,
	//E_OS_ID = 3,
	//E_OS_LIMIT = 4,
	//E_OS_NOFUNC = 5,
	//E_OS_RESOURCE = 6,
	//E_OS_STATE = 7,
	//E_OS_VALUE = 8,
} StatusType;

#endif /* OS_STATUSTYPES_H_ */