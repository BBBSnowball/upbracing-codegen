package de.upbracing.code_generation.config.rtos;


public class RTOSAlarm {
	private String comment;
	private RTOSTask task;
	private int ticks_per_base;
	private int phase = 0;
	
	/**
	 * @param task
	 * @param ticks_per_base
	 */
	public RTOSAlarm(RTOSTask task, int ticks_per_base) {
		this.task = task;
		this.ticks_per_base = ticks_per_base;
	}

	/**
	 * @return the task
	 */
	public RTOSTask getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(RTOSTask task) {
		this.task = task;
	}

	/**
	 * @return the ticks_per_base
	 */
	public int getTicksPerBase() {
		return ticks_per_base;
	}

	/**
	 * @param ticks_per_base the ticks_per_base to set
	 */
	public void setTicksPerBase(int ticks_per_base) {
		this.ticks_per_base = ticks_per_base;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the phase
	 */
	public int getPhase() {
		if (phase < 0)
			return ticks_per_base;
		else
			return phase;
	}

	/**
	 * @param phase the inv_phase to set
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}
}
