/*
 * Sdram.c
 *
 * Created: 03.01.2012 17:56:12
 *  Author: peer
 */ 

#include <avr/io.h>
#include "Sdram.h"

//uint_fast8_t SdramRead8(const sdram_ptr_t from)
//{
    //uint8_t value;
//
    //asm volatile(
        //"movw r30, %A1 \n\t"
        //"out %2, %C1 \n\t"
        //"ld %0, Z \n\t"
        //"out %2, __zero_reg__ \n\t"
        //: "=r"(value)
        //: "r"(from), "i"(&RAMPZ)
        //: "r30", "r31"
    //);
//
    //return value;
//}
//
//uint_fast16_t SdramRead16(sdram_ptr_t from)
//{
	//uint16_t value;
//
    //asm(
        //"movw r30, %A1 \n\t"
        //"out %2, %C1 \n\t"
        //"ld %A0, Z+ \n\t"
        //"ld %B0, Z \n\t"
        //"out %2, __zero_reg__ \n\t"
        //: "=r"(value)
        //: "r"(from), "i"(&RAMPZ)
        //: "r30", "r31"
    //);
//
    //return value;
//}
//
//uint_fast32_t SdramRead32(sdram_ptr_t from)
//{
	//uint32_t value;
//
    //asm(
        //"movw r30, %A1 \n\t"
        //"out %2, %C1 \n\t"
        //"ld %A0, Z+ \n\t"
        //"ld %B0, Z+ \n\t"
        //"ld %C0, Z+ \n\t"
        //"ld %D0, Z \n\t"
        //"out %2, __zero_reg__ \n\t"
        //: "=r"(value)
        //: "r"(from), "i"(&RAMPZ)
        //: "r30", "r31"
    //);
//
    //return value;
//}
//
//void SdramWrite8(sdram_ptr_t to, uint_fast8_t val)
//{
    //asm volatile(
        //"movw r30, %A0 \n\t"
        //"out %2, %C0 \n\t"
        //"st Z, %1 \n\t"
        //"out %2, __zero_reg__ \n\t"
        //:
        //: "r"(to), "r"(val), "i"(&RAMPZ)
        //: "r30", "r31"
    //);
//}
//
//void SdramWrite16(sdram_ptr_t to, uint_fast16_t val)
//{
    //asm(
        //"movw r30, %A0 \n\t"
        //"out %2, %C0 \n\t"
        //"st Z+, %A1 \n\t"
        //"st Z, %B1 \n\t"
        //"out %2, __zero_reg__ \n\t"
        //:
        //: "r"(to), "r"(val), "i"(&RAMPZ)
        //: "r30", "r31"
    //);
//}
//
//void SdramWrite32(sdram_ptr_t to, uint_fast32_t val)
//{
    //asm(
        //"movw r30, %A0 \n\t"
        //"out %2, %C0 \n\t"
        //"st Z+, %A1 \n\t"
        //"st Z+, %B1 \n\t"
        //"st Z+, %C1 \n\t"
        //"st Z, %D1 \n\t"
        //"out %2, __zero_reg__ \n\t"
        //:
        //: "r"(to), "r"(val), "i"(&RAMPZ)
        //: "r30", "r31"
    //);
//}

//void SdramInit(void)
//{
	//// RAM Spielereien
	//EBI.CTRL = EBI_IFMODE_3PORT_gc;	// SDRAM in 3Port mode
		//PORTH.OUT = 0x0f;
		//PORTH.DIR = 0xff;
		//PORTJ.DIR = 0xf0;
		//PORTK.DIR = 0xff;
	//EBI.SDRAMCTRLA = EBI_SDCAS_bm | EBI_SDROW_bm | EBI_SDCOL_10BIT_gc; // 12bit rows, 10 bit cols
	//EBI.SDRAMCTRLB = EBI_MRDLY_2CLK_gc | EBI_ROWCYCDLY_7CLK_gc | EBI_RPDLY_7CLK_gc; // Mode register delay 3clk, refresh->activate delay 2clk, Precharge 2clk
	//EBI.SDRAMCTRLC = EBI_WRDLY_1CLK_gc | EBI_ESRDLY_7CLK_gc | EBI_ROWCOLDLY_7CLK_gc;
	//EBI.REFRESH = BOARD_EBI_SDRAM_REFRESH;
	//EBI.INITDLY = BOARD_EBI_SDRAM_INITDLY;
	//EBI.CS3.BASEADDR = (BOARD_EBI_SDRAM_BASE >> 8) & 0xfff0;
	//EBI.CS3.CTRLA = EBI_CS_ASIZE_8MB_gc | EBI_CS_MODE_SDRAM_gc; 
	//while(!(EBI.CS3.CTRLB & EBI_CS_SDINITDONE_bm));
//}