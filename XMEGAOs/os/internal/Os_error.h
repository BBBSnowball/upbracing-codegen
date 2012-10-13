/*
 * Os_error.h
 *
 * Created: 12-Oct-12 1:23:57 PM
 *  Author: Krishna
 */ 


#ifndef OS_ERROR_H_
#define OS_ERROR_H_

typedef enum {
	INVALID_TOKEN,
	QUEUE_FULL,
	
	}ERROR_CODES;

extern void OS_ERROR(ERROR_CODES error);

void OS_FATAL_ERROR(ERROR_CODES error);



#endif /* OS_ERROR_H_ */