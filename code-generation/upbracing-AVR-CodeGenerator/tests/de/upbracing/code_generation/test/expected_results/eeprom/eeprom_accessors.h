/*
 * eeprom_accessors.h
 *
 * This file declares accessors for values in non-volatile memory (EEPROM).
 *
 * Generated automatically. DO NOT MODIFY! Change config.rb instead.
 */

#ifndef EEPROM_DATA_H_
#define EEPROM_DATA_H_

/////////////////////////
///    EEPROM data    ///
/////////////////////////

// only define EEPROM stuff, if avr/eeprom.h has been included
#ifdef EEMEM

//#pragma pack(push, 1)
typedef struct {
	uint8_t       wdt_reset_count;
	float         xyz;
	s16           foo;
	unsigned char bar;
	signed long   foobar;
	struct PointD abc;
	struct PointD def;
} EEPROMDATA;
//#pragma pack(pop)

extern EEPROMDATA eeprom_data EEMEM NO_UNUSED_WARNING_PLEASE;


#undef  EEPROM_POINTER
#define EEPROM_POINTER(name) &eeprom_data.name

#define READ_WDT_RESET_COUNT() eeprom_read_byte(EEPROM_POINTER(wdt_reset_count))
#define WRITE_WDT_RESET_COUNT(value) eeprom_write_byte(EEPROM_POINTER(wdt_reset_count), value)
#define READ_XYZ() (float)eeprom_read_dword(EEPROM_POINTER(xyz))
#define WRITE_XYZ(value) eeprom_write_dword(EEPROM_POINTER(xyz), (uint32_t)(value))
#define READ_FOO() (s16)eeprom_read_word(EEPROM_POINTER(foo))
#define WRITE_FOO(value) eeprom_write_word(EEPROM_POINTER(foo), (uint16_t)(value))
#define READ_BAR() (unsigned char)eeprom_read_byte(EEPROM_POINTER(bar))
#define WRITE_BAR(value) eeprom_write_byte(EEPROM_POINTER(bar), (uint8_t)(value))
#define READ_FOOBAR() (signed long)eeprom_read_dword(EEPROM_POINTER(foobar))
#define WRITE_FOOBAR(value) eeprom_write_dword(EEPROM_POINTER(foobar), (uint32_t)(value))
#define READ_ABC(dst) (struct PointD)eeprom_read_block(dst, EEPROM_POINTER(abc), 16)
#define WRITE_ABC(value) eeprom_write_block(EEPROM_POINTER(abc), value, 16)
#define READ_DEF(dst) (struct PointD)eeprom_read_block(dst, EEPROM_POINTER(def), 16)
#define WRITE_DEF(value) eeprom_write_block(EEPROM_POINTER(def), value, 16)

#endif	// defined EEMEM

#endif	// not defined EEPROM_DATA_H_
