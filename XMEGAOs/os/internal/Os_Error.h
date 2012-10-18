/*
 * Os_Error.h
 *
 *  Created on: Oct 19, 2012
 *      Author: benny
 */

#ifndef OS_ERROR_H_
#define OS_ERROR_H_

#include "Os_ErrorCodes.h"

// This function must be defined by the application. It should
// report the error. It may block or restart the application.
// If it returns, the OS will try to fix/circumvent the error
// or reset the processor.
// The OS will never call this directly. It must use
// OS_report_error.
void OS_error(OS_ERROR_CODE error);

// Report an error (of any severity). Used by the OS.
// It will disable interrupts before it calls OS_error.
inline static void OS_report_error(OS_ERROR_CODE error);

// Report a fatal error and reset the processor, if OS_error
// returns. Used by the OS.
inline static void OS_report_fatal(OS_ERROR_CODE error);

// Reset the processor as reaction to an error.
void OS_error_reset_processor(void);



//// implementation

// Those functions are short 'inline static' functions for performance reasons.
// Therefore, they must be defined in the header file.

#include "internal/Os_Kernel.h"

inline static void OS_report_error(OS_ERROR_CODE error) {
	OS_ENTER_CRITICAL();

	OS_error(error);

	OS_EXIT_CRITICAL();
}

inline static void OS_report_fatal(OS_ERROR_CODE error) {
	cli();

	OS_error(error);

	OS_error_reset_processor();
}

#endif /* OS_ERROR_H_ */
