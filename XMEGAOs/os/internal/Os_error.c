/*
 * Os_error.c
 *
 * Created: 12-Oct-12 1:33:11 PM
 *  Author: Krishna
 */ 
#include "Os_error.h"

void OS_FATAL_ERROR( ERROR_CODES error )
{
	OS_ERROR(error);
	while (1)
	{
	}
}