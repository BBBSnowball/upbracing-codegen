/*
 * pins.h
 *
 * This file defines names for processor pins.
 *
 * Generated automatically. DO NOT MODIFY! Change config.rb instead.
 */

#ifndef PIN_NAMES_H_
#define PIN_NAMES_H_

/////////////////////////
///  Pin definitions  ///
/////////////////////////

#define ERROR1_PORT  PORTA
#define ERROR1_PIN   PINA
#define ERROR1_DDR   DDRA
#define ERROR1_BIT   2
#define ERROR1_MASK  (1<<2)
#define ERROR1_IS_PA2

#define ERROR2_PORT  PORTA
#define ERROR2_PIN   PINA
#define ERROR2_DDR   DDRA
#define ERROR2_BIT   3
#define ERROR2_MASK  (1<<3)
#define ERROR2_IS_PA3

#define ERROR3_PORT  PORTA
#define ERROR3_PIN   PINA
#define ERROR3_DDR   DDRA
#define ERROR3_BIT   4
#define ERROR3_MASK  (1<<4)
#define ERROR3_IS_PA4

#define ERROR_BATTERY_PORT  PORTA
#define ERROR_BATTERY_PIN   PINA
#define ERROR_BATTERY_DDR   DDRA
#define ERROR_BATTERY_BIT   3
#define ERROR_BATTERY_MASK  (1<<3)
#define ERROR_BATTERY_IS_PA3

#define ERROR_OIL_PRESSURE_PORT  PORTA
#define ERROR_OIL_PRESSURE_PIN   PINA
#define ERROR_OIL_PRESSURE_DDR   DDRA
#define ERROR_OIL_PRESSURE_BIT   4
#define ERROR_OIL_PRESSURE_MASK  (1<<4)
#define ERROR_OIL_PRESSURE_IS_PA4

#define ERROR_TEMPERATURE_PORT  PORTA
#define ERROR_TEMPERATURE_PIN   PINA
#define ERROR_TEMPERATURE_DDR   DDRA
#define ERROR_TEMPERATURE_BIT   2
#define ERROR_TEMPERATURE_MASK  (1<<2)
#define ERROR_TEMPERATURE_IS_PA2

#define GEAR_A_PORT  PORTB
#define GEAR_A_PIN   PINB
#define GEAR_A_DDR   DDRB
#define GEAR_A_BIT   4
#define GEAR_A_MASK  (1<<4)
#define GEAR_A_IS_PB4

#define GEAR_ANODE_PORT  PORTE
#define GEAR_ANODE_PIN   PINE
#define GEAR_ANODE_DDR   DDRE
#define GEAR_ANODE_BIT   4
#define GEAR_ANODE_MASK  (1<<4)
#define GEAR_ANODE_IS_PE4

#define GEAR_B_PORT  PORTB
#define GEAR_B_PIN   PINB
#define GEAR_B_DDR   DDRB
#define GEAR_B_BIT   3
#define GEAR_B_MASK  (1<<3)
#define GEAR_B_IS_PB3

#define GEAR_C_PORT  PORTE
#define GEAR_C_PIN   PINE
#define GEAR_C_DDR   DDRE
#define GEAR_C_BIT   7
#define GEAR_C_MASK  (1<<7)
#define GEAR_C_IS_PE7

#define GEAR_D_PORT  PORTE
#define GEAR_D_PIN   PINE
#define GEAR_D_DDR   DDRE
#define GEAR_D_BIT   3
#define GEAR_D_MASK  (1<<3)
#define GEAR_D_IS_PE3

#define GEAR_E_PORT  PORTE
#define GEAR_E_PIN   PINE
#define GEAR_E_DDR   DDRE
#define GEAR_E_BIT   2
#define GEAR_E_MASK  (1<<2)
#define GEAR_E_IS_PE2

#define GEAR_F_PORT  PORTB
#define GEAR_F_PIN   PINB
#define GEAR_F_DDR   DDRB
#define GEAR_F_BIT   5
#define GEAR_F_MASK  (1<<5)
#define GEAR_F_IS_PB5

#define GEAR_G_PORT  PORTB
#define GEAR_G_PIN   PINB
#define GEAR_G_DDR   DDRB
#define GEAR_G_BIT   6
#define GEAR_G_MASK  (1<<6)
#define GEAR_G_IS_PB6

#define RPM0_PORT  PORTC
#define RPM0_PIN   PINC
#define RPM0_DDR   DDRC
#define RPM0_BIT   0
#define RPM0_MASK  (1<<0)
#define RPM0_IS_PC0

#define RPM1_PORT  PORTC
#define RPM1_PIN   PINC
#define RPM1_DDR   DDRC
#define RPM1_BIT   1
#define RPM1_MASK  (1<<1)
#define RPM1_IS_PC1

#define RPM2_PORT  PORTC
#define RPM2_PIN   PINC
#define RPM2_DDR   DDRC
#define RPM2_BIT   2
#define RPM2_MASK  (1<<2)
#define RPM2_IS_PC2

#define RPM3_PORT  PORTC
#define RPM3_PIN   PINC
#define RPM3_DDR   DDRC
#define RPM3_BIT   3
#define RPM3_MASK  (1<<3)
#define RPM3_IS_PC3

#define RPM4_PORT  PORTC
#define RPM4_PIN   PINC
#define RPM4_DDR   DDRC
#define RPM4_BIT   4
#define RPM4_MASK  (1<<4)
#define RPM4_IS_PC4

#define RPM5_PORT  PORTC
#define RPM5_PIN   PINC
#define RPM5_DDR   DDRC
#define RPM5_BIT   5
#define RPM5_MASK  (1<<5)
#define RPM5_IS_PC5

#define RPM6_PORT  PORTC
#define RPM6_PIN   PINC
#define RPM6_DDR   DDRC
#define RPM6_BIT   6
#define RPM6_MASK  (1<<6)
#define RPM6_IS_PC6

#define RPM7_PORT  PORTC
#define RPM7_PIN   PINC
#define RPM7_DDR   DDRC
#define RPM7_BIT   7
#define RPM7_MASK  (1<<7)
#define RPM7_IS_PC7

#define RPM_ANODE_PORT  PORTE
#define RPM_ANODE_PIN   PINE
#define RPM_ANODE_DDR   DDRE
#define RPM_ANODE_BIT   6
#define RPM_ANODE_MASK  (1<<6)
#define RPM_ANODE_IS_PE6


#define RPM_OUTPUT()               { DDRC  = 0xff;  }
#define RPM_INPUT()                { DDRC  = 0x00;  }
#define RPM_TOGGLE_INPUT_OUTPUT()  { DDRC  ^= 0xff; }
#define SET_RPM(x)                 { PORTC = x;     }
#define GET_RPM()                  (PINC)


#endif	// not defined PIN_NAMES_H_
