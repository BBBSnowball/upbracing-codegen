
#ifndef CAN_VALUETABLES_H_
#define CAN_VALUETABLES_H_

#include <common.h>

#ifndef DONT_SET_VALUE_TABLES
#ifndef SKIP_VT_BootloaderNode
typedef enum _BootloaderNode {
	LenkradMain = 66,
	LenkradDisplay = 67,
	LenkradCANtoRS232 = 68,
	Sensorboard = 71,
} BootloaderNode;
#endif	// not defined SKIP_VT_BootloaderNode
#ifndef SKIP_VT_Dunkermotor_CANOPEN_Index
typedef enum _Dunkermotor_CANOPEN_Index {
	DUNKER_VPOS_ActualPosition_cnt = 14194,
	DUNKER_MovA = 14224,
} Dunkermotor_CANOPEN_Index;
#endif	// not defined SKIP_VT_Dunkermotor_CANOPEN_Index
#ifndef SKIP_VT_boolean
typedef bool boolean;
#endif	// not defined SKIP_VT_boolean
#ifndef SKIP_VT_main2display
typedef enum _main2display {
	MAIN_ACK = 0,
	START_TETRIS = 1,
	START_FLIPPER = 2,
	BOOTLOADER_ACTIVE = 3,
} main2display;
#endif	// not defined SKIP_VT_main2display
#endif	// not defined DONT_SET_VALUE_TABLES

#endif	// defined CAN_VALUETABLES_H_
