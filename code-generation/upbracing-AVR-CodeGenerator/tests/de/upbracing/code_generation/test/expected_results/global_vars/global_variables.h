/*
 * global_variables.h
 *
 * This file defines thread-safe accessors for global variables.
 *
 * Generated automatically. DO NOT MODIFY! Change config.rb instead.
 */

#ifndef GLOBAL_VARIABLES_H_
#define GLOBAL_VARIABLES_H_

//////////////////////////
///  global variables  ///
//////////////////////////

extern uint8_t       h_2510C39011C5BE704182423E3A695E91;
extern float         xyz_D16FB36F0911F878998C136191AF705E;
extern s16           foo_ACBD18DB4CC2F85CEDEF654FCCC4A4D8;
extern unsigned char bar_37B51D194A7513E45B56F6524F2D51F2;
extern signed long   foobar_3858F62230AC3C915F300C664312C63F;
extern struct PointD abc_900150983CD24FB0D6963F7D28E17F72;
extern struct PointD def_4ED9407630EB1000C0F6B63842DEFA7D;


inline static uint8_t getH() {
	return h_2510C39011C5BE704182423E3A695E91;
}

inline static void setH(uint8_t value) {
	h_2510C39011C5BE704182423E3A695E91 = value;
}

inline static float getXyz() {
	OS_ENTER_CRITICAL();
	volatile float tmp = xyz_D16FB36F0911F878998C136191AF705E;
	OS_EXIT_CRITICAL();
	return tmp;
}

inline static void setXyz(float value) {
	OS_ENTER_CRITICAL();
	xyz_D16FB36F0911F878998C136191AF705E = value;
	OS_EXIT_CRITICAL();
}

inline static s16 getFoo() {
	OS_ENTER_CRITICAL();
	volatile s16 tmp = foo_ACBD18DB4CC2F85CEDEF654FCCC4A4D8;
	OS_EXIT_CRITICAL();
	return tmp;
}

inline static void setFoo(s16 value) {
	OS_ENTER_CRITICAL();
	foo_ACBD18DB4CC2F85CEDEF654FCCC4A4D8 = value;
	OS_EXIT_CRITICAL();
}

inline static unsigned char getBar() {
	return bar_37B51D194A7513E45B56F6524F2D51F2;
}

inline static void setBar(unsigned char value) {
	bar_37B51D194A7513E45B56F6524F2D51F2 = value;
}

inline static signed long getFoobar() {
	OS_ENTER_CRITICAL();
	volatile signed long tmp = foobar_3858F62230AC3C915F300C664312C63F;
	OS_EXIT_CRITICAL();
	return tmp;
}

inline static void setFoobar(signed long value) {
	OS_ENTER_CRITICAL();
	foobar_3858F62230AC3C915F300C664312C63F = value;
	OS_EXIT_CRITICAL();
}

inline static struct PointD getAbc() {
	OS_ENTER_CRITICAL();
	volatile struct PointD tmp = abc_900150983CD24FB0D6963F7D28E17F72;
	OS_EXIT_CRITICAL();
	return tmp;
}

inline static void setAbc(struct PointD value) {
	OS_ENTER_CRITICAL();
	abc_900150983CD24FB0D6963F7D28E17F72 = value;
	OS_EXIT_CRITICAL();
}

inline static struct PointD getDef() {
	OS_ENTER_CRITICAL();
	volatile struct PointD tmp = def_4ED9407630EB1000C0F6B63842DEFA7D;
	OS_EXIT_CRITICAL();
	return tmp;
}

inline static void setDef(struct PointD value) {
	OS_ENTER_CRITICAL();
	def_4ED9407630EB1000C0F6B63842DEFA7D = value;
	OS_EXIT_CRITICAL();
}


#endif	// not defined GLOBAL_VARIABLES_H_
