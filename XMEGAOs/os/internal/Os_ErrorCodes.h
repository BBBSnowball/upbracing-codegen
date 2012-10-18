/*
 * Os_ErrorCodes.h
 *
 *  Created on: Oct 19, 2012
 *      Author: benny
 */

#ifndef OS_ERRORCODES_H_
#define OS_ERRORCODES_H_

typedef enum {
	// no error has occurred
	// This value will never be passed to OS_error.
	OS_ERROR_NO_ERROR = 0,

	// error codes between OS_ERROR_FATAL_MIN and
	// OS_ERROR_FATAL_MAX are fatal
	// This value will never be passed to OS_error.
	OS_ERROR_FATAL_MIN = OS_ERROR_NO_ERROR,

	// fatal error codes go here

	// Overflow of semaphore waiting queue
	OS_ERROR_SEM_QUEUE_FULL,

	// error codes between OS_ERROR_FATAL_MIN and
	// OS_ERROR_FATAL_MAX are fatal
	// This value will never be passed to OS_error.
	OS_ERROR_FATAL_MAX,

	// error codes between OS_ERROR_NORMAL_MIN and
	// OS_ERROR_NORMAL_MAX are fatal
	// This value will never be passed to OS_error.
	OS_ERROR_NORMAL_MIN = OS_ERROR_FATAL_MAX,

	// normal error codes go here

	// The token must be ready, but it isn't.
	OS_ERROR_NOT_READY,

	// error codes between OS_ERROR_NORMAL_MIN and
	// OS_ERROR_NORMAL_MAX are fatal
	// This value will never be passed to OS_error.
	OS_ERROR_NORMAL_MAX,

	// error codes between OS_ERROR_FIXABLE_MIN and
	// OS_ERROR_FIXABLE_MAX are fatal
	// This value will never be passed to OS_error.
	OS_ERROR_FIXABLE_MIN = OS_ERROR_NORMAL_MAX,

	// fixable error codes go here

	// an invalid token has been passed to an
	// asynchronous semaphore operation
	OS_ERROR_SEM_INVALID_TOKEN,

	// error codes between OS_ERROR_FIXABLE_MIN and
	// OS_ERROR_FIXABLE_MAX are fatal
	// This value will never be passed to OS_error.
	OS_ERROR_FIXABLE_MAX,
} OS_ERROR_CODE;

#endif /* OS_ERRORCODES_H_ */
