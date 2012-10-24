/*
 * semaphore_normal.c
 */
#include "semaphore.h"
#include "internal/Os_Kernel.h"
#include "internal/Os_Error.h"
#include <avr/interrupt.h>


#define INCLUDED_FROM_SEMAPHORE_C
#define SEMAPHORE_MODE_NORMAL
#include "semaphore.c.inc"
