/*
 * Os_application_dependent_code.c
 *
 *  Created on: Jul 26, 2012
 *      Author: benny
 */

// This file contains code that should be part of the OS, but depends on the
// application configuration (e.g. count of TCBs). It must be compiled for
// each application.

#include "Os_cfg_application.h"

// You can mark application specific code like this:


// will be compiled in Os_application_dependent_code.c:
#ifdef APPLICATION_DEPENDENT_CODE
// your code
#endif	// end of APPLICATION_DEPENDENT_CODE

// This file can be generated with the shell script Os_application_dependent_code.c.sh


// from file os/config/Os_application_dependent_code.c:

// your code


// from file os/internal/OSEK_Kernel.c:

#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
/* Simple priority "queue":
 * - Just an array of bools */
//QUESTION(Benjamin): Could we replace it by a bitfield?
uint8_t os_ready_queue[OS_NUMBER_OF_TCBS_DEFINE];
#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
#	error Multiple activations for basic tasks, multiple tasks per priority
#endif
