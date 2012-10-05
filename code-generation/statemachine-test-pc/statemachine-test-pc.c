/*
 ============================================================================
 Name        : statemachine-test-pc.c
 Author      : Benjamin
 Version     :
 Copyright   : 
 Description : Hello World in C, Ansi-style
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <CUnit/Basic.h>

#include "fake-avr.h"
#include "gen/statemachines.h"

int counter_test_add_suites(void);
int random_test_add_suites(void);

int main(void) {
	if (CUE_SUCCESS != CU_initialize_registry())
		return CU_get_error();

	// add test suites
#	define HANDLE_ERRORS(x) if (!(x)) { CU_cleanup_registry(); return CU_get_error(); }
	HANDLE_ERRORS( counter_test_add_suites() );
	HANDLE_ERRORS( random_test_add_suites()  );

	// run all tests using the CUnit Basic interface
	CU_basic_set_mode(CU_BRM_VERBOSE);
	CU_basic_run_tests();
	CU_cleanup_registry();
	return CU_get_error();
}
