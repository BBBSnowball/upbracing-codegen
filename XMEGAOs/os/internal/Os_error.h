/*
 * Os_error.h
 *
 * Created: 12-Oct-12 1:23:57 PM
 *  Author: Krishna
 */ 


#ifndef OS_ERROR_H_
#define OS_ERROR_H_



// error codes
typedef enum {
	
	// no error has occurred
	// This value will never be passed to OS_error .
	OS_ERROR_NO_ERROR = 0,
	
	// error codes between OS_ERROR_FATAL_MIN and
	// OS_ERROR_FATAL_MAX are fatal
	// This value will never be passed to OS_error .
	OS_ERROR_FATAL_MIN = OS_ERROR_NO_ERROR ,
	
	// fatal error codes go here
	
	// Overflow of semaphore waiting queue
	OS_ERROR_SEM_QUEUE_FULL ,
	// error codes between OS_ERROR_FATAL_MIN and
	// OS_ERROR_FATAL_MAX are fatal
	// This value will never be passed to OS_error .
	OS_ERROR_FATAL_MAX ,
	// error codes between OS_ERROR_NORMAL_MIN and
	// OS_ERROR_NORMAL_MAX are normal
	 // This value will never be passed to OS_error .
	 OS_ERROR_NORMAL_MIN = OS_ERROR_FATAL_MAX ,
	
	// normal error codes go here
	
	// error codes between OS_ERROR_NORMAL_MIN and
	// OS_ERROR_NORMAL_MAX are normal
	// This value will never be passed to OS_error .
	OS_ERROR_NORMAL_MAX ,
	
	// error codes between OS_ERROR_FIXABLE_MIN and
	// OS_ERROR_FIXABLE_MAX are fixable
	// This value will never be passed to OS_error .
	OS_ERROR_FIXABLE_MIN = OS_ERROR_NORMAL_MAX ,
	
	// fixable error codes go here
	
	// an invalid token has been passed to an
	// asynchronous semaphore operation
	OS_ERROR_SEM_INVALID_TOKEN ,
	
	// error codes between OS_ERROR_FIXABLE_MIN and
	// OS_ERROR_FIXABLE_MAX are fixable
	// This value will never be passed to OS_error .
	OS_ERROR_FIXABLE_MAX ,
} OS_ERROR_CODE ;

// This function must be defined by the aplication . It should
// report the error . It may block or restart the application .
// If it returns , the OS will try to fix / circumvent the error
// or reset the processor .
// The OS will never call this directly . It must use
// OS_report_error .
void OS_error ( OS_ERROR_CODE error );

// Report an error (of any severity ). Used by the OS.
// It will disable interrupts before it calls OS_error .
void OS_report_error ( OS_ERROR_CODE error );

// Report a fatal error and reset the processor , if OS_error
// returns . Used by the OS.
void OS_report_fatal ( OS_ERROR_CODE error );

// Reset the processor as reaction to an error .
void OS_error_reset_processor ( void );


#endif /* OS_ERROR_H_ */