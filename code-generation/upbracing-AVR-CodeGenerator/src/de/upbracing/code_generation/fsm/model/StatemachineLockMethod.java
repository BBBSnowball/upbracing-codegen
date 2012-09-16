package de.upbracing.code_generation.fsm.model;

public enum StatemachineLockMethod {
	/** do not use any lock */
	NO_LOCK,
	
	/** use lock provided by the OS: OS_{ENTER,EXIT}_CRITICAL
	 *
	 * In the current implementation this behaves like
	 * INTERRUPT, but the semantics are different (cooperate
	 * with OS vs disable interrupts).
	 */
	OS,
	
	/** use a semaphore */
	SEMAPHORE,
	
	/** disable interrupts
	 * 
	 * You must use this type of locking, if any part of the
	 * statemachine might be called by an interrupt.
	 */
	INTERRUPT,
	
	/** custom lock functions
	 * 
	 * You must use a GlobalCode box to provide (define or
	 * include) those functions ($name is the name of your
	 * statemachine):
	 * - $name_enter_critical()
	 * - $name_exit_critical()
	 *
	 * The enter function must make sure that no function of
	 * the statemachine can be called by any part of the
	 * program. This means that it must disable all interrupts
	 * that can call such a function.
	 *  
	 * The exit function should reverse the effects of the
	 * enter function.
	 * 
	 * Do not make
	 * any assumptions about when and how often those functions
	 * will be called. Nested calls must work. This means: The
	 * enter function can be called several times in a row,
	 * before exit is called. The lock must remain active,
	 * until exit has been called for each of the enter calls.
	 * 
	 * You may use macros instead of functions. In that case,
	 * you can take advantage of the fact that enter and exit
	 * functions will always be called in the same scope. You
	 * may declare local variables in the enter macro.
	 */
	CUSTOM
}
