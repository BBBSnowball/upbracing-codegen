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

// tick frequency is 5.0
#if F_CPU != 8000000
#	error Wrong CPU frequency!
#endif
#define OS_TIMER_PRESCALE			TIMER_PRESCALE_64_bm
#define OS_TIMER_COMPARE_VALUE		24999

// category: drivers/usart

// enable USART driver
#define USART_ENABLE_DRIVER

#endif /* OS_CFG_FEATURES_H_ */
