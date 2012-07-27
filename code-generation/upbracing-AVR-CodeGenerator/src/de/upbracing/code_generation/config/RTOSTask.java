package de.upbracing.code_generation.config;

public class RTOSTask {
	public enum TaskState { READY, SUSPENDED };
	
	private String name;
	//private int topOfStack = -1;
	//private int baseOfStack = -1;
	private int stackSize = 0x200;
	private boolean ready = false;
	private boolean preemptable = true;
	
	protected int id;
	private RTOSAlarm alarm;
	
	/** constructor
	 * @param name task name
	 */
	public RTOSTask(String name) {
		this.name = name;
	}

	/** Get task name
	 * 
	 * @return task name
	 */
	public String getName() {
		return name;
	}

	/** Set task name
	 * 
	 * @param name the task name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** Get top of stack
	 * 
	 * @return address of the first byte after the stack
	 */
	/*public int getTopOfStack() {
		return topOfStack;
	}*/

	/** Set top of stack
	 * 
	 * @param topOfStack address of the first byte after the stack
	 */
	/*public void setTopOfStack(int topOfStack) {
		this.topOfStack = topOfStack;
	}*/

	/*public int getBaseOfStack() {
		return baseOfStack;
	}

	public void setBaseOfStack(int baseOfStack) {
		this.baseOfStack = baseOfStack;
	}*/

	/** Get stack size
	 * 
	 * @return stack size
	 */
	public int getStackSize() {
		return stackSize;
	}

	/** Set stack size
	 * 
	 * @param stackSize new stack size
	 */
	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	/** Does this task start in the READY state?
	 * 
	 * @return true, if the task is READY; false, if it is SUSPENDED
	 */
	public boolean isReady() {
		return ready;
	}

	/** Set, whether this task starts in the READY state
	 * 
	 * @param ready true, if the task is READY; false, if it is SUSPENDED
	 */
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	/** Is this task preemptable?
	 * 
	 * @return whether the task is preemptable
	 */
	public boolean isPreemptable() {
		return preemptable;
	}

	/** Set whether the task is preemptable
	 * 
	 * @param preemptable true, if the task is preemptable
	 */
	public void setPreemptable(boolean preemptable) {
		this.preemptable = preemptable;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the alarm
	 */
	public RTOSAlarm getAlarm() {
		return alarm;
	}

	/**
	 * @param alarm the alarm to set
	 */
	public void setAlarm(RTOSAlarm alarm) {
		this.alarm = alarm;
	}

	/** create and set the alarm for this task
	 * 
	 * @param ticks_per_base divider for the os tick timer (5 means run the alarm on every 5th tick)
	 * @return the new alarm
	 */
	public RTOSAlarm setAlarm(int ticks_per_base) {
		RTOSAlarm alarm = new RTOSAlarm(this, ticks_per_base);
		alarm.setComment("Alarm for Task_" + this.getName());
		setAlarm(alarm);
		return alarm;
	}
}
