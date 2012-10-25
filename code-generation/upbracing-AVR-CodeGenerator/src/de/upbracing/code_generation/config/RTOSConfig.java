package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.upbracing.code_generation.config.RTOSTask.TaskState;

public class RTOSConfig {
	private String processor = "AT90CAN128";
	private long clock = -1;
	private String conformance_class = null;
	private float tick_frequency = -1;
	
	private int timer_prescaler;
	private int timer_count_to;
	private boolean tick_frequency_valid = false;
	
	private boolean used = false;
	
	private ArrayList<RTOSTask> tasks = new ArrayList<RTOSTask>();
	private ArrayList<RTOSAlarm> alarms = new ArrayList<RTOSAlarm>();
	
	public RTOSConfig() {
		// add idle task
		addTask("Idle", TaskState.READY);
	}
	
	/** Create and add an alarm
	 * 
	 * @param name alarm name
	 * @return the new alarm
	 */
	public RTOSAlarm addAlarm(RTOSTask task, int ticks_per_base) {
		RTOSAlarm alarm = new RTOSAlarm(task, ticks_per_base);
		alarms.add(alarm);
		used = true;
		return alarm;
	}
	
	/** Create and add a task
	 * 
	 * @param name task name
	 * @return the new task
	 */
	public RTOSTask addTask(String name) {
		RTOSTask task = new RTOSTask(name);
		tasks.add(task);
		used = true;
		return task;
	}
	
	/** Create and add a task
	 * 
	 * @param name task name
	 * @return the new task
	 */
	public RTOSTask addTask(String name, int ticks_per_base) {
		RTOSTask task = addTask(name);
		alarms.add(task.setAlarm(ticks_per_base));
		used = true;
		return task;
	}
	
	/** Create and add a task
	 * 
	 * @param name task name
	 * @param initial_state initial state of the task (ready or suspended)
	 * @return the new task
	 */
	public RTOSTask addTask(String name, TaskState initial_state) {
		RTOSTask task = addTask(name);
		switch (initial_state) {
		case READY:
			task.setReady(true);
			break;
		case SUSPENDED:
			task.setReady(false);
			break;
		}
		return task;
	}
	
	/** Create and add a task
	 * 
	 * @param name task name
	 * @param initial_state initial state of the task (ready or suspended)
	 * @return the new task
	 */
	public RTOSTask addTask(String name, TaskState initial_state, int ticks_per_base) {
		RTOSTask task = addTask(name, initial_state);
		alarms.add(task.setAlarm(ticks_per_base));
		return task;
	}

	/**
	 * @return whether the OS configuration will be generated
	 */
	public boolean isUsed() {
		return used;
	}

	/**
	 * @param used whether the OS configuration should be generated
	 */
	public void setUsed(boolean used) {
		this.used = used;
	}

	/**
	 * @return the tasks
	 */
	public ArrayList<RTOSTask> getTasks() {
		return tasks;
	}

	/**
	 * @return the alarms
	 */
	public ArrayList<RTOSAlarm> getAlarms() {
		return alarms;
	}

	/**
	 * @return the processor type
	 */
	public String getProcessor() {
		return processor;
	}

	/**
	 * @param processor the processor type to set
	 */
	public void setProcessor(String processor) {
		this.processor = processor;
		used = true;
	}

	/**
	 * @return the clock
	 */
	public long getClock() {
		return clock;
	}

	/**
	 * @param clock the clock to set
	 */
	public void setClock(long clock) {
		this.clock = clock;
		used = true;
		updateTimerSettings();
	}

	/**
	 * @return the conformance_class
	 */
	public String getConformanceClass() {
		return conformance_class;
	}

	/**
	 * @param conformance_class the conformance_class to set
	 */
	public void setConformanceClass(String conformance_class) {
		this.conformance_class = conformance_class;
		used = true;
	}

	/**
	 * @return the tick_frequency
	 */
	public float getTickFrequency() {
		return tick_frequency;
	}

	/**
	 * @param tick_frequency the tick_frequency to set
	 */
	public void setTickFrequency(float tick_frequency) {
		this.tick_frequency = tick_frequency;
		used = true;
		updateTimerSettings();
	}
	
	/**
	 * @return whether the chosen tick frequency can be used
	 */
	public boolean isTickFrequencyValid() {
		return tick_frequency_valid;
	}

	/**
	 * @return the timer_prescaler
	 */
	public int getTimerPrescaler() {
		return timer_prescaler;
	}

	/**
	 * @return the timer_count_to
	 */
	public int getTimerCountTo() {
		return timer_count_to;
	}

	/** Set IDs for all tasks according to their position in the list
	 */
	public void updateTaskIDs() {
		for (int i=0;i<tasks.size();i++)
			tasks.get(i).id = i;
	}
	
	/** find a prescaler and timer top (value for the compare register)
	 * to achieve the chosen timer tick rate
	 */
	private void updateTimerSettings() {
		if (clock <= 0 || tick_frequency <= 0) {
			tick_frequency_valid = false;
			return;
		}
		
		if (tick_frequency > clock) {
			timer_prescaler = 1;
			timer_count_to = 0;
			tick_frequency_valid = false;
			return;
		}
		
		Iterable<Integer> prescalers = getSystemTimerPrescalers();
		if (prescalers == null) {
			// processor setting invalid or not supported
			tick_frequency_valid = false;
			return;
		}
		
		
		// freq = clock / (2*prescaler*(1+ocr_value))
		// -> ocr_value = clock / (freq * 2*prescaler) - 1
		for (int prescaler : prescalers) {
			int ocr_value = Math.round(clock / (tick_frequency * 2 * prescaler) - 1);
			if (ocr_value == 0x10000)
				ocr_value = 0xffff;
			if (ocr_value <= 0xffff) {
				timer_prescaler = prescaler;
				timer_count_to = ocr_value;
				tick_frequency_valid = true;
				break;
			}
		}
	}
	
	/** calculate the timer frequency that will be used
	 * 
	 * The frequency should be equal to the one returned by {@link #getRealTickFrequency()}, but
	 * due to the limitations of the processor it may be different. Nevertheless, it should be
	 * quite close to the chosen frequency.
	 * @return the real timer frequency
	 */
	public float getRealTickFrequency() {
		return clock / (2*timer_prescaler*(1+timer_count_to));
	}
	
	/** get valid prescaler values for the chosen processor
	 * @return a list of prescaler values in ascending order or null, if the processor is not supported
	 */
	private Iterable<Integer> getSystemTimerPrescalers() {
		if (processor.equals("AT90CAN32")
				|| processor.equals("AT90CAN64")
				|| processor.equals("AT90CAN128"))
			return Collections.unmodifiableList(
					Arrays.asList(1, 8, 64, 256, 1024));
		else
			return null;
	}
}
