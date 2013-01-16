
#ifndef CAN_LENKRADDISPLAY_DEFS_H_
#define CAN_LENKRADDISPLAY_DEFS_H_

#define ECU_NODE_ID 0x43


/////////////////////////
///  CAN definitions  ///
/////////////////////////

// CAN Message IDs:
typedef enum uint32_t {
	CAN_Bootloader_SelectNode        = 0x0,    // receive
	CAN_Bootloader_1                 = 0x1,    // receive
	CAN_RS232_FORWARD_DATA           = 0x1,    // receive, alias for Bootloader_1
	CAN_ClutchGetPos                 = 0x5ff,  // receive
	CAN_Kupplung_Soll                = 0x10,   // receive
	CAN_Gear                         = 0x71,   // receive
	CAN_Sensoren                     = 0x80,   // receive
	CAN_Sensoren_2                   = 0x81,   // receive
	CAN_OpenSquirt_Engine            = 0x88,   // receive
	CAN_Kupplung_Calibration         = 0x101,  // receive
	CAN_OpenSquirt_Sensoren1         = 0x108,  // receive
	CAN_Geschwindigkeit              = 0x110,  // receive
	CAN_Lenkrad_main2display         = 0x4201, // receive
	CAN_Kupplung_Calibration_Control = 0x250,  // send
	CAN_Launch                       = 0x60,   // send
	CAN_Radio                        = 0x90,   // send
	CAN_CockpitBrightness            = 0x4242, // send
	CAN_EmptyMessage                 = 0x7b,   // send
	CAN_EmptyMessage2                = 0x7c,   // send
} CAN_msgID;

// do the messages use extended CAN ids or not (0 = standard, 1 = extended)
typedef enum {
	CAN_Bootloader_SelectNode_IsExtended        = 0,
	CAN_Bootloader_1_IsExtended                 = 0,
	CAN_RS232_FORWARD_DATA_IsExtended           = 0,
	CAN_ClutchGetPos_IsExtended                 = 0,
	CAN_Kupplung_Soll_IsExtended                = 1,
	CAN_Gear_IsExtended                         = 1,
	CAN_Sensoren_IsExtended                     = 1,
	CAN_Sensoren_2_IsExtended                   = 1,
	CAN_OpenSquirt_Engine_IsExtended            = 1,
	CAN_Kupplung_Calibration_IsExtended         = 1,
	CAN_OpenSquirt_Sensoren1_IsExtended         = 1,
	CAN_Geschwindigkeit_IsExtended              = 1,
	CAN_Lenkrad_main2display_IsExtended         = 1,
	CAN_Kupplung_Calibration_Control_IsExtended = 1,
	CAN_Launch_IsExtended                       = 1,
	CAN_Radio_IsExtended                        = 1,
	CAN_CockpitBrightness_IsExtended            = 1,
	CAN_EmptyMessage_IsExtended                 = 0,
	CAN_EmptyMessage2_IsExtended                = 0,
} CAN_isExtended;

/*
tx_msgs: Kupplung_Calibration_Control Launch Radio CockpitBrightness EmptyMessage EmptyMessage2
rx_msgs: Bootloader_SelectNode Bootloader_1 ClutchGetPos Kupplung_Soll Gear Sensoren Sensoren_2 OpenSquirt_Engine Kupplung_Calibration OpenSquirt_Sensoren1 Geschwindigkeit Lenkrad_main2display
rx_signals: Bootloader_SelectNode Clutch_IstPosition Kupplung_Soll Gang Temp_Oel Druck_Oel Druck_Kraftstoff Drehzahl Druck_Ansaug Lambda ThrottlePosition Kupplung_RAW Temp_Wasser Temp_Ansaug Boardspannung Geschwindigkeit Lenkrad_main2display
*/

#include "can_valuetables.h"

typedef enum {
	MOB_Bootloader_SelectNode = 0,   // CAN ID: 0x0, receive
	MOB_Bootloader_1          = 1,   // CAN ID: 0x1, receive
	MOB_RS232_FORWARD_DATA    = 1,   // receive, alias for Bootloader_1
	MOB_ClutchGetPos          = 2,   // CAN ID: 0x5ff, receive
	MOB_Kupplung              = 3,   // CAN ID: 0x10x, 0x101x, receive
	MOB_Gear                  = 4,   // CAN ID: 0x71x, receive
	MOB_Sensoren              = 5,   // CAN ID: 0x80x, receive
	MOB_Sensoren_2            = 6,   // CAN ID: 0x81x, receive
	MOB_OpenSquirt_Engine     = 7,   // CAN ID: 0x88x, receive
	MOB_OpenSquirt_Sensoren1  = 8,   // CAN ID: 0x108x, receive
	MOB_Geschwindigkeit       = 9,   // CAN ID: 0x110x, receive
	MOB_Lenkrad_main2display  = 10,  // CAN ID: 0x4201x, receive
	MOB_Launch                = 11,  // CAN ID: 0x60x, send
	MOB_Radio                 = 12,  // CAN ID: 0x90x, send
	MOB_EmptyMessage          = 13,  // CAN ID: 0x7b, send
	MOB_EmptyMessage2         = 14,  // CAN ID: 0x7c, send

	MOB_GENERAL_MESSAGE_TRANSMITTER = 15
} MessageObjectID;

#include "can_at90.h"

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
inline static void can_init_MOB_EmptyMessage(void) { can_mob_init_transmit2(MOB_EmptyMessage, CAN_EmptyMessage, false); }
inline static void can_init_MOB_EmptyMessage2(void) { can_mob_init_transmit2(MOB_EmptyMessage2, CAN_EmptyMessage2, false); }

void can_init_mobs(void);

// we use interrupts - polling isn't necessary
inline static void can_poll(void) { }

// 0x250x
inline static void send_Kupplung_Calibration_Control(bool wait, boolean KupplungKalibrationActive) {
	// select MOB
	CANPAGE = (MOB_GENERAL_MESSAGE_TRANSMITTER<<4);

	// wait for an ongoing transmission to finish
	can_mob_wait_for_transmission_of_current_mob();

	// reset transmission status
	CANSTMOB = 0;

	can_mob_init_transmit2(MOB_GENERAL_MESSAGE_TRANSMITTER, CAN_Kupplung_Calibration_Control, CAN_Kupplung_Calibration_Control_IsExtended);

	// disable mob, as it would be retransmitted otherwise
	CANCDMOB = (CANCDMOB&0x30) | ((1&0xf)<<DLC0);

		// writing signal KupplungKalibrationActive
		{
			boolean value;
			value = KupplungKalibrationActive;
			CANMSG = (uint8_t) value;
		}
	if (wait)
		can_mob_transmit_wait(MOB_GENERAL_MESSAGE_TRANSMITTER);
	else
		can_mob_transmit_nowait(MOB_GENERAL_MESSAGE_TRANSMITTER);
}
inline static void send_Kupplung_Calibration_Control_wait(boolean KupplungKalibrationActive) {
	send_Kupplung_Calibration_Control(true, KupplungKalibrationActive);
}
inline static void send_Kupplung_Calibration_Control_nowait(boolean KupplungKalibrationActive) {
	send_Kupplung_Calibration_Control(false, KupplungKalibrationActive);
}
// 0x60x
inline static void send_Launch(bool wait, boolean Launch) {
	// select MOB
	CANPAGE = (MOB_Launch<<4);

	// wait for an ongoing transmission to finish
	can_mob_wait_for_transmission_of_current_mob();

	// reset transmission status
	CANSTMOB = 0;

	can_mob_init_transmit2(MOB_Launch, CAN_Launch, CAN_Launch_IsExtended);

	// disable mob, as it would be retransmitted otherwise
	CANCDMOB = (CANCDMOB&0x30) | ((1&0xf)<<DLC0);

		// writing signal Launch
		{
			boolean value;
			value = Launch;
			CANMSG = (uint8_t) value;
		}
	if (wait)
		can_mob_transmit_wait(MOB_Launch);
	else
		can_mob_transmit_nowait(MOB_Launch);
}
inline static void send_Launch_wait(boolean Launch) {
	send_Launch(true, Launch);
}
inline static void send_Launch_nowait(boolean Launch) {
	send_Launch(false, Launch);
}
// 0x90x
inline static void send_Radio(bool wait, boolean Radio) {
	// select MOB
	CANPAGE = (MOB_Radio<<4);

	// wait for an ongoing transmission to finish
	can_mob_wait_for_transmission_of_current_mob();

	// reset transmission status
	CANSTMOB = 0;

	can_mob_init_transmit2(MOB_Radio, CAN_Radio, CAN_Radio_IsExtended);

	// disable mob, as it would be retransmitted otherwise
	CANCDMOB = (CANCDMOB&0x30) | ((1&0xf)<<DLC0);

		// writing signal Radio
		{
			boolean value;
			value = Radio;
			CANMSG = (uint8_t) value;
		}
	if (wait)
		can_mob_transmit_wait(MOB_Radio);
	else
		can_mob_transmit_nowait(MOB_Radio);
}
inline static void send_Radio_wait(boolean Radio) {
	send_Radio(true, Radio);
}
inline static void send_Radio_nowait(boolean Radio) {
	send_Radio(false, Radio);
}
// 0x4242x
inline static void send_CockpitBrightness(bool wait, uint8_t CockpitRPMBrightness, uint8_t CockpitGangBrightness, uint8_t CockpitShiftLightPeriod, uint8_t CockpitShiftLightAlwaysFlash) {
	// select MOB
	CANPAGE = (MOB_GENERAL_MESSAGE_TRANSMITTER<<4);

	// wait for an ongoing transmission to finish
	can_mob_wait_for_transmission_of_current_mob();

	// reset transmission status
	CANSTMOB = 0;

	can_mob_init_transmit2(MOB_GENERAL_MESSAGE_TRANSMITTER, CAN_CockpitBrightness, CAN_CockpitBrightness_IsExtended);

	// disable mob, as it would be retransmitted otherwise
	CANCDMOB = (CANCDMOB&0x30) | ((4&0xf)<<DLC0);

		// writing signal CockpitRPMBrightness
		{
			uint8_t value;
			value = CockpitRPMBrightness;
			CANMSG = value;
		}

		// writing signal CockpitGangBrightness
		{
			uint8_t value;
			value = CockpitGangBrightness;
			CANMSG = value;
		}

		// writing signal CockpitShiftLightPeriod
		{
			uint8_t value;
			value = CockpitShiftLightPeriod;
			CANMSG = value;
		}

		// writing signal CockpitShiftLightAlwaysFlash
		{
			uint8_t value;
			value = CockpitShiftLightAlwaysFlash;
			CANMSG = value;
		}
	if (wait)
		can_mob_transmit_wait(MOB_GENERAL_MESSAGE_TRANSMITTER);
	else
		can_mob_transmit_nowait(MOB_GENERAL_MESSAGE_TRANSMITTER);
}
inline static void send_CockpitBrightness_wait(uint8_t CockpitRPMBrightness, uint8_t CockpitGangBrightness, uint8_t CockpitShiftLightPeriod, uint8_t CockpitShiftLightAlwaysFlash) {
	send_CockpitBrightness(true, CockpitRPMBrightness, CockpitGangBrightness, CockpitShiftLightPeriod, CockpitShiftLightAlwaysFlash);
}
inline static void send_CockpitBrightness_nowait(uint8_t CockpitRPMBrightness, uint8_t CockpitGangBrightness, uint8_t CockpitShiftLightPeriod, uint8_t CockpitShiftLightAlwaysFlash) {
	send_CockpitBrightness(false, CockpitRPMBrightness, CockpitGangBrightness, CockpitShiftLightPeriod, CockpitShiftLightAlwaysFlash);
}
// 0x7b
inline static void send_EmptyMessage(bool wait) {
	// select MOB
	CANPAGE = (MOB_EmptyMessage<<4);

	// wait for an ongoing transmission to finish
	can_mob_wait_for_transmission_of_current_mob();

	// reset transmission status
	CANSTMOB = 0;

	can_mob_init_transmit2(MOB_EmptyMessage, CAN_EmptyMessage, CAN_EmptyMessage_IsExtended);

	// disable mob, as it would be retransmitted otherwise
	CANCDMOB = (CANCDMOB&0x30) | ((0&0xf)<<DLC0);
	if (wait)
		can_mob_transmit_wait(MOB_EmptyMessage);
	else
		can_mob_transmit_nowait(MOB_EmptyMessage);
}
inline static void send_EmptyMessage_wait(void) {
	send_EmptyMessage(true);
}
inline static void send_EmptyMessage_nowait(void) {
	send_EmptyMessage(false);
}
// 0x7c
inline static void send_EmptyMessage2(bool wait) {
	// select MOB
	CANPAGE = (MOB_EmptyMessage2<<4);

	// wait for an ongoing transmission to finish
	can_mob_wait_for_transmission_of_current_mob();

	// reset transmission status
	CANSTMOB = 0;

	can_mob_init_transmit2(MOB_EmptyMessage2, CAN_EmptyMessage2, CAN_EmptyMessage2_IsExtended);

	// disable mob, as it would be retransmitted otherwise
	CANCDMOB = (CANCDMOB&0x30) | ((2&0xf)<<DLC0);
	if (wait)
		can_mob_transmit_wait(MOB_EmptyMessage2);
	else
		can_mob_transmit_nowait(MOB_EmptyMessage2);
}
inline static void send_EmptyMessage2_wait(void) {
	send_EmptyMessage2(true);
}
inline static void send_EmptyMessage2_nowait(void) {
	send_EmptyMessage2(false);
}

#endif	// defined CAN_LENKRADDISPLAY_DEFS_H_
