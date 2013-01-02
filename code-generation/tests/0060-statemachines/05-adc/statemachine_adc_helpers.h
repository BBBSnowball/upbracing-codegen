#ifndef STATEMACHINE_ADC_HELPERS_H
#define STATEMACHINE_ADC_HELPERS_H

// we need the bool type
#include <common.h>

inline static bool is_adc_enabled(void) {
	return (ADCSRA & (1 << ADEN));
}

inline static void adc_disable(void) {
	ADCSRA &= ~(1 << ADEN);
}

inline static void start_adc(void) {
	ADCSRA |= (1 << ADSC);
}

inline static bool is_adc_running(void) {
	return (ADCSRA & (1 << ADSC));
}

inline static uint16_t adc_read_and_reset_result(void) {
	return ADCW;
}

inline static uint8_t adc_active_channel(void) {
	return (ADMUX & 0x1f);
}

extern void on_adc_finished(uint8_t channel, uint16_t value);

#endif	// defined STATEMACHINE_ADC_HELPERS_H
