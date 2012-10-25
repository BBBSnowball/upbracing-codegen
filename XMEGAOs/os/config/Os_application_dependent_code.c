/*
 * Os_application_dependent_code.c
 *
 * generated by script
 */

// This file contains code that should be part of the OS, but depends on the
// application configuration (e.g. count of TCBs). It must be compiled for
// each application.

// This file is generated by the shell script Os_application_dependent_code.c.sh
// DO NOT MODIFY IT !!!

// You can mark application specific code like this:


// will be compiled in Os_application_dependent_code.c:
#ifdef APPLICATION_DEPENDENT_CODE
// your includes (including the directory name!)
// your code
#endif	// end of APPLICATION_DEPENDENT_CODE


#include "config/Os_cfg_application.h"



// from file os/drivers/USART.c:

#include "IPC/queue.h"

#ifndef USART_TRANSMIT_QUEUE_LENGTH
#	warning USART_TRANSMIT_QUEUE_LENGTH not set, using default value of 10
#	define USART_TRANSMIT_QUEUE_LENGTH 10
#endif

// reserve waiting places for "writers" for all
// tasks except the USART transmitter
QUEUE(usart,USART_TRANSMIT_QUEUE_LENGTH,1,OS_NUMBER_OF_TCBS_DEFINE-1);


// from file os/config/Os_application_dependent_code.c:

// your includes (including the directory name!)
// your code


// from file os/internal/Os_Kernel.c:

#if OS_CFG_CC == BCC1 || OS_CFG_CC == ECC1
/* Simple priority "queue":
 * - Just an array of bools */
//QUESTION(Benjamin): Could we replace it by a bitfield?
//ANSWER(Peer): Yes. But are we that low on memory?
//              Isn't evaluating single bits quite time consuming?
uint8_t os_ready_queue[OS_NUMBER_OF_TCBS_DEFINE];
#elif OS_CFG_CC == BCC2 || OS_CFG_CC == ECC2
#	error Multiple activations for basic tasks, multiple tasks per priority
#endif
