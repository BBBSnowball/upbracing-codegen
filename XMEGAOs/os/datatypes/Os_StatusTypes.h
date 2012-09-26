/*
 * OSEK_StatusTypes.h
 *
 * Created: 21.12.2011 18:12:02
 *  Author: Peer Adelt (adelt@mail.uni-paderborn.de)
 */ 


#ifndef OSEK_TYPES_H_
#define OSEK_TYPES_H_

#define BCC1 1
#define BCC2 2
#define ECC1 3
#define ECC2 4

#include "Platform_Types.h"

// OSEK_ERROR_TYPES: ErrorCodes (see OS223.pdf -> p.48)
typedef enum
{
	E_OK =	0,
	E_OS_ACCESS = 1,
	E_OS_CALLLEVEL = 2,
	E_OS_ID = 3,
	E_OS_LIMIT = 4,
	E_OS_NOFUNC = 5,
	E_OS_RESOURCE = 6,
	E_OS_STATE = 7,
	E_OS_VALUE = 8,
} StatusType;

#endif /* OSEK_TYPES_H_ */