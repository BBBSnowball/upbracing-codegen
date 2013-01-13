#include <avr/io.h>
#include <util/delay.h>

#include <avr/pgmspace.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>

#include <string.h>

#include <rs232.h>
#include <rs232-helpers.h>

#include "main.h"


typedef struct {
	uint8_t instruction;
	union {
		struct {
			union {
				uint8_t  id_bytes[4];
				uint32_t id;
			};
			uint8_t dlc;
			uint8_t data[2];
		} instruction0;
		struct {
			uint8_t data[6];
			uint8_t dummy;
		} instruction1;
	};
} instruction_t;

instruction_t saved_instruction;

static void can_init_mobs(void) {
	// MOB_RECEIVE

	// select MOb page
	CANPAGE = (MOB_RECEIVE<<4);

	// receive all IDs
	// -> mask of all 0 (ignore bit)
	CANIDM4 = CANIDM3 = CANIDM2 = CANIDM1 = 0;

	//configure message as receive-msg (see CANCDMOB register, page257)
	CANCDMOB = (1<<CONMOB1);

	// enable interrupts for this MOb
	can_mob_enable(MOB_RECEIVE);
	can_mob_enable_interrupt(MOB_RECEIVE);


	// MOB_RELAY

	can_mob_init_transmit2(MOB_RELAY, CAN_RELAY_ID & CAN_ID_MASK, !!(CAN_RELAY_ID & CAN_ID_FLAG_EXTENDED));


	// MOB_SEND
	// initialized when sending a message
}

int main(void) {
	// init GPIOs
	DDRA = 0xff; // Set LED pins as output
	DDRE = 0x00; // Set button pins as input
	PORTE = 0xf4; // Enable pull ups for buttons

	PORTA = 0x01;

	// init usart
	usart_init();
	PORTA = 0x02;
	usart_send_str("\r\nStarting CAN helper.\r\n");

	if (sizeof(instruction_t) != 8) {
		// This is a problem...
		usart_send_str("ERROR: The type instruction_t should be exactly 8 byte to fit into a CAN message.\r\n");
		usart_send_str("  sizeof(instruction_t) = "); usart_send_number(sizeof(instruction_t), 10, 0); usart_send_str("\r\n");
		instruction_t* x = (instruction_t*)0;
#define SEND_ADDR_OF(member) usart_send_str("  " #member ": "); \
			usart_send_number((uint32_t)&(x->member), 10, 0); \
			usart_send_str("\r\n");
		SEND_ADDR_OF(instruction);
		SEND_ADDR_OF(instruction0);
		SEND_ADDR_OF(instruction1);
		SEND_ADDR_OF(instruction0.dlc);
	}

	usart_send_str("Initialize CAN with 500kbps.\r\n");
	can_init_500kbps();

	usart_send_str("Initialize CAN mobs.\r\n");
	can_init_mobs();

	_delay_ms(100);

	usart_send_str("Ready.\r\n");

	// Interrupts are needed to receive can messages
	sei();

	while(1) {
		// toggle LED7 to show we're still alive
		PORTA ^= 0x80;
		_delay_ms(500);
	}
}

static void handle_instruction(uint8_t dlc, instruction_t* instruction) {
	PORTA |= 0x40;

	switch (instruction->instruction) {
	case 0x00:	// first half of message to send
		// copy data, so we can use it, when we receive the next instruction
		PORTA |= 0x10;
		memcpy(&saved_instruction, instruction, sizeof(saved_instruction));
		break;
	case 0x01:	// second half of message to send
		{
			// send a message
			uint32_t id = saved_instruction.instruction0.id;
			bool extended = !!(id & CAN_ID_FLAG_EXTENDED);
			bool rtr      = !!(id & CAN_ID_FLAG_RTR);

			//TODO implement RTR
			can_mob_init_transmit2(MOB_SEND, id & CAN_ID_MASK, extended);

			uint8_t data[8];
			memcpy(data, saved_instruction.instruction0.data, 2);
			memcpy(data+2, instruction->instruction1.data, 6);

			can_mob_set_data(MOB_SEND, saved_instruction.instruction0.dlc, data);

			can_mob_transmit_nowait(MOB_SEND);

			PORTA &= ~0x10;
		}
		break;
	case 0x02:	// ping
		// answer with pong
		can_mob_set_data(MOB_RELAY, dlc, (uint8_t*)instruction);
		can_mob_transmit_wait(MOB_RELAY);
		break;
	default:
		usart_send_str("WARN: I have received an invalid instruction code.\r\n");
		break;
	}

	PORTA &= ~0x40;
}

static void handle_incoming_message(uint32_t id, uint8_t dlc, uint8_t* data) {
	PORTA |= 0x20;

	//NOTE We use an instruction_t because relayed messages have the same
	//     structure as instruction 0x00 and 0x01.
	instruction_t buffer;

	// send first message

	buffer.instruction = 0x00;
	buffer.instruction0.id = id;
	buffer.instruction0.dlc = dlc;
	memcpy(buffer.instruction0.data, data, 2);

	can_mob_set_data(MOB_RELAY, 8, (uint8_t*)&buffer);
	can_mob_transmit_wait(MOB_RELAY);

	// send second message

	buffer.instruction = 0x01;
	memcpy(buffer.instruction1.data, data+2, 6);

	can_mob_set_data(MOB_RELAY, 8, (uint8_t*)&buffer);
	can_mob_transmit_nowait(MOB_RELAY);

	PORTA &= ~0x20;
}

// CAN receive interrupt
ISR(SIG_CAN_INTERRUPT1) {
	static uint8_t buffer[8];

	if (CANSIT1==0 && CANSIT2==0)
		//TODO we HAVE to reset the interrupt reason!
		return;

	uint8_t saved_canpage = CANPAGE;

	if (can_caused_interrupt(MOB_RECEIVE)) {
		CANPAGE = (MOB_RECEIVE<<4);

		// get message data
		uint8_t* tmp = buffer;
		for (uint8_t i=0;i<8;i++)
			*(tmp++) = CANMSG;

		uint32_t id = can_mob_get_id(MOB_RECEIVE);
		uint8_t dlc = can_mob_get_data_length(MOB_RECEIVE);

		// at this point we are ready to receive a new message

	    // reset INT reason
	    CANSTMOB &= ~(1<<RXOK);
	    // re-enable RX, reconfigure MOb IDE=1
	    //CANCDMOB = (1<<CONMOB1) | (1<<IDE);
	    CANCDMOB |= (1<<CONMOB1);

	    // handle the message
		if (id == CAN_INSTRUCTION_ID)
			handle_instruction(dlc, (instruction_t*)buffer);
		else
			handle_incoming_message(id, dlc, buffer);
	}

    // reset INT reason
    CANSTMOB &= ~(1<<RXOK);
    // re-enable RX, reconfigure MOb IDE=1
    //CANCDMOB = (1<<CONMOB1) | (1<<IDE);
    CANCDMOB |= (1<<CONMOB1);

    // restore CANPAGE
    CANPAGE = saved_canpage;
}
