#include <avr/io.h>
#include <util/delay.h>

#include <avr/interrupt.h>
#include <avr/wdt.h>

#include <rs232.h>
#include <rs232-helpers.h>

#include "main.h"

#include "can-helper-master.c.inc"

int main(void) {
	// init GPIOs
	DDRA = 0xff; // Set LED pins as output
	DDRE = 0x00; // Set button pins as input
	PORTE = 0xf4; // Enable pull ups for buttons

	PORTA = 0x01;

	// init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\r\nStarting CAN master.\r\n");

	usart_send_str("Initialize CAN with 500kbps.\r\n");
	can_init_500kbps();

	usart_send_str("Initialize CAN mobs.\r\n");
	can_helper_master_init();

	_delay_ms(100);

	usart_send_str("Ready.\r\n");

	// Interrupts are needed to receive can messages
	sei();

	while(1) {
		if (usart_recv_char_available()) {
			uint8_t c = usart_recv();
			if (!can_helper_master_handle_char(c)) {
				// we may use the char
			}
		}
	}
}

// CAN receive interrupt
ISR(SIG_CAN_INTERRUPT1) {
	if (CANSIT1==0 && CANSIT2==0)
		//TODO we HAVE to reset the interrupt reason!
		return;

	uint8_t saved_canpage = CANPAGE;

	if (can_caused_interrupt(MOB_RECEIVE_RELAY)) {
		CANPAGE = (MOB_RECEIVE_RELAY<<4);

		can_helper_master_receive_relay();
	}

    // reset INT reason
    CANSTMOB &= ~(1<<RXOK);
    // re-enable RX, reconfigure MOb IDE=1
    //CANCDMOB = (1<<CONMOB1) | (1<<IDE);
    CANCDMOB |= (1<<CONMOB1);

    // restore CANPAGE
    CANPAGE = saved_canpage;
}
