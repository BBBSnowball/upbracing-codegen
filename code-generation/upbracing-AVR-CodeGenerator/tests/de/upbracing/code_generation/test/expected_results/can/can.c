
#include "can.h"
#include "can_at90.h"

// CAN receive interrupt
ISR(SIG_CAN_INTERRUPT1) {
	if (CANSIT1==0 && CANSIT2==0)
		//TODO we HAVE to reset the interrupt reason!
		return;

	uint8_t saved_canpage = CANPAGE;

	if (0) {	// dummy clause to make code generation easier
	} else if (can_caused_interrupt(MOB_Bootloader_SelectNode)) {		// CAN ID: 0x0
		CANPAGE = (MOB_Bootloader_SelectNode<<4);
		handle_bootloader_selectnode();
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_Bootloader_SelectNode<<4);
	} else if (can_caused_interrupt(MOB_Bootloader_1)) {		// CAN ID: 0x1, alias: RS232_FORWARD_DATA
		CANPAGE = (MOB_Bootloader_1<<4);
		handle_rs232_forward_data();
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_Bootloader_1<<4);
	} else if (can_caused_interrupt(MOB_ClutchGetPos)) {		// CAN ID: 0x5ff
		CANPAGE = (MOB_ClutchGetPos<<4);
		handle_clutch_actuator();
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_ClutchGetPos<<4);
	} else if (can_caused_interrupt(MOB_Kupplung)) {		// shared mob
		CANPAGE = (MOB_Kupplung<<4);
		if (0) {	// dummy clause to make code generation easier
		} else if (1 && CANIDT3 == 0 && CANIDT4 == 128) {		// Kupplung_Soll, CAN ID: 0x10x

			// reading signal Kupplung_Soll
			{
				uint8_t value = CANMSG;
				if (!demo_mode) {
					display_values[DI_Kupplung_Soll].value8 = value;
					display_values[DI_Kupplung_Soll].changed = 1;
				}
			}
			clutch_calibration_mode = false;
		} else if (1 && CANIDT3 == 8 && CANIDT4 == 8) {		// Kupplung_Calibration, CAN ID: 0x101x

			// reading signal Kupplung_RAW
			{
				union {
					uint16_t value;
					struct {
						uint8_t byte0;
						uint8_t byte1;
					} bytes;
				} x;
				x.bytes.byte1 = CANMSG;
				x.bytes.byte0 = CANMSG;
				uint16_t value = x.value;
				display_values[DI_Kupplung_Soll].value8 = value / 4;
				display_values[DI_Kupplung_Soll].changed = 1;
			}
			clutch_calibration_mode = true;
		}
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_Kupplung<<4);
	} else if (can_caused_interrupt(MOB_Gear)) {		// CAN ID: 0x71x
		CANPAGE = (MOB_Gear<<4);

		// reading signal Gang
		{
			uint8_t value = CANMSG;
			setGang(value);
		}
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_Gear<<4);
	} else if (can_caused_interrupt(MOB_Sensoren)) {		// CAN ID: 0x80x
		CANPAGE = (MOB_Sensoren<<4);

		// skipping 2 byte(s)
		CANMSG;
		CANMSG;

		// reading signal Temp_Oel
		{
			uint8_t value = CANMSG;
			setTemp_Oel(value);
		}

		// skipping 1 byte(s)
		CANMSG;

		// reading signal Druck_Oel
		{
			union {
				uint16_t value;
				struct {
					uint8_t byte0;
					uint8_t byte1;
				} bytes;
			} x;
			x.bytes.byte0 = CANMSG;
			x.bytes.byte1 = CANMSG;
			uint16_t value = x.value;
			setDruck_Oel(value);
		}
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_Sensoren<<4);
	} else if (can_caused_interrupt(MOB_Sensoren_2)) {		// CAN ID: 0x81x
		CANPAGE = (MOB_Sensoren_2<<4);

		// reading signal Druck_Kraftstoff
		{
			union {
				uint16_t value;
				struct {
					uint8_t byte0;
					uint8_t byte1;
				} bytes;
			} x;
			x.bytes.byte0 = CANMSG;
			x.bytes.byte1 = CANMSG;
			uint16_t value = x.value;
			setDruck_Kraftstoff(value);
		}
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_Sensoren_2<<4);
	} else if (can_caused_interrupt(MOB_OpenSquirt_Engine)) {		// CAN ID: 0x88x
		CANPAGE = (MOB_OpenSquirt_Engine<<4);

		// reading signal Drehzahl
		{
			union {
				uint16_t value;
				struct {
					uint8_t byte0;
					uint8_t byte1;
				} bytes;
			} x;
			x.bytes.byte1 = CANMSG;
			x.bytes.byte0 = CANMSG;
			uint16_t value = x.value;
			setDrehzahl(value);
		}

		// reading signal Druck_Ansaug
		{
			union {
				int16_t value;
				struct {
					uint8_t byte0;
					uint8_t byte1;
				} bytes;
			} x;
			x.bytes.byte1 = CANMSG;
			x.bytes.byte0 = CANMSG;

#warning This signal uses factor or offset, which is not supported yet.

			int16_t value = x.value;
			setDruck_Ansaug(value);
		}

		// reading signal Lambda
		{
			union {
				int16_t value;
				struct {
					uint8_t byte0;
					uint8_t byte1;
				} bytes;
			} x;
			x.bytes.byte1 = CANMSG;
			x.bytes.byte0 = CANMSG;

#warning This signal uses factor or offset, which is not supported yet.

			int16_t value = x.value;
			setLambda(value);
		}

		// reading signal ThrottlePosition
		{
			union {
				int16_t value;
				struct {
					uint8_t byte0;
					uint8_t byte1;
				} bytes;
			} x;
			x.bytes.byte1 = CANMSG;
			x.bytes.byte0 = CANMSG;

#warning This signal uses factor or offset, which is not supported yet.

			int16_t value = x.value;
			setThrottlePosition(value);
		}
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_OpenSquirt_Engine<<4);
	} else if (can_caused_interrupt(MOB_OpenSquirt_Sensoren1)) {		// CAN ID: 0x108x
		CANPAGE = (MOB_OpenSquirt_Sensoren1<<4);

		// reading signal Temp_Wasser
		{
			union {
				int16_t value;
				struct {
					uint8_t byte0;
					uint8_t byte1;
				} bytes;
			} x;
			x.bytes.byte1 = CANMSG;
			x.bytes.byte0 = CANMSG;

#warning This signal uses factor or offset, which is not supported yet.

			int16_t value = x.value;
			setTemp_Wasser(value);
		}

		// reading signal Temp_Ansaug
		{
			union {
				int16_t value;
				struct {
					uint8_t byte0;
					uint8_t byte1;
				} bytes;
			} x;
			x.bytes.byte1 = CANMSG;
			x.bytes.byte0 = CANMSG;

#warning This signal uses factor or offset, which is not supported yet.

			int16_t value = x.value;
			setTemp_Ansaug(value);
		}

		// reading signal Boardspannung
		{
			union {
				int16_t value;
				struct {
					uint8_t byte0;
					uint8_t byte1;
				} bytes;
			} x;
			x.bytes.byte1 = CANMSG;
			x.bytes.byte0 = CANMSG;

#warning This signal uses factor or offset, which is not supported yet.

			int16_t value = x.value;
			setBoardspannung(value);
		}
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_OpenSquirt_Sensoren1<<4);
	} else if (can_caused_interrupt(MOB_Geschwindigkeit)) {		// CAN ID: 0x110x
		CANPAGE = (MOB_Geschwindigkeit<<4);

		// reading signal Geschwindigkeit
		{
			uint8_t value = CANMSG;
			setGeschwindigkeit(value);
		}
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_Geschwindigkeit<<4);
	} else if (can_caused_interrupt(MOB_Lenkrad_main2display)) {		// CAN ID: 0x4201x
		CANPAGE = (MOB_Lenkrad_main2display<<4);
		handle_main2display();
		// only necessary, if we change CANPAGE in our message handler
		// But if we forget to do that, the interrupt handler will be called forever.
		CANPAGE = (MOB_Lenkrad_main2display<<4);
	} else {
		// well, this shouldn't happen
		// do nothing...
	}

    // reset INT reason
    CANSTMOB &= ~(1<<RXOK);
    // re-enable RX, reconfigure MOb IDE=1
    //CANCDMOB = (1<<CONMOB1) | (1<<IDE);
    CANCDMOB |= (1<<CONMOB1);

    // restore CANPAGE
    CANPAGE = saved_canpage;
}

inline static void can_init_MOB_Bootloader_SelectNode(void) { can_mob_init_receive2(MOB_Bootloader_SelectNode, CAN_Bootloader_SelectNode, false); }
inline static void can_init_MOB_Bootloader_1(void) { can_mob_init_receive2(MOB_Bootloader_1, CAN_Bootloader_1, false); }
inline static void can_init_MOB_ClutchGetPos(void) { can_mob_init_receive2(MOB_ClutchGetPos, CAN_ClutchGetPos, false); }
inline static void can_init_MOB_Kupplung(void) {
	// select MOB
	CANPAGE = (MOB_Kupplung<<4);

	// set id and mask
	CANIDT1 = 0x00;
	CANIDT2 = 0x00;
	CANIDT3 = 0x08;
	CANIDT4 = 0x88;
	CANIDM1 = 0xff;
	CANIDM2 = 0xff;
	CANIDM3 = 0xf7;
	CANIDM4 = 0x75;

	//configure message as receive-msg (see CANCDMOB register, page257)
	CANCDMOB = (1<<CONMOB1) | (1<<IDE);

	// enable interrupts for this MOb
	can_mob_enable(MOB_Kupplung);
	can_mob_enable_interrupt(MOB_Kupplung);
}
inline static void can_init_MOB_Gear(void) { can_mob_init_receive2(MOB_Gear, CAN_Gear, true); }
inline static void can_init_MOB_Sensoren(void) { can_mob_init_receive2(MOB_Sensoren, CAN_Sensoren, true); }
inline static void can_init_MOB_Sensoren_2(void) { can_mob_init_receive2(MOB_Sensoren_2, CAN_Sensoren_2, true); }
inline static void can_init_MOB_OpenSquirt_Engine(void) { can_mob_init_receive2(MOB_OpenSquirt_Engine, CAN_OpenSquirt_Engine, true); }
inline static void can_init_MOB_OpenSquirt_Sensoren1(void) { can_mob_init_receive2(MOB_OpenSquirt_Sensoren1, CAN_OpenSquirt_Sensoren1, true); }
inline static void can_init_MOB_Geschwindigkeit(void) { can_mob_init_receive2(MOB_Geschwindigkeit, CAN_Geschwindigkeit, true); }
inline static void can_init_MOB_Lenkrad_main2display(void) { can_mob_init_receive2(MOB_Lenkrad_main2display, CAN_Lenkrad_main2display, true); }
inline static void can_init_MOB_Launch(void) { can_mob_init_transmit2(MOB_Launch, CAN_Launch, true); }
inline static void can_init_MOB_Radio(void) { can_mob_init_transmit2(MOB_Radio, CAN_Radio, true); }

inline static void can_init_mobs(void) {
	can_init_MOB_Bootloader_SelectNode();
	// not initialising disabled MOB MOB_Bootloader_1 for messages 
	can_init_MOB_ClutchGetPos();
	can_init_MOB_Kupplung();
	can_init_MOB_Gear();
	can_init_MOB_Sensoren();
	can_init_MOB_Sensoren_2();
	can_init_MOB_OpenSquirt_Engine();
	can_init_MOB_OpenSquirt_Sensoren1();
	can_init_MOB_Geschwindigkeit();
	can_init_MOB_Lenkrad_main2display();
	can_init_MOB_Launch();
	can_init_MOB_Radio();
}

#warning There were 6 warnings and/or errors
/*
WARN:  This signal uses factor or offset, which is not supported yet.
WARN:  This signal uses factor or offset, which is not supported yet.
WARN:  This signal uses factor or offset, which is not supported yet.
WARN:  This signal uses factor or offset, which is not supported yet.
WARN:  This signal uses factor or offset, which is not supported yet.
WARN:  This signal uses factor or offset, which is not supported yet.
*/
