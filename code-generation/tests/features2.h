/*
 * Os_cfg_features.h
 *
 * This file defines the features the the OS should have. Whenever you change
 * it, you must recompile the OS library. The library name should contain the
 * MD5 hash of this file.
 */

#ifndef OS_CFG_FEATURES_H_
#define OS_CFG_FEATURES_H_

#ifndef __AVR_AT90CAN128__
#	error Expecting AT90CAN128. Either update your project configuration / Makefile or the code generator configuration.
#endif

#define OS_CFG_CC BCC1

//TODO put as much as possible into program memory constants or
//     initialization functions in OS_cfg_generated.c
#define OS_TIMER_PRESCALE			TIMER_PRESCALE_64_bm
#define OS_TIMER_COMPARE_VALUE		12499

// category: drivers/usart

// enable USART driver
#define USART_ENABLE_DRIVER

// category: os/core

// conformance mode (task features)
#define OS_CFG_CC BCC1
#define OS_CFG_CC_BCC1
#undef  OS_CFG_CC_BCC2
#undef  OS_CFG_CC_ECC1
#undef  OS_CFG_CC_ECC2

#endif /* OS_CFG_FEATURES_H_ */
