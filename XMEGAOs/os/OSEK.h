/*
 * Os.h
 *
 * Created: 22.12.2011 20:54:57
 *  Author: peer
 */ 


#ifndef OS_H_
#define OS_H_

#include "generated/Os_cfg_generated.h"
#include "OSEK_Kernel.h"

#ifndef OSEK_CONFORMANCE_CLASS
#error No Conformance Class specified!
#endif

#if OSEK_CONFORMANCE_CLASS != BCC1 && OSEK_CONFORMANCE_CLASS != BCC2 && \
	OSEK_CONFORMANCE_CLASS != ECC1 && OSEK_CONFORMANCE_CLASS != ECC2
#error No valid Conformance Class specified
#endif

extern void StartOS(void);

#endif /* OS_H_ */