#ifndef STATEMACHINE_ADC_HELPERS_H
#define STATEMACHINE_ADC_HELPERS_H

// we need the bool type
#include <common.h>

#ifndef IS_ADC_ENABLED_DECLARED
#define IS_ADC_ENABLED_DECLARED
inline static bool is_adc_enabled(void) {
	return (ADCSRA & (1 << ADEN));
}
#endif

inline static void adc_disable(void) {
	ADCSRA &= ~(1 << ADEN);
}

inline static void start_adc(void) {
	ADCSRA |= (1 << ADSC);
}

#ifndef IS_ADC_RUNNING_DECLARED
#define IS_ADC_RUNNING_DECLARED
	inline static bool is_adc_running(void) {
		return (ADCSRA & (1 << ADSC));
	}
#endif

inline static uint16_t adc_read_and_reset_result(void) {
	return ADCW;
}

inline static uint8_t adc_active_channel(void) {
	return (ADMUX & 0x1f);
}

extern void on_adc_finished(uint8_t channel, uint16_t value);

#endif	// defined STATEMACHINE_ADC_HELPERS_H
