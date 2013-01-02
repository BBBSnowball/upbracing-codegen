/*
 * adc.h
 *
 *  Created on: Jan 1, 2013
 *      Author: benny
 */

#ifndef ADC_H_
#define ADC_H_

#include "gen/statemachines.h"

inline static void adc_start(uint8_t channel) {
	// select channel and internal reference (2.56V)
	ADMUX = (channel & 0x1f)  |  (1 << REFS1) | (1 << REFS0);

	// enable ADC
	// prescaler: 8Mhz / 64 = 125kHz
	// also clear interrupt flag and enable ADC interrupt
	ADCSRA = (1 << ADEN) | (1 << ADPS2) | (1 << ADPS1) | (0 << ADPS0)
			| (1 << ADIF) | (1 << ADIE);

	// tell statemachine to start a conversion
	adc_sm_event_start();
}

inline static void adc_restart(void) {
	// tell statemachine to start a conversion
	// (will run on the same channel as last time)
	adc_sm_event_start();
}

inline static void adc_disable(void) {
	adc_sm_event_adc_disable();
}

// implement this function - it will be called, whenever a conversion is finished
extern void on_adc_finished(uint8_t channel, uint16_t value);

#endif /* ADC_H_ */
