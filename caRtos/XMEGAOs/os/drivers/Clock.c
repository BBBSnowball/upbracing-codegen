/*
 * Clock.c
 *
 * Created: 21.12.2011 16:25:07
 *  Author: peer
 */ 

#include "Clock.h"

//void ClockInit(void)
//{
    ///* Set Oscillator to 32Mhz */
	//OSC.CTRL |= OSC_RC32MEN_bm;
	///* Wait for 32MHz Oscillator to get stable */
	//while(!(OSC.STATUS & OSC_RC32MRDY_bm));
	///* I/O Protection */
	//CCP = CCP_IOREG_gc;
	///* Use 32MHz Oscillator as system clock */
	//CLK.CTRL = CLK_SCLKSEL_RC32M_gc;
	//
	//DFLLRC32M.CTRL = DFLL_ENABLE_bm;
//}