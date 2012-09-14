/*
 * eeprom_accessors.c
 *
 * This file declares accessors for values in non-volatile memory (EEPROM).
 *
 * Generated automatically. DO NOT MODIFY! Change config.rb instead.
 */

#include "eeprom_accessors.h"

EEPROMDATA eeprom_data EEMEM NO_UNUSED_WARNING_PLEASE = {
	17,                          // wdt_reset_count
	1.75,                        // xyz
	0x4242,                      // foo
	(unsigned char)(uint64_t)-1, // bar
	(signed long)(uint64_t)-1,   // foobar
	(struct PointD)(uint64_t)-1, // abc
	{1,2},                       // def
};
