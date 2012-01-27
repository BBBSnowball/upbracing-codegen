/*
 * Sdram.h
 *
 * Created: 03.01.2012 17:55:48
 *  Author: peer
 */ 


#ifndef SDRAM_H_
#define SDRAM_H_

//! Base address of SDRAM on board
#define BOARD_EBI_SDRAM_BASE		0x800000UL
#define BOARD_EBI_SDRAM_SIZE	    0x800000UL
//! SDRAM refresh interval in number of CLKper2 cycles (16 us)
#define BOARD_EBI_SDRAM_REFRESH   1023
//! SDRAM initialization delay in number of CLKper2 cycles (100 us)
#define BOARD_EBI_SDRAM_INITDLY   6400

typedef uint32_t sdram_ptr_t;

uint_fast8_t SdramRead8(const sdram_ptr_t from);
uint_fast16_t SdramRead16(const sdram_ptr_t from);
uint_fast32_t SdramRead32(const sdram_ptr_t from);
void SdramWrite8(sdram_ptr_t to, uint_fast8_t val);
void SdramWrite16(sdram_ptr_t to, uint_fast16_t val);
void SdramWrite32(sdram_ptr_t to, uint_fast32_t val);
void SdramInit(void);

#endif /* SDRAM_H_ */