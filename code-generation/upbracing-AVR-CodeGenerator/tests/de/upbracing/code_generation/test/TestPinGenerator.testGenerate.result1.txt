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

#define ABC_PORT  PORTA
#define ABC_PIN   PINA
#define ABC_DDR   DDRA
#define ABC_BIT   2
#define ABC_MASK  (1<<2)
#define ABC_IS_PA2

#define BAR_PORT  PORTA
#define BAR_PIN   PINA
#define BAR_DDR   DDRA
#define BAR_BIT   3
#define BAR_MASK  (1<<3)
#define BAR_IS_PA3

#define FOO_PORT  PORTC
#define FOO_PIN   PINC
#define FOO_DDR   DDRC
#define FOO_BIT   0
#define FOO_MASK  (1<<0)
#define FOO_IS_PC0

#define RPM0_PORT  PORTD
#define RPM0_PIN   PIND
#define RPM0_DDR   DDRD
#define RPM0_BIT   0
#define RPM0_MASK  (1<<0)
#define RPM0_IS_PD0

#define RPM1_PORT  PORTD
#define RPM1_PIN   PIND
#define RPM1_DDR   DDRD
#define RPM1_BIT   1
#define RPM1_MASK  (1<<1)
#define RPM1_IS_PD1

#define RPM2_PORT  PORTD
#define RPM2_PIN   PIND
#define RPM2_DDR   DDRD
#define RPM2_BIT   2
#define RPM2_MASK  (1<<2)
#define RPM2_IS_PD2

#define RPM3_PORT  PORTD
#define RPM3_PIN   PIND
#define RPM3_DDR   DDRD
#define RPM3_BIT   3
#define RPM3_MASK  (1<<3)
#define RPM3_IS_PD3

#define RPM4_PORT  PORTD
#define RPM4_PIN   PIND
#define RPM4_DDR   DDRD
#define RPM4_BIT   4
#define RPM4_MASK  (1<<4)
#define RPM4_IS_PD4

#define RPM5_PORT  PORTD
#define RPM5_PIN   PIND
#define RPM5_DDR   DDRD
#define RPM5_BIT   5
#define RPM5_MASK  (1<<5)
#define RPM5_IS_PD5

#define RPM6_PORT  PORTD
#define RPM6_PIN   PIND
#define RPM6_DDR   DDRD
#define RPM6_BIT   6
#define RPM6_MASK  (1<<6)
#define RPM6_IS_PD6

#define RPM7_PORT  PORTD
#define RPM7_PIN   PIND
#define RPM7_DDR   DDRD
#define RPM7_BIT   7
#define RPM7_MASK  (1<<7)
#define RPM7_IS_PD7

#define X_PORT  PORTB
#define X_PIN   PINB
#define X_DDR   DDRB
#define X_BIT   2
#define X_MASK  (1<<2)
#define X_IS_PB2

#define Y_PORT  PORTB
#define Y_PIN   PINB
#define Y_DDR   DDRB
#define Y_BIT   3
#define Y_MASK  (1<<3)
#define Y_IS_PB3

#define Z_PORT  PORTB
#define Z_PIN   PINB
#define Z_DDR   DDRB
#define Z_BIT   4
#define Z_MASK  (1<<4)
#define Z_IS_PB4


#define RPM_OUTPUT()               { DDRD  = 0xff;  }
#define RPM_INPUT()                { DDRD  = 0x00;  }
#define RPM_TOGGLE_INPUT_OUTPUT()  { DDRD  ^= 0xff; }
#define SET_RPM(x)                 { PORTD = x;     }
#define GET_RPM()                  (PIND)


#endif	// not defined PIN_NAMES_H_
