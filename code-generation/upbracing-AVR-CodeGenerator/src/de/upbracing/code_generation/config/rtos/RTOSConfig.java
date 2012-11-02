package de.upbracing.code_generation.config.rtos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import de.upbracing.code_generation.config.rtos.RTOSConfigValue.ConfigFile;
import de.upbracing.code_generation.config.rtos.RTOSTask.TaskState;

public class RTOSConfig {
	private String processor = "AT90CAN128";
	private long clock = -1;
//	private String conformance_class = null;
	private float tick_frequency = -1;
	
	private int timer_prescaler;
	private int timer_count_to;
	private boolean tick_frequency_valid = false;
	
	private boolean used = false;
	
	private ArrayList<RTOSTask> tasks = new ArrayList<RTOSTask>();
	private ArrayList<RTOSAlarm> alarms = new ArrayList<RTOSAlarm>();
	
	private SortedMap<String, SortedMap<String, RTOSConfigValue>> config_values;
	
	public RTOSConfig() {
		// add idle task
		addTask("Idle", TaskState.READY);
		
		// reset to not used
		used = false;
		
		config_values = new TreeMap<String, SortedMap<String,RTOSConfigValue>>();
		for (RTOSConfigValue value : getAllConfigValues()) {
			String cat_name = value.getCategory();
			SortedMap<String,RTOSConfigValue> category = config_values.get(cat_name);
			if (category == null) {
				category = new TreeMap<String, RTOSConfigValue>();
				config_values.put(cat_name, category);
			}
			
			
			String name = value.getName();
			if (category.containsKey(name))
				throw new RuntimeException("duplicate config value " + name + " in category " + cat_name);
			
			category.put(name, value);
		}
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

//	/**
//	 * @return the conformance_class
//	 */
//	public String getConformanceClass() {
//		return conformance_class;
//	}

//	/**
//	 * @param conformance_class the conformance_class to set
//	 */
//	public void setConformanceClass(String conformance_class) {
//		this.conformance_class = conformance_class;
//		used = true;
//	}

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

	
	/**
	 * Get all RTOSConfigValues using the Service Provider Interface for the
	 * class RTOSConfigValueProvider
	 * 
	 * This will find all variables that are provided by RTOSConfigValueProviders
	 * mentioned in an SPI file somewhere on the class path. The file is called:
	 * META-INF/services/de.upbracing.code_generation.config.rtos.RTOSConfigValueProvider
	 * @return a list of generator instances
	 */
	public static List<RTOSConfigValue> getAllConfigValues() {
		ArrayList<RTOSConfigValue> values = new ArrayList<RTOSConfigValue>();
		
		for (RTOSConfigValueProvider provider : findConfigValueProviders())
			values.addAll(provider.getRTOSConfigValues());
		
		return values;
	}
	
	/**
	 * Load RTOSConfigValueProvider instances via the Service Provider Interface
	 * 
	 * This will find all classes that are mentioned in an SPI file somewhere on the
	 * class path. The file is called:
	 * META-INF/services/de.upbracing.code_generation.config.rtos.RTOSConfigValueProvider
	 * @return a list of generator instances
	 */
	private static ServiceLoader<RTOSConfigValueProvider> findConfigValueProviders() {
		ServiceLoader<RTOSConfigValueProvider> loader
			= ServiceLoader.load(RTOSConfigValueProvider.class);
		return loader;
	}
	
	public SortedSet<String> getConfigValueCategories() {
		return Collections.unmodifiableSortedSet((SortedSet<String>) config_values.keySet());
	}
	
	public SortedMap<String, RTOSConfigValue> getConfigValueCategory(String category) {
		return Collections.unmodifiableSortedMap(config_values.get(category));
	}
	
	public RTOSConfigValue getConfigValue(String category, String name) {
		return config_values.get(category).get(name);
	}
	
	public void setConfigValue(String category, String name, String value) {
		getConfigValue(category, name).setValue(value);
	}

	public void addConfigValues(String indent, StringBuffer stringBuffer,
			ConfigFile file) {
		for (Entry<String, SortedMap<String, RTOSConfigValue>> category : config_values.entrySet()) {
			boolean any_active = false;
			for (RTOSConfigValue cvalue : category.getValue().values()) {
				if (cvalue.getFile() == file) {
					any_active = true;
					break;
				}
			}
			
			if (!any_active)
				continue;
			
			stringBuffer.append("\n" + indent + "// category: " + category.getKey() + "\n");

			for (RTOSConfigValue cvalue : category.getValue().values()) {
				if (cvalue.getFile() != file)
					continue;

				stringBuffer.append("\n");
				if (cvalue.getComment() != null) {
					stringBuffer.append(indent + "// ");
					stringBuffer.append(cvalue.getComment().replaceAll("\n", "\n" + indent + "// "));
					stringBuffer.append("\n");
				}
				
				cvalue.getType().addCode(indent, stringBuffer, cvalue);
			}
		}
	}
}
