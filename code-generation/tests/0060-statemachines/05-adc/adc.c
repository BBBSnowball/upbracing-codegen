/*
 * adc.c
 *
 *  Created on: Jan 1, 2013
 *      Author: benny
 */

#include <avr/interrupt.h>
#include "gen/statemachines.h"
#include "adc.h"

// ADC interrupt handler

ISR(ADC_vect) {
	adc_sm_event_conversion_finished();
}
