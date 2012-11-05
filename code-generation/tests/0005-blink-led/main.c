#include <avr/io.h>
#include <util/delay.h>

int main(void) {
	// LEDs on PORTA: outputs, low
	DDRA = 0xff;
	PORTA = 0x00;

	// buttons on PE2 and PE4..7: inputs with pullup
	DDRE = 0x00;
	PORTE = 0xf4;

	uint8_t a = 0;
	uint8_t b = 0;
	while (1) {
		// read button state
		// We invert the value because the pin will
		// go down, if the button is pressed.
		uint8_t button_state = ~PINE;

		// center switch is connected to PE2, but
		// we want to show it on LED 3
		if (button_state & (1<<2)) {
			button_state |= (1<<3);
		} else {
			button_state &= ~(1<<3);
		}
		button_state &= 0xf8;

		// We don't want to sleep for a second
		// because that would slow down the
		// response time of button->led.
		++a;
		if (a >= 100) {
			a = 0;

			// 1 second has elapsed
			// -> increment counter for LEDs
			++b;
		}

		// set LEDs
		PORTA = button_state | (b & 0x07);

		// sleep for 10ms
		_delay_ms(10);
	}

	return 0;
}
