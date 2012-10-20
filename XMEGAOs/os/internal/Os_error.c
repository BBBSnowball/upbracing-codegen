/*
 * Os_error.c
 *
 * Created: 12-Oct-12 1:33:11 PM
 *  Author: Krishna
 */ 
#include "Os_error.h"

void OS_report_fatal( OS_ERROR_CODE error )
{
	OS_error(error);
	OS_error_reset_processor();
}

void OS_report_error ( OS_ERROR_CODE error )
{
	//Disable interrupts
	
	//error handling by application
	OS_error(error);
}

void OS_error_reset_processor( void )
{
	//reset the processor
}
