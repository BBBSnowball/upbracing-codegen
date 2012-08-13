/*
 * Platform_Types.h
 *
 * Created: 27.12.2011 17:00:32
 *  Author: peer
 */ 


#ifndef PLATFORM_TYPES_H_
#define PLATFORM_TYPES_H_

#include <avr/io.h>

#define NULL ((void *)0)

// Platform dependent type definitions

// type of an item on the stack
// (whatever a stack pointer points to - not the pointer itself)
typedef uint8_t StackPointerType;
typedef uint8_t TaskPriorityType;
typedef void (*TaskFunctionPointerType)(void);
typedef void (*AlarmFunctionPointerType)(void);

#endif /* PLATFORM_TYPES_H_ */
