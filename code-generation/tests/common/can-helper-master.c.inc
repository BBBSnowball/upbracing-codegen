/*
 * can-helper-master.c
 *
 *  Created on: Jan 13, 2013
 *      Author: benny
 */

#include <avr/io.h>
#include <avr/pgmspace.h>
#include <string.h>

#include "rs232.h"
#include "rs232-helpers.h"

static void can_helper_master_init(void) {
	// make sure that instruction_t has the right size
	if (sizeof(instruction_t) != 8) {
		while (1) {
			usart_send_str_P(PSTR("ERROR: The type instruction_t should be exactly 8 byte to fit into a CAN message.\r\n"));
			usart_send_str_P(PSTR("  sizeof(instruction_t) = ")); usart_send_number(sizeof(instruction_t), 10, 0); usart_send_str_P(PSTR("\r\n"));
		}
	}

	// MOB_RECEIVE_RELAY

	// select MOb page
	CANPAGE = (MOB_RECEIVE_RELAY<<4);

	can_mob_init_receive2(MOB_RECEIVE_RELAY, CAN_RELAY_ID & CAN_ID_MASK, !!(CAN_RELAY_ID & CAN_ID_FLAG_EXTENDED));

	// enable interrupts for this MOb
	can_mob_enable(MOB_RECEIVE_RELAY);
	can_mob_enable_interrupt(MOB_RECEIVE_RELAY);

	// MOB_SEND_INSTRUCTION is initialized when sending a message, so it can
	// be shared (e.g. you can use MOB_GENERAL_TRANSMITTER).
}

static bool can_helper_master_handle_char(uint8_t c) {
	// T  00 00 00 00  8  00 ....
	static uint8_t buffer[26];
	static volatile uint8_t pos_in_line = 0;

	if (pos_in_line == 0xff) {
		if (c == '\r' || c == '\n')
			pos_in_line = 0;

		// not our line -> char is not for us
		return false;
	}

	if (pos_in_line == 0xfe) {
		// 0xfe means: ignore a '\n', if we get one
		// In any case, we are at the beginning of the next
		// line.
		pos_in_line = 0;

		if (c == '\n')
			return true;
	}

	if (pos_in_line == 0) {
		// first character

		if (c == '~') {
			// this is our line
			pos_in_line++;
			return true;
		} else {
			// not our line
			if (c == '\r' || c == '\n')
				pos_in_line = 0;
			else
				pos_in_line = 0xff;

			return false;
		}
	}

	if (c == '\r' || c == '\n') {
		// end of line -> send the message
		uint8_t msg_len = pos_in_line-1;

		// If it was a '\r', we ignore the next
		// '\n' (code for that is 0xfe). Otherwise,
		// we are at the start of the next line.
		pos_in_line = (c == '\r' ? 0xfe : 0);

		if (msg_len > 0) {
			bool extended, rtr, send_cmd;
			instruction_t inst;

			// disable interrupts, so nobody can mess with our MOB
			uint8_t sreg_backup = SREG;
			cli();

			// wait for any ongoing transmissions on our MOB
			can_mob_wait_for_transmission_of_current_mob(MOB_SEND_INSTRUCTION);

			// initialize for sending to slave board
			can_mob_init_transmit2(MOB_SEND_INSTRUCTION, CAN_INSTRUCTION_ID & CAN_ID_MASK, !!(CAN_INSTRUCTION_ID & CAN_ID_FLAG_EXTENDED));

			switch (buffer[0]) {
			case 'P':	// send ping
				inst.instruction = 0x02;
				can_mob_set_data(MOB_SEND_INSTRUCTION, 1, (uint8_t*)&inst);
				can_mob_transmit_nowait(MOB_SEND_INSTRUCTION);
				send_cmd = false;
				break;
			case 't':
				send_cmd = true;
				extended = false;
				rtr = false;
				break;
			case 'T':
				send_cmd = true;
				extended = true;
				rtr = false;
				break;
			case 'r':
				send_cmd = true;
				extended = false;
				rtr = true;
				break;
			case 'R':
				send_cmd = true;
				extended = true;
				rtr = true;
				break;
			default:
				send_cmd = false;
				usart_send_str_P(PSTR("~E\r\n"));
				break;
			}

			if (send_cmd) {
				bool valid = true;

				if (valid) {
					uint8_t min_len = 2 + (extended ? 8 : 3);
					if (msg_len < min_len) {
						usart_send_str_P(PSTR("~E\r\n"));
						valid = false;
					}
				}

				if (valid) {
					// transform hex chars to value
					for (uint8_t i = 1;i<msg_len;i++) {
						if (buffer[i] >= 'a' && buffer[i] <= 'f')
							buffer[i] -= ('a' - 10);
						else if (buffer[i] >= 'A' && buffer[i] <= 'F')
							buffer[i] -= ('A' - 10);
						else if (buffer[i] >= '0' && buffer[i] <= '9')
							buffer[i] -= '0';
						else {
							usart_send_str_P(PSTR("~E\r\n"));
							valid = false;
							break;
						}
					}
				}

				if (valid) {
					uint32_t id = 0;
					uint8_t id_len = (extended ? 8 : 3);
					for (uint8_t i=0;i<id_len;i++) {
						uint8_t d = buffer[i+1];
						id |= ((uint32_t)d) << (4 * (id_len - i - 1));
					}

					if (extended)
						id |= CAN_ID_FLAG_EXTENDED;
					if (rtr)
						id |= CAN_ID_FLAG_RTR;

					uint8_t dlc = buffer[id_len+1];

					inst.instruction = 0x00;
					inst.instruction0.id = id;
					inst.instruction0.dlc = dlc;
					//NOTE If dlc<2, we put invalid data in there, but
					//     that doesn't matter.
					inst.instruction0.data[0] = (buffer[id_len+2] << 4) | buffer[id_len+3];
					inst.instruction0.data[1] = (buffer[id_len+4] << 4) | buffer[id_len+5];

					if (msg_len != 2 + id_len + 2*dlc*(rtr ? 0 : 1)) {
						usart_send_str_P(PSTR("~E\r\n"));
						valid = false;
					} else {
						// send first message
						can_mob_set_data(MOB_SEND_INSTRUCTION, 8, (uint8_t*)&inst);
						can_mob_transmit_nowait(MOB_SEND_INSTRUCTION);

						// prepare next message
						inst.instruction = 0x01;
						for (uint8_t i=0;i<6;i++)
							inst.instruction1.data[i] = (buffer[id_len+6+2*i] << 4) | buffer[id_len+6+2*i+1];

						// wait for transmission of first message
						can_mob_wait_for_transmission_of_current_mob(MOB_SEND_INSTRUCTION);

						// send second message
						can_mob_set_data(MOB_SEND_INSTRUCTION, 8, (uint8_t*)&inst);
						can_mob_transmit_nowait(MOB_SEND_INSTRUCTION);
					}
				}
			}

			// enable interrupts again (if they have been enabled before)
			SREG = sreg_backup;
		}
	} else if (pos_in_line <= sizeof(buffer) / sizeof(*buffer)) {
		// ignore spaces
		if (c != ' ') {
			// put char in the buffer
			buffer[pos_in_line-1] = c;
			pos_in_line++;
		}
	}

	// we have used the char
	return true;
}

static void can_helper_print_message(uint32_t id, uint8_t dlc, uint8_t* data) {
	// disable interrupts, so nobody can interfere with our sending on RS232
	uint8_t sreg_backup = SREG;
	cli();

	// mark it as a CAN master line
	usart_send('~');

	bool extended = !!(id & CAN_ID_FLAG_EXTENDED);
	bool rtr      = !!(id & CAN_ID_FLAG_RTR);
	char c;
	if (extended) {
		if (rtr)
			c = 'R';
		else
			c = 'T';
	} else {
		if (rtr)
			c = 'r';
		else
			c = 't';
	}
	usart_send(c);
	usart_send(' ');

	if (extended)
		id &= CAN_ID_MASK;
	else
		id &= 0x7ff;

	uint8_t id_len = (extended ? 8 : 3);
	for (uint8_t i = 0;i<id_len;i++) {
		uint8_t digit = 0x0f & (uint8_t)(id >> (4 * (id_len - i - 1)));
		if (digit < 10)
			digit += '0';
		else
			digit += 'a' - 10;
		usart_send(digit);
	}
	
	usart_send(' ');

	if (dlc > 8)
		dlc = 8;
	usart_send('0' + dlc);

	if (!rtr) {
		for (uint8_t i = 0;i<dlc;i++) {
			uint8_t digit;
			uint8_t data_byte = data[i];

			usart_send(' ');

			digit = data_byte >> 4;
			if (digit < 10)
				digit += '0';
			else
				digit += 'a' - 10;
			usart_send(digit);

			digit = data_byte & 0x0f;
			if (digit < 10)
				digit += '0';
			else
				digit += 'a' - 10;
			usart_send(digit);
		}
	}

	usart_send('\r');
	usart_send('\n');

	// restore interrupt status
	SREG = sreg_backup;
}

static void can_helper_master_receive_relay(void) {
	static uint32_t id;
	static uint8_t dlc;
	static uint8_t data[8];

	// save CANPAGE, so we can restore it later
	uint8_t saved_canpage = CANPAGE;

	if (false) {
		// send to RS232 verbatim
		id = can_mob_get_id(MOB_RECEIVE_RELAY);
		dlc = can_mob_get_data_length(MOB_RECEIVE_RELAY);
		CANPAGE = (MOB_RECEIVE_RELAY<<4);
		for (uint8_t i = 0;i<dlc;i++) {
			data[i] = CANMSG;
		}

		can_helper_print_message(id, dlc, data);
	} else {
		instruction_t inst;
		CANPAGE = (MOB_RECEIVE_RELAY<<4);
		uint8_t* tmp = (uint8_t*)&inst;
		for (uint8_t i=0;i<sizeof(instruction_t);i++)
			*(tmp++) = CANMSG;

	    // reset INT reason
	    CANSTMOB &= ~(1<<RXOK);
	    // re-enable RX
	    CANCDMOB |= (1<<CONMOB1);

		uint8_t sreg_backup;
		switch (inst.instruction) {
		case 0x00:
			id = inst.instruction0.id;
			dlc = inst.instruction0.dlc;
			memcpy(data, inst.instruction0.data, 2);
			break;
		case 0x01:
			memcpy(data+2, inst.instruction1.data, 6);

			can_helper_print_message(id, dlc, data);
			break;
		case 0x02:
			// we got a ping response

			// disable interrupts
			sreg_backup = SREG;
			cli();

			// notify PC
			usart_send_str_P(PSTR("~P\r\n"));

			// enable interrupts again (if they have been enabled before)
			SREG = sreg_backup;
			break;
		case 0x04:
			// we lost some messages

			// disable interrupts
			sreg_backup = SREG;
			cli();

			// notify PC
			usart_send_str_P(PSTR("~L:"));
			usart_send_number(inst.instruction4.lost_messages, 10, 0);
			usart_send_str_P(PSTR("\r\n"));

			// enable interrupts again (if they have been enabled before)
			SREG = sreg_backup;
			break;
		default:
			// error: we don't know that code

			// disable interrupts
			sreg_backup = SREG;
			cli();

			// notify PC
			usart_send_str_P(PSTR("~E:unknown instruction code\r\n"));

			// enable interrupts again (if they have been enabled before)
			SREG = sreg_backup;
		}
	}

    // restore CANPAGE
    CANPAGE = saved_canpage;
}
