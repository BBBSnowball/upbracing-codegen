
#include "can.h"
#include "can_at90.h"
#include "global_variables.h"
#include <avr/interrupt.h>

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

#warning The program expects a different formula for converting the signal Druck_Ansaug to physical units. Automatic adjustment is not supported, yet:\
		expected by program: physical_value = raw_value * 1/1 + 0.0\
		on the CAN bus: physical_value = raw_value * 10.0 + 0.0\
		(For example, the error for raw_value = -32768 is 294912.0, but it may be higher for other values.)

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

#warning The program expects a different formula for converting the signal Lambda to physical units. Automatic adjustment is not supported, yet:\
		expected by program: physical_value = raw_value * 1/1 + 0.0\
		on the CAN bus: physical_value = raw_value * 147.0 + 0.0\
		(For example, the error for raw_value = -32768 is 4784128.0, but it may be higher for other values.)

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

#warning The program expects a different formula for converting the signal ThrottlePosition to physical units. Automatic adjustment is not supported, yet:\
		expected by program: physical_value = raw_value * 1/1 + 0.0\
		on the CAN bus: physical_value = raw_value * 10.0 + 0.0\
		(For example, the error for raw_value = -32768 is 294912.0, but it may be higher for other values.)

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

#warning The program expects a different formula for converting the signal Temp_Ansaug to physical units. Automatic adjustment is not supported, yet:\
		expected by program: physical_value = raw_value * 1/1 + 0.0\
		on the CAN bus: physical_value = raw_value * 10.0 + 0.0\
		(For example, the error for raw_value = -32768 is 294912.0, but it may be higher for other values.)

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

void can_init_mobs(void) {
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
	can_init_MOB_EmptyMessage();
	can_init_MOB_EmptyMessage2();
}

#include "Os.h"

//OS Tasks for periodic sending of messages:

TASK(Kupplung_Calibration_Control) { //period: 500 ms. Task for message Kupplung_Calibration_Control

	//Sending message Kupplung_Calibration_Control
	{
		//Test replacement of entire task handler for this message
	}
	TerminateTask();
}

TASK(Launch) { //period: 3 ms. Shared task for messages Launch, Radio

	//Sending message Launch
	{
		//Test comment before task message
		//Test comment before task signal
		boolean par1;
		par1 = getLaunch(); //read value
		send_Launch_nowait(par1);
		//Test comment after task signal
		//Test comment after task message
	}

	//Sending message Radio
	{
		//Another test comment before task
		boolean par1;
		par1 = getRadio(); //read value
		send_Radio_nowait(par1);
		//Another test comment after task
	}
	TerminateTask();
}

TASK(CockpitBrightness) { //period: 333333 us. Task for message CockpitBrightness

	//Sending message CockpitBrightness
	{
		//Test before read value
		uint8_t par1;
		par1 = 34; //read value
		//Test after read value
		uint8_t par2;
		par2 = getCockpitGangBrightness(); //read value
		uint8_t par3;
		par3 = getCockpitShiftLightPeriod(); //read value
		uint8_t par4;
		par4 = getCockpitShiftLightAlwaysFlash(); //read value
		send_CockpitBrightness_nowait(par1, par2, par3, par4);
	}
	TerminateTask();
}

#warning There were 4 warnings and/or errors
/*
WARN:  The program expects a different formula for converting the signal Druck_Ansaug to physical units. Automatic adjustment is not supported, yet:
	expected by program: physical_value = raw_value * 1/1 + 0.0
	on the CAN bus: physical_value = raw_value * 10.0 + 0.0
	(For example, the error for raw_value = -32768 is 294912.0, but it may be higher for other values.)
WARN:  The program expects a different formula for converting the signal Lambda to physical units. Automatic adjustment is not supported, yet:
	expected by program: physical_value = raw_value * 1/1 + 0.0
	on the CAN bus: physical_value = raw_value * 147.0 + 0.0
	(For example, the error for raw_value = -32768 is 4784128.0, but it may be higher for other values.)
WARN:  The program expects a different formula for converting the signal ThrottlePosition to physical units. Automatic adjustment is not supported, yet:
	expected by program: physical_value = raw_value * 1/1 + 0.0
	on the CAN bus: physical_value = raw_value * 10.0 + 0.0
	(For example, the error for raw_value = -32768 is 294912.0, but it may be higher for other values.)
WARN:  The program expects a different formula for converting the signal Temp_Ansaug to physical units. Automatic adjustment is not supported, yet:
	expected by program: physical_value = raw_value * 1/1 + 0.0
	on the CAN bus: physical_value = raw_value * 10.0 + 0.0
	(For example, the error for raw_value = -32768 is 294912.0, but it may be higher for other values.)
*/
