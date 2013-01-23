#include <avr/io.h>
#include <util/delay.h>

#include <avr/pgmspace.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>

#include <string.h>

#include <rs232.h>
#include <rs232-helpers.h>

#include <can-helper.h>

#include "main.h"

instruction_t saved_instruction;

typedef struct {
	uint32_t id;
	uint8_t dlc;
	uint8_t data[8];
} can_message_t;

#define queue_size 64
volatile static can_message_t message_queue[queue_size];
volatile static uint8_t queue_write = 0, queue_read = 0;
volatile static uint8_t queue_items = 0;
volatile static uint32_t lost_messages = 0;

static void can_init_mobs(void) {
	// MOB_RECEIVE

	for (uint8_t i=0;i<MOB_RECEIVE_COUNT;i++) {
		// select MOb page
		CANPAGE = ((MOB_RECEIVE+i)<<4);

		// receive all IDs
		// -> mask of all 0 (ignore bit)
		CANIDM4 = CANIDM3 = CANIDM2 = CANIDM1 = 0;

		//configure message as receive-msg (see CANCDMOB register, page257)
		CANCDMOB = (1<<CONMOB1);

		// enable interrupts for this MOb
		can_mob_enable(MOB_RECEIVE+i);
		can_mob_enable_interrupt(MOB_RECEIVE+i);
	}


	// MOB_RELAY

	can_mob_init_transmit2(MOB_RELAY, CAN_RELAY_ID & CAN_ID_MASK, !!(CAN_RELAY_ID & CAN_ID_FLAG_EXTENDED));


	// MOB_SEND
	// initialized when sending a message
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

			can_mob_wait_for_transmission_of_mob(MOB_SEND);

			if (!rtr)
				can_mob_init_transmit2(MOB_SEND, id & CAN_ID_MASK, extended);
			else
				can_mob_init_transmit_rtr(MOB_SEND, id & CAN_ID_MASK, extended);

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
		can_mob_wait_for_transmission_of_mob(MOB_RELAY);
		//can_mob_init_transmit2(MOB_RELAY, CAN_RELAY_ID & CAN_ID_MASK, !!(CAN_RELAY_ID & CAN_ID_FLAG_EXTENDED));
		can_mob_set_data(MOB_RELAY, dlc, (uint8_t*)instruction);
		can_mob_transmit_nowait(MOB_RELAY);
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

	// wait some time because the master doesn't have a buffer
	_delay_ms(30);

	// send second message

	buffer.instruction = 0x01;
	memcpy(buffer.instruction1.data, data+2, 6);

	can_mob_set_data(MOB_RELAY, 8, (uint8_t*)&buffer);
	can_mob_transmit_wait(MOB_RELAY);

	// wait some time because the master doesn't have a buffer
	_delay_ms(50);

	PORTA &= ~0x20;
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
			usart_send_number((size_t)&(x->member), 10, 0); \
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

	while (1) {

		if (queue_items > 0) {
			uint8_t x = queue_read;
			uint32_t id = message_queue[x].id;

			//usart_send_str("begin "); usart_send_number(x, 10, 0); usart_send_str("\r\n");

			// handle the message
			if (id == CAN_INSTRUCTION_ID)
				handle_instruction(message_queue[x].dlc, (instruction_t*)message_queue[x].data);
			else if (id == CAN_RELAY_ID)
				// we shouldn't receive such messages...
				;
			else
				handle_incoming_message(message_queue[x].id, message_queue[x].dlc, (uint8_t*)message_queue[x].data);

			++x;
			if (x >= queue_size)
				queue_read = 0;
			else
				queue_read = x;

			cli();
			--queue_items;
			sei();

			//usart_send_str("end   "); usart_send_number(x, 10, 0); usart_send_str("\r\n");
		}

		if (lost_messages > 0) {
			instruction_t inst;
			inst.instruction = 0x04;
			cli();
			inst.instruction4.lost_messages = lost_messages;
			lost_messages = 0;
			sei();

			can_mob_wait_for_transmission_of_mob(MOB_RELAY);
			//can_mob_init_transmit2(MOB_RELAY, CAN_RELAY_ID & CAN_ID_MASK, !!(CAN_RELAY_ID & CAN_ID_FLAG_EXTENDED));
			can_mob_set_data(MOB_RELAY, 4, (uint8_t*)&inst);
			can_mob_transmit_nowait(MOB_RELAY);
		}
	}

	while(1) {
		// toggle LED7 to show we're still alive
		PORTA ^= 0x80;
		_delay_ms(500);
	}
}

// CAN receive interrupt
ISR(SIG_CAN_INTERRUPT1) {
	if (CANSIT1==0 && CANSIT2==0)
		//TODO we HAVE to reset the interrupt reason!
		return;

	uint8_t saved_canpage = CANPAGE;

	//usart_send('I');

	// The AT90CAN will always use the highest priority
	// MOb (which is the lowest one). If we re-enable the
	// MOb immediately, it would be really hard to figure
	// out the right order, so we wait until all of them
	// have been used. The exact algorithm is this:
	// - Process received messages, but don't re-arm the MObs.
	// - If the last MOb is full, re-arm all the other MObs.
	// - After handling the message in the last MOb, re-arm it.

	for (uint8_t i=0;i<MOB_RECEIVE_COUNT;i++) {
		if (can_caused_interrupt(MOB_RECEIVE+i)) {
			if (i == MOB_RECEIVE_COUNT-1) {
				// This is the last MOb -> enable all the others
				//usart_send('E');

				for (uint8_t j=0;j<MOB_RECEIVE_COUNT-1;j++) {
					// select
					CANPAGE = ((MOB_RECEIVE+j)<<4);

					// re-enable RX
					CANCDMOB |= (1<<CONMOB1);
				}
			}

			//usart_send('0' + i);

			// select current MOb
			CANPAGE = ((MOB_RECEIVE+i)<<4);

			if (queue_items < queue_size) {
				uint8_t x = queue_write;

				message_queue[x].id = can_mob_get_id(MOB_RECEIVE+i);
				message_queue[x].dlc = can_mob_get_data_length(MOB_RECEIVE+i);

				volatile uint8_t* tmp = message_queue[x].data;
				for (uint8_t i=0;i<8;i++)
					*(tmp++) = CANMSG;

				++x;
				if (x >= queue_size)
					queue_write = 0;
				else
					queue_write = x;

				//NOTE Interrupts are disabled.
				queue_items++;
			} else
				lost_messages++;

			// at this point we are ready to receive a new message

			// reset INT reason
			CANSTMOB &= ~(1<<RXOK);

			if (i == MOB_RECEIVE_COUNT-1) {
				// re-enable RX
				CANCDMOB |= (1<<CONMOB1);

				//usart_send('e');
			}

			//usart_send('0' + i);
		}
	}

    // restore CANPAGE
    CANPAGE = saved_canpage;
}
