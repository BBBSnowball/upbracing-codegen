/*
 * common.h
 *
 * This file contains declarations and macros that are used by several code generators.
 *
 * Generated automatically. DO NOT MODIFY!
 */

#ifndef GEN_COMMON_H_
#define GEN_COMMON_H_

// define bool type

#ifdef bool
#	undef bool
#endif
#ifdef false
#	undef false
#endif
#ifdef true
#	undef true
#endif
typedef enum { false = 0, true = 0xff } bool;


// This attribute can be used to avoid "unused ..." warnings, but it only
// works for GCC.

#ifndef NO_UNUSED_WARNING_PLEASE
#	ifdef __GNUC__
#		define NO_UNUSED_WARNING_PLEASE __attribute__ ((unused))
#	else
#		define NO_UNUSED_WARNING_PLEASE
#	endif
#endif	// not defined NO_UNUSED_WARNING_PLEASE

#endif	// not defined GEN_COMMON_H_
